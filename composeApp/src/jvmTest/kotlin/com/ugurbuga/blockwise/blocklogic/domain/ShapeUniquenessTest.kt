package com.ugurbuga.blockwise.blocklogic.domain

import kotlin.test.Test
import kotlin.test.assertTrue

class ShapeUniquenessTest {

    @Test
    fun `all shapes should be unique up to reflection`() {
        val shapesByName = Shapes::class.java.declaredFields
            .filter { field ->
                field.type == Shape::class.java
            }
            .associate { field ->
                field.isAccessible = true
                val value = field.get(Shapes) as Shape
                value to field.name
            }

        val signatures = mutableMapOf<String, MutableList<String>>()

        Shapes.All.forEachIndexed { index, shape ->
            val name = shapesByName[shape] ?: "All[$index]"
            val signature = canonicalReflectionSignature(shape)
            signatures.getOrPut(signature) { mutableListOf() }.add(name)
        }

        val duplicates = signatures
            .values
            .filter { it.size > 1 }
            .sortedByDescending { it.size }

        assertTrue(
            actual = duplicates.isEmpty(),
            message = buildString {
                appendLine("Found reflection-duplicate shapes:")
                duplicates.forEach { group ->
                    appendLine(group.joinToString(prefix = "- ", separator = ", "))
                }
            }
        )
    }

    private fun canonicalReflectionSignature(shape: Shape): String {
        val base = shape.cells.map { it.dx to it.dy }
        val mirrored = mirrorX(base)
        return minOf(
            normalize(base),
            normalize(mirrored),
        )
    }

    private fun mirrorX(points: List<Pair<Int, Int>>): List<Pair<Int, Int>> {
        // Mirror across vertical axis: (x,y) -> (-x, y)
        return points.map { (x, y) -> -x to y }
    }

    private fun normalize(points: List<Pair<Int, Int>>): String {
        val minX = points.minOf { it.first }
        val minY = points.minOf { it.second }
        return points
            .map { (x, y) -> (x - minX) to (y - minY) }
            .sortedWith(compareBy({ it.second }, { it.first }))
            .joinToString(separator = ";") { (x, y) -> "$x,$y" }
    }
}
