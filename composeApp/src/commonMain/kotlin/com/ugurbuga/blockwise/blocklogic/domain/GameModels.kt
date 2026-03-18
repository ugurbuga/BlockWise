package com.ugurbuga.blockwise.blocklogic.domain

data class GridSize(val value: Int) {
    init {
        require(value > 0)
    }
}

data class CellCoord(
    val x: Int,
    val y: Int,
)

data class Cell(
    val color: BlockColor,
)

data class Grid(
    val size: GridSize,
    val cells: List<List<Cell?>>, // [y][x]
) {
    init {
        require(cells.size == size.value)
        require(cells.all { it.size == size.value })
    }

    operator fun get(x: Int, y: Int): Cell? = cells[y][x]
}

data class PlacementResult(
    val grid: Grid,
    val placedGrid: Grid = grid,
    val clearedRowIndices: Set<Int> = emptySet(),
    val clearedColIndices: Set<Int> = emptySet(),
    val clearedRows: Int = clearedRowIndices.size,
    val clearedCols: Int = clearedColIndices.size,
    val ruleViolation: RuleViolation? = null,
)

sealed interface PlacementFailure {
    data object NoPieceSelected : PlacementFailure
    data object OutOfBounds : PlacementFailure
    data object Overlap : PlacementFailure
    data class Rule(val violation: RuleViolation) : PlacementFailure
}

sealed interface RuleViolation {
    data class TooManySameColorInRow(
        val row: Int,
        val color: BlockColor,
        val limit: Int,
    ) : RuleViolation

    data class TooManySameColorInCol(
        val col: Int,
        val color: BlockColor,
        val limit: Int,
    ) : RuleViolation
}
