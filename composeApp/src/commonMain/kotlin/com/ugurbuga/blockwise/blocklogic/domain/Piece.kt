package com.ugurbuga.blockwise.blocklogic.domain

data class Piece(
    val shape: Shape,
    val color: BlockColor,
)

data class Shape(
    val cells: List<CellOffset>,
) {
    init {
        require(cells.isNotEmpty())
    }
}

data class CellOffset(
    val dx: Int,
    val dy: Int,
)

object Shapes {
    val Single = Shape(listOf(CellOffset(0, 0)))
    val Line2H = Shape(listOf(CellOffset(0, 0), CellOffset(1, 0)))
    val Line3H = Shape(listOf(CellOffset(0, 0), CellOffset(1, 0), CellOffset(2, 0)))
    val Line4H = Shape(listOf(CellOffset(0, 0), CellOffset(1, 0), CellOffset(2, 0), CellOffset(3, 0)))
    val Line2V = Shape(listOf(CellOffset(0, 0), CellOffset(0, 1)))
    val Line3V = Shape(listOf(CellOffset(0, 0), CellOffset(0, 1), CellOffset(0, 2)))
    val Square2 = Shape(listOf(CellOffset(0, 0), CellOffset(1, 0), CellOffset(0, 1), CellOffset(1, 1)))
    val L3 = Shape(listOf(CellOffset(0, 0), CellOffset(0, 1), CellOffset(1, 1)))
    val T4 = Shape(listOf(CellOffset(0, 0), CellOffset(1, 0), CellOffset(2, 0), CellOffset(1, 1)))

    val All: List<Shape> = listOf(Single, Line2H, Line3H, Line4H, Line2V, Line3V, Square2, L3, T4)
}
