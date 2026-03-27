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
    val Rect4x2 = shapeOf(
        0 to 0, 1 to 0, 2 to 0, 3 to 0,
        0 to 1, 1 to 1, 2 to 1, 3 to 1,
    )

    val L3 = shapeOf(0 to 0, 0 to 1, 1 to 1)

    val T4 = shapeOf(0 to 0, 1 to 0, 2 to 0, 1 to 1)

    val L4TallRight = shapeOf(0 to 0, 0 to 1, 0 to 2, 1 to 2)
    val L5TallRight = shapeOf(0 to 0, 0 to 1, 0 to 2, 0 to 3, 1 to 3)

    val Z4Horizontal = shapeOf(0 to 0, 1 to 0, 1 to 1, 2 to 1)

    val T5WideDown = shapeOf(1 to 0, 0 to 1, 1 to 1, 2 to 1, 3 to 1)

    // Additional 3x3 shapes
    val Cross3 = shapeOf(1 to 0, 0 to 1, 1 to 1, 2 to 1, 1 to 2)
    val Diagonal3 = shapeOf(0 to 0, 1 to 1, 2 to 2)
    val X3 = shapeOf(0 to 0, 1 to 1, 2 to 2, 2 to 0, 0 to 2)
    val Step3 = shapeOf(0 to 0, 1 to 0, 1 to 1, 2 to 1, 2 to 2)
    val S3 = shapeOf(0 to 0, 1 to 0, 1 to 1, 2 to 1, 2 to 2)
    val Corner3 = shapeOf(0 to 0, 1 to 0, 0 to 1, 0 to 2)
    val V3 = shapeOf(0 to 0, 0 to 1, 1 to 2, 2 to 2)
    val A3 = shapeOf(1 to 0, 0 to 1, 1 to 1, 2 to 1, 0 to 2, 2 to 2)

    // 4x4 shapes
    val Square4 = shapeOf(
        0 to 0, 1 to 0, 2 to 0, 3 to 0,
        0 to 1, 1 to 1, 2 to 1, 3 to 1,
        0 to 2, 1 to 2, 2 to 2, 3 to 2,
        0 to 3, 1 to 3, 2 to 3, 3 to 3,
    )
    val Rect4x3 = shapeOf(
        0 to 0, 1 to 0, 2 to 0, 3 to 0,
        0 to 1, 1 to 1, 2 to 1, 3 to 1,
        0 to 2, 1 to 2, 2 to 2, 3 to 2,
    )
    val U4 = shapeOf(0 to 0, 2 to 0, 0 to 1, 1 to 1, 2 to 1, 0 to 2, 2 to 2)
    val C4 = shapeOf(1 to 0, 2 to 0, 0 to 1, 1 to 1, 2 to 1, 0 to 2, 1 to 2, 2 to 2)
    val Corner4 = shapeOf(0 to 0, 1 to 0, 2 to 0, 0 to 1, 0 to 2, 0 to 3)
    val Diagonal4 = shapeOf(0 to 0, 1 to 1, 2 to 2, 3 to 3)
    val X4 = shapeOf(0 to 0, 1 to 1, 2 to 2, 3 to 3, 3 to 0, 2 to 1, 1 to 2, 0 to 3)
    val Z4Large = shapeOf(0 to 0, 1 to 0, 1 to 1, 2 to 1, 2 to 2, 3 to 2)
    val S4Large = shapeOf(1 to 0, 2 to 0, 0 to 1, 1 to 1, 2 to 1, 3 to 1, 3 to 2)
    val H4 = shapeOf(0 to 0, 0 to 1, 1 to 1, 2 to 1, 3 to 1, 0 to 2, 3 to 2)
    val Pyramid4 = shapeOf(1 to 0, 0 to 1, 1 to 1, 2 to 1, 0 to 2, 2 to 2)
    val Trident4 = shapeOf(0 to 0, 2 to 0, 0 to 1, 1 to 1, 2 to 1, 0 to 2, 2 to 2, 1 to 3)

    // 5x5 shapes
    val Square5 = shapeOf(
        0 to 0, 1 to 0, 2 to 0, 3 to 0, 4 to 0,
        0 to 1, 1 to 1, 2 to 1, 3 to 1, 4 to 1,
        0 to 2, 1 to 2, 2 to 2, 3 to 2, 4 to 2,
        0 to 3, 1 to 3, 2 to 3, 3 to 3, 4 to 3,
        0 to 4, 1 to 4, 2 to 4, 3 to 4, 4 to 4,
    )
    val Rect5x4 = shapeOf(
        0 to 0, 1 to 0, 2 to 0, 3 to 0, 4 to 0,
        0 to 1, 1 to 1, 2 to 1, 3 to 1, 4 to 1,
        0 to 2, 1 to 2, 2 to 2, 3 to 2, 4 to 2,
        0 to 3, 1 to 3, 2 to 3, 3 to 3, 4 to 3,
    )
    val U5 = shapeOf(0 to 0, 1 to 0, 2 to 0, 3 to 0, 4 to 0, 0 to 1, 4 to 1, 0 to 2, 4 to 2, 0 to 3, 4 to 3)
    val C5 = shapeOf(1 to 0, 2 to 0, 3 to 0, 4 to 0, 0 to 1, 1 to 1, 2 to 1, 3 to 1, 4 to 1, 0 to 2, 1 to 2, 2 to 2, 3 to 2, 4 to 2, 0 to 3, 1 to 3, 2 to 3, 3 to 3, 4 to 3, 0 to 4, 1 to 4, 2 to 4, 3 to 4, 4 to 4)
    val Corner5 = shapeOf(0 to 0, 1 to 0, 2 to 0, 3 to 0, 4 to 0, 0 to 1, 0 to 2, 0 to 3, 0 to 4)
    val Diagonal5 = shapeOf(0 to 0, 1 to 1, 2 to 2, 3 to 3, 4 to 4)
    val Cross5Large = shapeOf(2 to 0, 1 to 1, 2 to 1, 3 to 1, 0 to 2, 1 to 2, 2 to 2, 3 to 2, 4 to 2, 2 to 3, 2 to 4)
    val Plus5Large = shapeOf(2 to 0, 1 to 1, 2 to 1, 3 to 1, 2 to 2, 2 to 3, 1 to 4, 2 to 4, 3 to 4)
    val X5Large = shapeOf(0 to 0, 1 to 1, 2 to 2, 3 to 3, 4 to 4, 4 to 0, 3 to 1, 2 to 2, 1 to 3, 0 to 4)
    val Diamond5Large = shapeOf(2 to 0, 1 to 1, 2 to 1, 3 to 1, 0 to 2, 1 to 2, 2 to 2, 3 to 2, 4 to 2, 1 to 3, 2 to 3, 3 to 3, 2 to 4)
    val Z5Large = shapeOf(0 to 0, 1 to 0, 1 to 1, 2 to 1, 2 to 2, 3 to 2, 3 to 3, 4 to 3)
    val H5 = shapeOf(0 to 0, 0 to 1, 1 to 1, 2 to 1, 3 to 1, 4 to 1, 0 to 2, 4 to 2)
    val Step5 = shapeOf(0 to 0, 1 to 0, 1 to 1, 2 to 1, 2 to 2, 3 to 2, 3 to 3, 4 to 3)
    val Pyramid5 = shapeOf(2 to 0, 1 to 1, 2 to 1, 3 to 1, 0 to 2, 1 to 2, 2 to 2, 3 to 2, 4 to 2, 1 to 3, 2 to 3, 3 to 3, 2 to 4)
    val Arrow5 = shapeOf(2 to 0, 1 to 1, 2 to 1, 3 to 1, 0 to 2, 1 to 2, 2 to 2, 3 to 2, 4 to 2, 2 to 3, 2 to 4)
    val Fork5 = shapeOf(2 to 0, 1 to 1, 2 to 1, 3 to 1, 0 to 2, 1 to 2, 2 to 2, 3 to 2, 4 to 2, 1 to 3, 3 to 3)
    val Trident5 = shapeOf(0 to 0, 2 to 0, 4 to 0, 0 to 1, 1 to 1, 2 to 1, 3 to 1, 4 to 1, 0 to 2, 2 to 2, 4 to 2, 1 to 3, 2 to 3, 3 to 3, 2 to 4)
    val Star5 = shapeOf(2 to 0, 1 to 1, 2 to 1, 3 to 1, 0 to 2, 1 to 2, 2 to 2, 3 to 2, 4 to 2, 1 to 3, 2 to 3, 3 to 3, 2 to 4)
    val Ring5 = shapeOf(1 to 0, 2 to 0, 0 to 1, 1 to 1, 2 to 1, 3 to 1, 4 to 1, 0 to 2, 1 to 2, 2 to 2, 3 to 2, 4 to 2, 1 to 3, 2 to 3, 0 to 4, 1 to 4, 2 to 4, 3 to 4, 4 to 4)
    val Heart5 = shapeOf(1 to 0, 2 to 0, 0 to 1, 1 to 1, 2 to 1, 3 to 1, 4 to 1, 0 to 2, 1 to 2, 2 to 2, 3 to 2, 4 to 2, 1 to 3, 2 to 3, 3 to 3, 2 to 4)
    val W5 = shapeOf(0 to 0, 0 to 1, 1 to 1, 1 to 2, 2 to 2, 2 to 3, 3 to 3, 3 to 4, 4 to 4)
    val M5 = shapeOf(0 to 4, 0 to 3, 1 to 3, 1 to 2, 2 to 2, 2 to 1, 3 to 1, 3 to 0, 4 to 0)
    val N5 = shapeOf(0 to 0, 0 to 1, 0 to 2, 0 to 3, 0 to 4, 1 to 1, 2 to 2, 3 to 3, 4 to 4)
    val V5 = shapeOf(0 to 0, 0 to 1, 0 to 2, 1 to 3, 2 to 4, 3 to 4, 4 to 4)
    val A5 = shapeOf(1 to 0, 1 to 1, 0 to 2, 1 to 2, 2 to 2, 0 to 3, 2 to 3, 0 to 4, 2 to 4)

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
    )

    val Level3: List<Shape> = listOf(
        Diagonal3,
        Rect3x2,
        T4,
        L4TallRight,
        Z4Horizontal,
        Cross3,
        Square3,
    )

    val Level4: List<Shape> = listOf(
        Line4H,
        Line4V,
        Rect4x2,
        L5TallRight,
        Square4,
        Rect4x3,
        U4,
        Corner4,
        Diagonal4,
        Z4Large,
    )

    val Level5: List<Shape> = listOf(
        Square5,
        Rect5x4,
        Corner5,
        Diagonal5,
        X5Large,
        W5,
        Plus5Large,
        Z5Large,
    )

    val All: List<Shape> = (Level1 + Level2 + Level3 + Level4 + Level5).distinct()

    fun forComplexity(maxLevel: Int): List<Shape> {
        return buildList {
            if (maxLevel >= 1) addAll(Level1)
            if (maxLevel >= 2) addAll(Level2)
            if (maxLevel >= 3) addAll(Level3)
            if (maxLevel >= 4) addAll(Level4)
            if (maxLevel >= 5) addAll(Level5)
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
