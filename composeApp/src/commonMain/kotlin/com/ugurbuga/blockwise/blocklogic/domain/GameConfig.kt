package com.ugurbuga.blockwise.blocklogic.domain

data class GameModeKey(
    val gridSize: GridSize,
    val difficulty: Difficulty,
)

data class DifficultyConfig(
    val gridSize: Int,
    val maxPieceLevel: Int,
    val maxSameColorRatio: Float?,
    val maxAdjacentSameColor: Int?,
    val minColorVariety: Int?,
    val moveLimit: Int?,
    val preFilledRatio: Float,
    val lockedCellsRatio: Float,
)

data class GameConfig(
    val mode: GameModeKey,
    val difficultyConfig: DifficultyConfig,
    val rules: GameRules,
    val availableShapes: List<Shape>,
    val maxShapeDimension: Int,
)

fun supportedGridSizes(): List<GridSize> = listOf(8, 10, 12, 14).map(::GridSize)

fun supportedDifficulties(): List<Difficulty> = Difficulty.entries

fun resolveGameConfig(
    gridSize: GridSize,
    difficulty: Difficulty,
): GameConfig {
    val maxShapeDimension = when (difficulty) {
        Difficulty.Easy -> 2
        Difficulty.Normal -> 3
        Difficulty.Hard -> 3
        Difficulty.VeryHard -> 4
    }

    val difficultyConfig = when (difficulty) {
        Difficulty.Easy -> DifficultyConfig(
            gridSize = gridSize.value,
            maxPieceLevel = 2,
            maxSameColorRatio = null,
            maxAdjacentSameColor = null,
            minColorVariety = null,
            moveLimit = null,
            preFilledRatio = 0.08f,
            lockedCellsRatio = 0f,
        )

        Difficulty.Normal -> DifficultyConfig(
            gridSize = gridSize.value,
            maxPieceLevel = 3,
            maxSameColorRatio = 0.65f,
            maxAdjacentSameColor = 4,
            minColorVariety = null,
            moveLimit = null,
            preFilledRatio = 0.12f,
            lockedCellsRatio = 0f,
        )

        Difficulty.Hard -> DifficultyConfig(
            gridSize = gridSize.value,
            maxPieceLevel = 3,
            maxSameColorRatio = 0.60f,
            maxAdjacentSameColor = 3,
            minColorVariety = 3,
            moveLimit = 36,
            preFilledRatio = 0.14f,
            lockedCellsRatio = 0f,
        )

        Difficulty.VeryHard -> DifficultyConfig(
            gridSize = gridSize.value,
            maxPieceLevel = 4,
            maxSameColorRatio = 0.50f,
            maxAdjacentSameColor = 4,
            minColorVariety = 4,
            moveLimit = 32,
            preFilledRatio = 0.15f,
            lockedCellsRatio = 0.02f,
        )
    }
    val resolvedLimit = difficultyConfig.maxSameColorRatio
        ?.let { ratio -> kotlin.math.floor(gridSize.value * ratio).toInt().coerceAtLeast(1) }
    val resolvedAdjacentLimit = difficultyConfig.maxAdjacentSameColor
        ?.coerceAtLeast(maxShapeDimension)

    return GameConfig(
        mode = GameModeKey(gridSize = gridSize, difficulty = difficulty),
        difficultyConfig = difficultyConfig,
        rules = GameRules(
            maxSameColorPerRow = resolvedLimit,
            maxSameColorPerCol = resolvedLimit,
            maxAdjacentSameColor = resolvedAdjacentLimit,
            minDistinctColorsInFullLine = difficultyConfig.minColorVariety,
            moveLimit = difficultyConfig.moveLimit,
        ),
        availableShapes = Shapes.forComplexity(difficultyConfig.maxPieceLevel)
            .filter { shape ->
                val width = shape.cells.maxOf { it.dx } + 1
                val height = shape.cells.maxOf { it.dy } + 1
                width <= maxShapeDimension && height <= maxShapeDimension
            },
        maxShapeDimension = maxShapeDimension,
    )
}

fun allGameModes(): List<GameModeKey> {
    return supportedGridSizes().flatMap { size ->
        supportedDifficulties().map { difficulty ->
            GameModeKey(gridSize = size, difficulty = difficulty)
        }
    }
}

