package com.ugurbuga.blockwise.blocklogic.domain

import kotlin.random.Random

data class GameRules(
    val maxSameColorPerRow: Int? = 2,
    val maxSameColorPerCol: Int? = 2,
)

enum class Difficulty {
    Easy,
    Normal,
    Hard,
}

fun Difficulty.toRules(): GameRules {
    return when (this) {
        Difficulty.Easy -> GameRules(maxSameColorPerRow = null, maxSameColorPerCol = null)
        Difficulty.Normal -> GameRules(maxSameColorPerRow = 6, maxSameColorPerCol = 6)
        Difficulty.Hard -> GameRules(maxSameColorPerRow = 4, maxSameColorPerCol = 4)
    }
}

object GameEngine {
    fun newGrid(size: GridSize): Grid {
        val cells = List(size.value) { List<Cell?>(size.value) { null } }
        return Grid(size = size, cells = cells)
    }

    fun randomPiece(random: Random = Random.Default): Piece {
        val shape = Shapes.All[random.nextInt(Shapes.All.size)]
        val color = BlockColor.entries[random.nextInt(BlockColor.entries.size)]
        return Piece(shape = shape, color = color)
    }

    fun validatePlacement(
        grid: Grid,
        piece: Piece,
        originX: Int,
        originY: Int,
        rules: GameRules,
    ): PlacementFailure? {
        val size = grid.size.value
        var hasOutOfBounds = false
        var hasOverlap = false

        piece.shape.cells.forEach { offset ->
            val x = originX + offset.dx
            val y = originY + offset.dy
            if (x !in 0 until size || y !in 0 until size) {
                hasOutOfBounds = true
                return@forEach
            }
            if (grid[x, y] != null) {
                hasOverlap = true
            }
        }

        if (hasOutOfBounds) return PlacementFailure.OutOfBounds
        if (hasOverlap) return PlacementFailure.Overlap

        val placed = grid.cells.mapIndexed { y, row ->
            row.mapIndexed { x, cell ->
                val isPieceCell = piece.shape.cells.any { o -> originX + o.dx == x && originY + o.dy == y }
                if (isPieceCell) Cell(piece.color) else cell
            }
        }

        val violation = findRowColorViolation(placed, rules)
        if (violation != null) return PlacementFailure.Rule(violation)

        val colViolation = findColColorViolation(placed, rules)
        if (colViolation != null) return PlacementFailure.Rule(colViolation)

        return null
    }

    fun canPlace(grid: Grid, piece: Piece, originX: Int, originY: Int, rules: GameRules): Boolean {
        return validatePlacement(grid, piece, originX, originY, rules) == null
    }

    fun findValidOrigins(grid: Grid, piece: Piece, rules: GameRules): Set<CellCoord> {
        val size = grid.size.value
        val result = HashSet<CellCoord>()
        for (y in 0 until size) {
            for (x in 0 until size) {
                if (canPlace(grid, piece, x, y, rules)) {
                    result += CellCoord(x, y)
                }
            }
        }
        return result
    }

    fun hasAnyValidMove(grid: Grid, pieces: List<Piece>, rules: GameRules): Boolean {
        return pieces.any { piece -> findValidOrigins(grid, piece, rules).isNotEmpty() }
    }

    fun place(
        grid: Grid,
        piece: Piece,
        originX: Int,
        originY: Int,
        rules: GameRules,
    ): PlacementResult {
        if (validatePlacement(grid, piece, originX, originY, rules) != null) {
            return PlacementResult(grid = grid)
        }

        val placed = grid.cells.mapIndexed { y, row ->
            row.mapIndexed { x, cell ->
                val isPieceCell = piece.shape.cells.any { o -> originX + o.dx == x && originY + o.dy == y }
                if (isPieceCell) Cell(piece.color) else cell
            }
        }

        val violation = findRowColorViolation(placed, rules)
        if (violation != null) {
            return PlacementResult(
                grid = grid,
                ruleViolation = violation,
            )
        }

        val colViolation = findColColorViolation(placed, rules)
        if (colViolation != null) {
            return PlacementResult(
                grid = grid,
                ruleViolation = colViolation,
            )
        }

        val size = grid.size.value
        val fullRows = (0 until size).filter { y -> placed[y].all { it != null } }.toSet()
        val fullCols = (0 until size).filter { x -> (0 until size).all { y -> placed[y][x] != null } }.toSet()

        val cleared = placed.mapIndexed { y, row ->
            row.mapIndexed { x, cell ->
                if (y in fullRows || x in fullCols) null else cell
            }
        }

        return PlacementResult(
            grid = Grid(size = grid.size, cells = cleared),
            placedGrid = Grid(size = grid.size, cells = placed),
            clearedRowIndices = fullRows,
            clearedColIndices = fullCols,
        )
    }

    private fun findRowColorViolation(
        placed: List<List<Cell?>>, // [y][x]
        rules: GameRules,
    ): RuleViolation? {
        val limit = rules.maxSameColorPerRow ?: return null
        placed.forEachIndexed { rowIndex, row ->
            val counts = mutableMapOf<BlockColor, Int>()
            row.forEach { cell ->
                val color = cell?.color ?: return@forEach
                counts[color] = (counts[color] ?: 0) + 1
            }
            val tooMuch = counts.entries.firstOrNull { it.value > limit }
            if (tooMuch != null) {
                return RuleViolation.TooManySameColorInRow(
                    row = rowIndex,
                    color = tooMuch.key,
                    limit = limit,
                )
            }
        }
        return null
    }

    private fun findColColorViolation(
        placed: List<List<Cell?>>, // [y][x]
        rules: GameRules,
    ): RuleViolation? {
        val limit = rules.maxSameColorPerCol ?: return null
        if (placed.isEmpty()) return null

        val height = placed.size
        val width = placed.first().size
        for (x in 0 until width) {
            val counts = mutableMapOf<BlockColor, Int>()
            for (y in 0 until height) {
                val color = placed[y][x]?.color ?: continue
                counts[color] = (counts[color] ?: 0) + 1
            }
            val tooMuch = counts.entries.firstOrNull { it.value > limit }
            if (tooMuch != null) {
                return RuleViolation.TooManySameColorInCol(
                    col = x,
                    color = tooMuch.key,
                    limit = limit,
                )
            }
        }
        return null
    }
}
