package com.ugurbuga.blockwise.blocklogic.domain

import kotlin.random.Random

data class GameRules(
    val maxSameColorPerRow: Int? = null,
    val maxSameColorPerCol: Int? = null,
    val maxAdjacentSameColor: Int? = null,
    val minDistinctColorsInFullLine: Int? = null,
    val moveLimit: Int? = null,
)

enum class Difficulty {
    Easy,
    Normal,
    Hard,
    VeryHard,
}

fun Difficulty.toRules(gridSize: GridSize): GameRules {
    return resolveGameConfig(gridSize = gridSize, difficulty = this).rules
}

internal data class OpeningState(
    val grid: Grid,
    val pieces: List<Piece>,
)

object GameEngine {
    fun newGrid(size: GridSize): Grid {
        val cells = List(size.value) { List<Cell?>(size.value) { null } }
        return Grid(size = size, cells = cells)
    }

    fun randomPiece(
        random: Random = Random.Default,
        availableShapes: List<Shape> = Shapes.forMaxDimension(3),
    ): Piece {
        require(availableShapes.isNotEmpty())
        val shape = availableShapes[random.nextInt(availableShapes.size)]
        val color = BlockColor.entries[random.nextInt(BlockColor.entries.size)]
        return Piece(shape = shape, color = color)
    }

    fun buildStartingGrid(
        config: GameConfig,
        random: Random = Random.Default,
    ): Grid {
        var grid = newGrid(config.mode.gridSize)
        val size = config.mode.gridSize.value
        val targetPrefilled = (size * size * config.difficultyConfig.preFilledRatio)
            .toInt()
            .coerceAtLeast(0)
        val placedCoords = mutableListOf<CellCoord>()
        repeat(targetPrefilled * 12) {
            if (placedCoords.size >= targetPrefilled) return@repeat
            val x = random.nextInt(size)
            val y = random.nextInt(size)
            if (grid[x, y] != null) return@repeat
            val color = BlockColor.entries[random.nextInt(BlockColor.entries.size)]
            val piece = Piece(shape = Shapes.Single, color = color)
            if (validatePlacement(grid, piece, x, y, config.rules) == null) {
                grid = placeSinglePrefilledCell(grid, x, y, color)
                placedCoords += CellCoord(x, y)
            }
        }

        if (placedCoords.isEmpty()) return grid

        val lockedTarget = (placedCoords.size * config.difficultyConfig.lockedCellsRatio)
            .toInt()
            .coerceAtMost(placedCoords.size)
        if (lockedTarget == 0) return grid

        val lockedCoords = placedCoords.shuffled(random).take(lockedTarget).toSet()
        val lockedCells = grid.cells.mapIndexed { y, row ->
            row.mapIndexed { x, cell ->
                if (cell != null && CellCoord(x, y) in lockedCoords) {
                    cell.copy(isLocked = true, isPrefilled = true)
                } else {
                    cell
                }
            }
        }
        return Grid(size = grid.size, cells = lockedCells)
    }

    internal fun buildPlayableOpening(
        config: GameConfig,
        random: Random = Random.Default,
        maxAttempts: Int = 40,
    ): OpeningState {
        val rules = config.rules
        repeat(maxAttempts.coerceAtLeast(1)) {
            val grid = buildStartingGrid(config = config, random = random)
            val pieces = buildOpeningPieces(config = config, random = random)
            if (hasAnyValidMove(grid, pieces, rules)) {
                return OpeningState(grid = grid, pieces = pieces)
            }
        }

        val relaxedConfig = config.copy(
            difficultyConfig = config.difficultyConfig.copy(
                preFilledRatio = minOf(config.difficultyConfig.preFilledRatio, 0.12f),
                lockedCellsRatio = 0f,
            )
        )
        val grid = buildStartingGrid(config = relaxedConfig, random = random)
        val guaranteedPiece = findGuaranteedOpeningPiece(grid = grid, config = config)
        val pieces = buildList {
            add(guaranteedPiece)
            repeat(2) {
                add(randomPiece(random = random, availableShapes = config.availableShapes))
            }
        }
        return OpeningState(grid = grid, pieces = pieces)
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

        val adjacencyViolation = findAdjacentColorViolation(placed, rules)
        if (adjacencyViolation != null) return PlacementFailure.Rule(adjacencyViolation)

        val diversityViolation = findDistinctColorViolation(placed, rules)
        if (diversityViolation != null) return PlacementFailure.Rule(diversityViolation)

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

    private fun buildOpeningPieces(
        config: GameConfig,
        random: Random,
    ): List<Piece> {
        val starterShapePool = config.availableShapes.filter { it in (Shapes.Level1 + Shapes.Level2) }
            .ifEmpty { config.availableShapes }
        return buildList {
            add(randomPiece(random = random, availableShapes = starterShapePool))
            repeat(2) {
                add(randomPiece(random = random, availableShapes = config.availableShapes))
            }
        }
    }

    private fun findGuaranteedOpeningPiece(
        grid: Grid,
        config: GameConfig,
    ): Piece {
        val orderedShapes = config.availableShapes
            .sortedWith(
                compareBy<Shape>({ it.cells.size }, { it.cells.maxOf { cell -> cell.dx } + it.cells.maxOf { cell -> cell.dy } })
            )
        orderedShapes.forEach { shape ->
            BlockColor.entries.forEach { color ->
                val piece = Piece(shape = shape, color = color)
                if (findValidOrigins(grid, piece, config.rules).isNotEmpty()) {
                    return piece
                }
            }
        }
        return Piece(shape = Shapes.Single, color = BlockColor.entries.first())
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

        val adjacencyViolation = findAdjacentColorViolation(placed, rules)
        if (adjacencyViolation != null) {
            return PlacementResult(
                grid = grid,
                ruleViolation = adjacencyViolation,
            )
        }

        val diversityViolation = findDistinctColorViolation(placed, rules)
        if (diversityViolation != null) {
            return PlacementResult(
                grid = grid,
                ruleViolation = diversityViolation,
            )
        }

        val size = grid.size.value
        val fullRows = (0 until size).filter { y -> placed[y].all { it != null } }.toSet()
        val fullCols = (0 until size).filter { x -> (0 until size).all { y -> placed[y][x] != null } }.toSet()

        val cleared = placed.mapIndexed { y, row ->
            row.mapIndexed { x, cell ->
                if ((y in fullRows || x in fullCols) && cell?.isLocked != true) null else cell
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

    private fun findAdjacentColorViolation(
        placed: List<List<Cell?>>, // [y][x]
        rules: GameRules,
    ): RuleViolation? {
        val limit = rules.maxAdjacentSameColor ?: return null
        placed.forEachIndexed { rowIndex, row ->
            var currentColor: BlockColor? = null
            var runLength = 0
            row.forEach { cell ->
                val color = cell?.color
                if (color != null && color == currentColor) {
                    runLength += 1
                } else {
                    currentColor = color
                    runLength = if (color != null) 1 else 0
                }
                if (color != null && runLength > limit) {
                    return RuleViolation.TooManyAdjacentSameColorInRow(rowIndex, color, limit)
                }
            }
        }
        if (placed.isEmpty()) return null
        for (x in placed.first().indices) {
            var currentColor: BlockColor? = null
            var runLength = 0
            for (y in placed.indices) {
                val color = placed[y][x]?.color
                if (color != null && color == currentColor) {
                    runLength += 1
                } else {
                    currentColor = color
                    runLength = if (color != null) 1 else 0
                }
                if (color != null && runLength > limit) {
                    return RuleViolation.TooManyAdjacentSameColorInCol(x, color, limit)
                }
            }
        }
        return null
    }

    private fun findDistinctColorViolation(
        placed: List<List<Cell?>>, // [y][x]
        rules: GameRules,
    ): RuleViolation? {
        val required = rules.minDistinctColorsInFullLine ?: return null
        placed.forEachIndexed { rowIndex, row ->
            if (row.all { it != null }) {
                val distinctCount = row.mapNotNull { it?.color }.toSet().size
                if (distinctCount < required) {
                    return RuleViolation.NotEnoughDistinctColorsInRow(rowIndex, distinctCount, required)
                }
            }
        }
        if (placed.isEmpty()) return null
        for (x in placed.first().indices) {
            val column = placed.indices.map { y -> placed[y][x] }
            if (column.all { it != null }) {
                val distinctCount = column.mapNotNull { it?.color }.toSet().size
                if (distinctCount < required) {
                    return RuleViolation.NotEnoughDistinctColorsInCol(x, distinctCount, required)
                }
            }
        }
        return null
    }

    private fun placeSinglePrefilledCell(
        grid: Grid,
        x: Int,
        y: Int,
        color: BlockColor,
    ): Grid {
        val updated = grid.cells.mapIndexed { rowIndex, row ->
            row.mapIndexed { colIndex, cell ->
                if (rowIndex == y && colIndex == x) {
                    Cell(color = color, isPrefilled = true)
                } else {
                    cell
                }
            }
        }
        return Grid(size = grid.size, cells = updated)
    }
}
