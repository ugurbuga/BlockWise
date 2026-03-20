package com.ugurbuga.blockwise.blocklogic.domain

import kotlin.random.Random

data class Piece(
    val id: Long = Random.nextLong(),
    val shape: Shape,
    val color: BlockColor,
)

data class Shape(
    val cells: List<CellOffset>,
) {
    init {
        require(cells.isNotEmpty())
        require(cells.distinct().size == cells.size)
    }
}

data class CellOffset(
    val dx: Int,
    val dy: Int,
)

private fun shapeOf(vararg cells: Pair<Int, Int>): Shape {
    require(cells.isNotEmpty())
    val minDx = cells.minOf { it.first }
    val minDy = cells.minOf { it.second }
    val normalized = cells
        .map { (dx, dy) -> CellOffset(dx - minDx, dy - minDy) }
        .distinct()
        .sortedWith(compareBy<CellOffset> { it.dy }.thenBy { it.dx })
    return Shape(normalized)
}

object Shapes {
    val Single = shapeOf(0 to 0)
    val Line2H = shapeOf(0 to 0, 1 to 0)
    val Line2V = shapeOf(0 to 0, 0 to 1)
    val Line3H = shapeOf(0 to 0, 1 to 0, 2 to 0)
    val Line3V = shapeOf(0 to 0, 0 to 1, 0 to 2)
    val Line4H = shapeOf(0 to 0, 1 to 0, 2 to 0, 3 to 0)
    val Line4V = shapeOf(0 to 0, 0 to 1, 0 to 2, 0 to 3)

    val Square2 = shapeOf(0 to 0, 1 to 0, 0 to 1, 1 to 1)
    val Square3 = shapeOf(
        0 to 0, 1 to 0, 2 to 0,
        0 to 1, 1 to 1, 2 to 1,
        0 to 2, 1 to 2, 2 to 2,
    )
    val Rect3x2 = shapeOf(
        0 to 0, 1 to 0, 2 to 0,
        0 to 1, 1 to 1, 2 to 1,
    )
    val Rect2x3 = shapeOf(
        0 to 0, 1 to 0,
        0 to 1, 1 to 1,
        0 to 2, 1 to 2,
    )
    val Rect4x2 = shapeOf(
        0 to 0, 1 to 0, 2 to 0, 3 to 0,
        0 to 1, 1 to 1, 2 to 1, 3 to 1,
    )
    val Rect2x4 = shapeOf(
        0 to 0, 1 to 0,
        0 to 1, 1 to 1,
        0 to 2, 1 to 2,
        0 to 3, 1 to 3,
    )

    val L3 = shapeOf(0 to 0, 0 to 1, 1 to 1)
    val L3TopRight = shapeOf(1 to 0, 0 to 1, 1 to 1)
    val L3BottomLeft = shapeOf(0 to 0, 1 to 0, 0 to 1)
    val L3BottomRight = shapeOf(0 to 0, 1 to 0, 1 to 1)

    val T4 = shapeOf(0 to 0, 1 to 0, 2 to 0, 1 to 1)
    val T4Down = shapeOf(1 to 0, 0 to 1, 1 to 1, 2 to 1)
    val T4Left = shapeOf(1 to 0, 0 to 1, 1 to 1, 1 to 2)
    val T4Right = shapeOf(0 to 0, 0 to 1, 1 to 1, 0 to 2)

    val L4TallRight = shapeOf(0 to 0, 0 to 1, 0 to 2, 1 to 2)
    val L4TallLeft = shapeOf(1 to 0, 1 to 1, 1 to 2, 0 to 2)
    val L4WideDown = shapeOf(0 to 0, 1 to 0, 2 to 0, 0 to 1)
    val L4WideUp = shapeOf(0 to 0, 1 to 0, 2 to 0, 2 to 1)
    val L5TallRight = shapeOf(0 to 0, 0 to 1, 0 to 2, 0 to 3, 1 to 3)
    val L5TallLeft = shapeOf(1 to 0, 1 to 1, 1 to 2, 1 to 3, 0 to 3)
    val L5WideDown = shapeOf(0 to 0, 1 to 0, 2 to 0, 3 to 0, 0 to 1)
    val L5WideUp = shapeOf(0 to 0, 1 to 0, 2 to 0, 3 to 0, 3 to 1)

    val S4Horizontal = shapeOf(1 to 0, 2 to 0, 0 to 1, 1 to 1)
    val S4Vertical = shapeOf(0 to 0, 0 to 1, 1 to 1, 1 to 2)
    val Z4Horizontal = shapeOf(0 to 0, 1 to 0, 1 to 1, 2 to 1)
    val Z4Vertical = shapeOf(1 to 0, 0 to 1, 1 to 1, 0 to 2)

    val Plus5 = shapeOf(1 to 0, 0 to 1, 1 to 1, 2 to 1, 1 to 2)
    val T5WideDown = shapeOf(1 to 0, 0 to 1, 1 to 1, 2 to 1, 3 to 1)
    val T5WideUp = shapeOf(0 to 0, 1 to 0, 2 to 0, 3 to 0, 1 to 1)

    val Level1: List<Shape> = listOf(
        Single,
        Line2H,
        Line2V,
    )

    val Level2: List<Shape> = listOf(
        Line3H,
        Line3V,
        Square2,
        L3,
        L3TopRight,
        L3BottomLeft,
        L3BottomRight,
    )

    val Level3: List<Shape> = listOf(
        Square3,
        Rect3x2,
        Rect2x3,
        T4,
        T4Down,
        T4Left,
        T4Right,
        L4TallRight,
        L4TallLeft,
        L4WideDown,
        L4WideUp,
        S4Horizontal,
        S4Vertical,
        Z4Horizontal,
        Z4Vertical,
        Plus5,
    )

    val Level4: List<Shape> = listOf(
        Line4H,
        Line4V,
        Rect4x2,
        Rect2x4,
        L5TallRight,
        L5TallLeft,
        L5WideDown,
        L5WideUp,
        T5WideDown,
        T5WideUp,
    )

    val All: List<Shape> = listOf(
        Single,
        Line2H,
        Line2V,
        Line3H,
        Line3V,
        Line4H,
        Line4V,
        Square2,
        Square3,
        Rect3x2,
        Rect2x3,
        Rect4x2,
        Rect2x4,
        L3,
        L3TopRight,
        L3BottomLeft,
        L3BottomRight,
        T4,
        T4Down,
        T4Left,
        T4Right,
        L4TallRight,
        L4TallLeft,
        L4WideDown,
        L4WideUp,
        L5TallRight,
        L5TallLeft,
        L5WideDown,
        L5WideUp,
        S4Horizontal,
        S4Vertical,
        Z4Horizontal,
        Z4Vertical,
        Plus5,
        T5WideDown,
        T5WideUp,
    )

    fun forComplexity(maxLevel: Int): List<Shape> {
        return buildList {
            if (maxLevel >= 1) addAll(Level1)
            if (maxLevel >= 2) addAll(Level2)
            if (maxLevel >= 3) addAll(Level3)
            if (maxLevel >= 4) addAll(Level4)
        }.distinct()
    }

    fun forMaxDimension(maxDimension: Int): List<Shape> {
        return All.filter { shape ->
            val width = shape.cells.maxOf { it.dx } + 1
            val height = shape.cells.maxOf { it.dy } + 1
            width <= maxDimension && height <= maxDimension
        }
    }
}
