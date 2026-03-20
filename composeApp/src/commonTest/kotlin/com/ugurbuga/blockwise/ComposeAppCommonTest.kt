package com.ugurbuga.blockwise

import com.ugurbuga.blockwise.blocklogic.domain.BlockColor
import com.ugurbuga.blockwise.blocklogic.domain.Cell
import com.ugurbuga.blockwise.blocklogic.domain.CellCoord
import com.ugurbuga.blockwise.blocklogic.domain.CellOffset
import com.ugurbuga.blockwise.blocklogic.domain.Difficulty
import com.ugurbuga.blockwise.blocklogic.domain.GameEngine
import com.ugurbuga.blockwise.blocklogic.domain.Grid
import com.ugurbuga.blockwise.blocklogic.domain.GridSize
import com.ugurbuga.blockwise.blocklogic.domain.Piece
import com.ugurbuga.blockwise.blocklogic.domain.Shapes
import com.ugurbuga.blockwise.blocklogic.domain.resolveGameConfig
import com.ugurbuga.blockwise.blocklogic.domain.supportedDifficulties
import com.ugurbuga.blockwise.blocklogic.ui.formatBestScore
import com.ugurbuga.blockwise.blocklogic.ui.buildGridGeometry
import com.ugurbuga.blockwise.blocklogic.ui.cellExtentPx
import com.ugurbuga.blockwise.blocklogic.ui.GridAxisGeometry
import com.ugurbuga.blockwise.blocklogic.ui.normalizeDragStartOffsetInContent
import com.ugurbuga.blockwise.blocklogic.ui.previewCellsForOrigins
import com.ugurbuga.blockwise.blocklogic.ui.resolveAttemptedDragOrigin
import com.ugurbuga.blockwise.blocklogic.ui.resolveAttemptedPointerCellAxis
import com.ugurbuga.blockwise.blocklogic.ui.resolveValidDragOrigin
import com.ugurbuga.blockwise.blocklogic.ui.resolveDragAnchor
import com.ugurbuga.blockwise.blocklogic.ui.resolveDraggedOriginAxis
import com.ugurbuga.blockwise.blocklogic.ui.resolveNearestCellAxis
import com.ugurbuga.blockwise.blocklogic.ui.resolvePointerCellAxis
import com.ugurbuga.blockwise.blocklogic.ui.resolveSnappedOriginAxis
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ComposeAppCommonTest {

    @Test
    fun example() {
        assertEquals(3, 1 + 2)
    }

    @Test
    fun `single piece reaches both horizontal edges`() {
        val axis = GridAxisGeometry(firstStart = 100f, step = 34f)

        assertEquals(
            0,
            resolveSnappedOriginAxis(
                targetContentStart = 100f,
                axis = axis,
                maxOrigin = 9,
            )
        )
        assertEquals(
            9,
            resolveSnappedOriginAxis(
                targetContentStart = 100f + 9 * 34f,
                axis = axis,
                maxOrigin = 9,
            )
        )
    }

    @Test
    fun `three cell bar reaches both horizontal edges`() {
        val axis = GridAxisGeometry(firstStart = 100f, step = 34f)

        assertEquals(
            0,
            resolveSnappedOriginAxis(
                targetContentStart = 100f,
                axis = axis,
                maxOrigin = 7,
            )
        )
        assertEquals(
            7,
            resolveSnappedOriginAxis(
                targetContentStart = 100f + 7 * 34f,
                axis = axis,
                maxOrigin = 7,
            )
        )
    }

    @Test
    fun `drag snapping reaches right edge even when grab point is near piece right side`() {
        val axisStarts = listOf(100f, 134f, 168f, 202f, 236f, 270f, 304f, 338f, 372f, 406f)

        assertEquals(
            7,
            resolveDraggedOriginAxis(
                targetContentStart = axisStarts[7],
                axisStarts = axisStarts,
                pieceSpanCells = 3,
            )
        )

        assertEquals(
            7,
            resolveDraggedOriginAxis(
                targetContentStart = axisStarts[7] + 20f,
                axisStarts = axisStarts,
                pieceSpanCells = 3,
            )
        )
    }

    @Test
    fun `drag snapping reaches left edge with same off center grab point`() {
        val axisStarts = listOf(100f, 134f, 168f, 202f, 236f, 270f, 304f, 338f, 372f, 406f)

        assertEquals(
            0,
            resolveDraggedOriginAxis(
                targetContentStart = axisStarts[0],
                axisStarts = axisStarts,
                pieceSpanCells = 3,
            )
        )
    }

    @Test
    fun `drag snapping clamps to the first origin when overlay start is far before the board`() {
        val axisStarts = listOf(100f, 134f, 168f, 202f, 236f, 270f, 304f, 338f, 372f, 406f)

        assertEquals(
            0,
            resolveDraggedOriginAxis(
                targetContentStart = -200f,
                axisStarts = axisStarts,
                pieceSpanCells = 1,
            )
        )
    }

    @Test
    fun `drag snapping clamps to the last origin when overlay start is beyond the board`() {
        val axisStarts = listOf(100f, 134f, 168f, 202f, 236f, 270f, 304f, 338f, 372f, 406f)

        assertEquals(
            7,
            resolveDraggedOriginAxis(
                targetContentStart = 900f,
                axisStarts = axisStarts,
                pieceSpanCells = 3,
            )
        )
    }

    @Test
    fun `drag snapping reaches right edge with uneven measured starts`() {
        val axisStarts = listOf(100f, 134f, 168f, 202f, 236f, 270f, 304f, 338.5f, 373.5f, 409f)

        assertEquals(
            7,
            resolveDraggedOriginAxis(
                targetContentStart = axisStarts[7],
                axisStarts = axisStarts,
                pieceSpanCells = 3,
            )
        )
    }

    @Test
    fun `drag snapping reaches bottom edge with uneven measured starts`() {
        val axisStarts = listOf(200f, 234f, 268f, 302f, 336f, 370f, 404f, 439f, 474f, 510f)

        assertEquals(
            7,
            resolveDraggedOriginAxis(
                targetContentStart = axisStarts[7],
                axisStarts = axisStarts,
                pieceSpanCells = 3,
            )
        )
    }

    @Test
    fun `drag start offset is clamped to the piece content when drag begins in padding`() {
        assertEquals(
            96f,
            normalizeDragStartOffsetInContent(
                startOffsetInPiece = 104f,
                pieceContainerInsetPx = 8f,
                contentSizePx = 96f,
            )
        )
        assertEquals(
            0f,
            normalizeDragStartOffsetInContent(
                startOffsetInPiece = 3f,
                pieceContainerInsetPx = 8f,
                contentSizePx = 96f,
            )
        )
    }

    @Test
    fun `normalized drag start still resolves the final origin for a horizontal bar`() {
        val axisStarts = listOf(100f, 134f, 168f, 202f, 236f, 270f, 304f, 338f, 372f, 406f)
        val normalizedOffset = normalizeDragStartOffsetInContent(
            startOffsetInPiece = 112f,
            pieceContainerInsetPx = 8f,
            contentSizePx = 96f,
        )

        assertEquals(
            7,
            resolveDraggedOriginAxis(
                targetContentStart = (axisStarts[7] + normalizedOffset) - normalizedOffset,
                axisStarts = axisStarts,
                pieceSpanCells = 3,
            )
        )
    }

    @Test
    fun `normalized drag start still resolves the final origin for a vertical bar`() {
        val axisStarts = listOf(200f, 234f, 268f, 302f, 336f, 370f, 404f, 438f, 472f, 506f)
        val normalizedOffset = normalizeDragStartOffsetInContent(
            startOffsetInPiece = 112f,
            pieceContainerInsetPx = 8f,
            contentSizePx = 96f,
        )

        assertEquals(
            7,
            resolveDraggedOriginAxis(
                targetContentStart = (axisStarts[7] + normalizedOffset) - normalizedOffset,
                axisStarts = axisStarts,
                pieceSpanCells = 3,
            )
        )
    }

    @Test
    fun `all random shapes stay within 4x4 bounds`() {
        assertTrue(
            Shapes.All.all { shape ->
                val maxDx = shape.cells.maxOf { it.dx }
                val maxDy = shape.cells.maxOf { it.dy }
                maxDx < 4 && maxDy < 4
            }
        )
    }

    @Test
    fun `all random shapes are unique`() {
        val signatures = Shapes.All.map { shape ->
            shape.cells.joinToString(";") { "${it.dx},${it.dy}" }
        }
        assertEquals(signatures.size, signatures.toSet().size)
    }

    @Test
    fun `cell extent is derived from board size and gap`() {
        assertEquals(
            48.2f,
            cellExtentPx(
                boardExtentPx = 500f,
                gridCount = 10,
                gapPx = 2f,
            )
        )
    }

    @Test
    fun `grid geometry uses board origin as the first cell start`() {
        val geometry = assertNotNull(
            buildGridGeometry(
                gridTopLeftInRoot = Offset(24f, 96f),
                gridSizePx = IntSize(500, 500),
                gridCount = 10,
                gapPx = 2f,
            )
        )

        assertEquals(24f, geometry.x.firstStart)
        assertEquals(96f, geometry.y.firstStart)
        assertEquals(50.2f, geometry.x.step)
        assertEquals(50.2f, geometry.y.step)
    }

    @Test
    fun `grid geometry snapping reaches the right edge on 8 10 and 12 boards`() {
        listOf(8, 10, 12).forEach { gridSize ->
            val geometry = assertNotNull(
                buildGridGeometry(
                    gridTopLeftInRoot = Offset(40f, 120f),
                    gridSizePx = IntSize(480, 480),
                    gridCount = gridSize,
                    gapPx = 2f,
                )
            )
            val maxOrigin = gridSize - 3

            assertEquals(
                maxOrigin,
                resolveSnappedOriginAxis(
                    targetContentStart = geometry.x.firstStart + geometry.x.step * 50f,
                    axis = geometry.x,
                    maxOrigin = maxOrigin,
                )
            )
        }
    }

    @Test
    fun `grid geometry snapping reaches the bottom edge without producing row 10 on a 10 board`() {
        val geometry = assertNotNull(
            buildGridGeometry(
                gridTopLeftInRoot = Offset(32f, 80f),
                gridSizePx = IntSize(500, 500),
                gridCount = 10,
                gapPx = 2f,
            )
        )

        val originY = assertNotNull(
            resolveSnappedOriginAxis(
                targetContentStart = geometry.y.firstStart + geometry.y.step * 100f,
                axis = geometry.y,
                maxOrigin = 7,
            )
        )

        assertEquals(7, originY)
        assertTrue(listOf(originY, originY + 1, originY + 2).all { it in 0..9 })
    }

    @Test
    fun `drag anchor selects the nearest occupied cell for a horizontal bar`() {
        val anchor = resolveDragAnchor(
            piece = com.ugurbuga.blockwise.blocklogic.domain.Piece(
                shape = Shapes.Line3H,
                color = com.ugurbuga.blockwise.blocklogic.domain.BlockColor.Blue,
            ),
            offsetInContent = Offset(118f, 20f),
            cellWidthPx = 48f,
            cellHeightPx = 48f,
            gapPx = 2f,
        )

        assertEquals(2, anchor.dx)
        assertEquals(0, anchor.dy)
    }

    @Test
    fun `drag anchor selects the nearest occupied cell for a vertical bar`() {
        val anchor = resolveDragAnchor(
            piece = com.ugurbuga.blockwise.blocklogic.domain.Piece(
                shape = Shapes.Line3V,
                color = com.ugurbuga.blockwise.blocklogic.domain.BlockColor.Red,
            ),
            offsetInContent = Offset(20f, 118f),
            cellWidthPx = 48f,
            cellHeightPx = 48f,
            gapPx = 2f,
        )

        assertEquals(0, anchor.dx)
        assertEquals(2, anchor.dy)
    }

    @Test
    fun `nearest cell snapping does not advance before the next cell center threshold`() {
        val axis = GridAxisGeometry(firstStart = 32f, step = 50.2f)

        assertEquals(
            0,
            resolveNearestCellAxis(
                pointerPosition = 32f + 47f,
                axis = axis,
                cellExtent = 48.2f,
                maxCellIndex = 9,
            )
        )
        assertEquals(
            1,
            resolveNearestCellAxis(
                pointerPosition = 32f + 51f,
                axis = axis,
                cellExtent = 48.2f,
                maxCellIndex = 9,
            )
        )
    }

    @Test
    fun `nearest cell snapping reaches the final board cell and clamps beyond it`() {
        val geometry = assertNotNull(
            buildGridGeometry(
                gridTopLeftInRoot = Offset(32f, 80f),
                gridSizePx = IntSize(500, 500),
                gridCount = 10,
                gapPx = 2f,
            )
        )

        assertEquals(
            9,
            resolveNearestCellAxis(
                pointerPosition = geometry.x.firstStart + geometry.x.step * 9 + geometry.cellWidth / 2f,
                axis = geometry.x,
                cellExtent = geometry.cellWidth,
                maxCellIndex = 9,
            )
        )
        assertEquals(
            9,
            resolveNearestCellAxis(
                pointerPosition = geometry.x.firstStart + geometry.x.step * 20,
                axis = geometry.x,
                cellExtent = geometry.cellWidth,
                maxCellIndex = 9,
            )
        )
    }

    @Test
    fun `pointer cell snapping stays on the same cell while pointer is inside that cell area`() {
        val geometry = assertNotNull(
            buildGridGeometry(
                gridTopLeftInRoot = Offset(32f, 80f),
                gridSizePx = IntSize(500, 500),
                gridCount = 10,
                gapPx = 2f,
            )
        )

        assertEquals(
            0,
            resolvePointerCellAxis(
                pointerPosition = geometry.x.firstStart + geometry.cellWidth - 1f,
                axis = geometry.x,
                cellExtent = geometry.cellWidth,
                maxCellIndex = 9,
            )
        )
        assertEquals(
            1,
            resolvePointerCellAxis(
                pointerPosition = geometry.x.firstStart + geometry.x.step + 1f,
                axis = geometry.x,
                cellExtent = geometry.cellWidth,
                maxCellIndex = 9,
            )
        )
    }

    @Test
    fun `pointer cell snapping resolves gap positions to the nearest neighboring cell`() {
        val geometry = assertNotNull(
            buildGridGeometry(
                gridTopLeftInRoot = Offset(32f, 80f),
                gridSizePx = IntSize(500, 500),
                gridCount = 10,
                gapPx = 2f,
            )
        )
        val gapStart = geometry.x.firstStart + geometry.cellWidth

        assertEquals(
            0,
            resolvePointerCellAxis(
                pointerPosition = gapStart + 0.25f,
                axis = geometry.x,
                cellExtent = geometry.cellWidth,
                maxCellIndex = 9,
            )
        )
        assertEquals(
            1,
            resolvePointerCellAxis(
                pointerPosition = gapStart + 1.75f,
                axis = geometry.x,
                cellExtent = geometry.cellWidth,
                maxCellIndex = 9,
            )
        )
    }

    @Test
    fun `pointer cell snapping reaches and clamps to the final board cell`() {
        val geometry = assertNotNull(
            buildGridGeometry(
                gridTopLeftInRoot = Offset(32f, 80f),
                gridSizePx = IntSize(500, 500),
                gridCount = 10,
                gapPx = 2f,
            )
        )

        assertEquals(
            9,
            resolvePointerCellAxis(
                pointerPosition = geometry.x.firstStart + geometry.x.step * 9 + geometry.cellWidth - 1f,
                axis = geometry.x,
                cellExtent = geometry.cellWidth,
                maxCellIndex = 9,
            )
        )
        assertEquals(
            9,
            resolvePointerCellAxis(
                pointerPosition = geometry.x.firstStart + geometry.x.step * 20f,
                axis = geometry.x,
                cellExtent = geometry.cellWidth,
                maxCellIndex = 9,
            )
        )
    }

    @Test
    fun `attempted pointer cell axis can resolve outside the board bounds`() {
        val geometry = assertNotNull(
            buildGridGeometry(
                gridTopLeftInRoot = Offset(32f, 80f),
                gridSizePx = IntSize(500, 500),
                gridCount = 10,
                gapPx = 2f,
            )
        )

        assertEquals(
            -1,
            resolveAttemptedPointerCellAxis(
                pointerPosition = geometry.x.firstStart - 1f,
                axis = geometry.x,
                cellExtent = geometry.cellWidth,
            )
        )
        assertEquals(
            10,
            resolveAttemptedPointerCellAxis(
                pointerPosition = geometry.x.firstStart + geometry.x.step * 10f + 1f,
                axis = geometry.x,
                cellExtent = geometry.cellWidth,
            )
        )
    }

    @Test
    fun `attempted drag origin keeps out of bounds coordinates for validation feedback`() {
        assertEquals(
            CellCoord(-1, 5),
            resolveAttemptedDragOrigin(
                hoveredCell = CellCoord(0, 6),
                anchor = CellOffset(1, 1),
            )
        )
    }

    @Test
    fun `row color limit reports a row violation with asymmetric limits`() {
        val grid = Grid(
            size = GridSize(4),
            cells = listOf(
                listOf(Cell(BlockColor.Red), Cell(BlockColor.Red), null, null),
                listOf(null, null, null, null),
                listOf(null, null, null, null),
                listOf(null, null, null, null),
            )
        )

        val failure = GameEngine.validatePlacement(
            grid = grid,
            piece = Piece(shape = Shapes.Single, color = BlockColor.Red),
            originX = 2,
            originY = 0,
            rules = com.ugurbuga.blockwise.blocklogic.domain.GameRules(
                maxSameColorPerRow = 2,
                maxSameColorPerCol = 4,
            ),
        )

        assertEquals(
            com.ugurbuga.blockwise.blocklogic.domain.PlacementFailure.Rule(
                com.ugurbuga.blockwise.blocklogic.domain.RuleViolation.TooManySameColorInRow(
                    row = 0,
                    color = BlockColor.Red,
                    limit = 2,
                )
            ),
            failure,
        )
    }

    @Test
    fun `column color limit reports a column violation with asymmetric limits`() {
        val grid = Grid(
            size = GridSize(4),
            cells = listOf(
                listOf(Cell(BlockColor.Blue), null, null, null),
                listOf(Cell(BlockColor.Blue), null, null, null),
                listOf(null, null, null, null),
                listOf(null, null, null, null),
            )
        )

        val failure = GameEngine.validatePlacement(
            grid = grid,
            piece = Piece(shape = Shapes.Single, color = BlockColor.Blue),
            originX = 0,
            originY = 2,
            rules = com.ugurbuga.blockwise.blocklogic.domain.GameRules(
                maxSameColorPerRow = 4,
                maxSameColorPerCol = 2,
            ),
        )

        assertEquals(
            com.ugurbuga.blockwise.blocklogic.domain.PlacementFailure.Rule(
                com.ugurbuga.blockwise.blocklogic.domain.RuleViolation.TooManySameColorInCol(
                    col = 0,
                    color = BlockColor.Blue,
                    limit = 2,
                )
            ),
            failure,
        )
    }

    @Test
    fun `easy mode stays open and simple`() {
        val config = resolveGameConfig(GridSize(14), Difficulty.Easy)

        assertEquals(null, config.rules.maxSameColorPerRow)
        assertEquals(null, config.rules.maxSameColorPerCol)
        assertEquals(0.08f, config.difficultyConfig.preFilledRatio)
        assertEquals(2, config.maxShapeDimension)
    }

    @Test
    fun `normal mode uses relaxed mid game limits`() {
        val config = resolveGameConfig(GridSize(12), Difficulty.Normal)

        assertEquals(7, config.rules.maxSameColorPerRow)
        assertEquals(7, config.rules.maxSameColorPerCol)
        assertEquals(4, config.rules.maxAdjacentSameColor)
        assertEquals(null, config.rules.minDistinctColorsInFullLine)
        assertEquals(null, config.rules.moveLimit)
        assertEquals(0.12f, config.difficultyConfig.preFilledRatio)
        assertEquals(3, config.maxShapeDimension)
    }

    @Test
    fun `hard and expert use softer layered constraints`() {
        val hard = resolveGameConfig(GridSize(10), Difficulty.Hard)
        val veryHard = resolveGameConfig(GridSize(14), Difficulty.VeryHard)

        assertEquals(6, hard.rules.maxSameColorPerRow)
        assertEquals(6, hard.rules.maxSameColorPerCol)
        assertEquals(3, hard.rules.maxAdjacentSameColor)
        assertEquals(3, hard.rules.minDistinctColorsInFullLine)
        assertEquals(36, hard.rules.moveLimit)
        assertEquals(0.14f, hard.difficultyConfig.preFilledRatio)

        assertEquals(7, veryHard.rules.maxSameColorPerRow)
        assertEquals(7, veryHard.rules.maxSameColorPerCol)
        assertEquals(4, veryHard.rules.maxAdjacentSameColor)
        assertEquals(4, veryHard.rules.minDistinctColorsInFullLine)
        assertEquals(32, veryHard.rules.moveLimit)
        assertEquals(0.15f, veryHard.difficultyConfig.preFilledRatio)
        assertEquals(0.02f, veryHard.difficultyConfig.lockedCellsRatio)
        assertEquals(4, veryHard.maxShapeDimension)
    }

    @Test
    fun `adjacent same color limits are never smaller than max piece size`() {
        supportedDifficulties().forEach { difficulty ->
            val config = resolveGameConfig(GridSize(14), difficulty)
            config.rules.maxAdjacentSameColor?.let { adjacentLimit ->
                assertTrue(adjacentLimit >= config.maxShapeDimension)
            }
        }
    }

    @Test
    fun `hard and expert start states always include at least one playable opening move`() {
        listOf(
            GridSize(10) to Difficulty.Hard,
            GridSize(14) to Difficulty.VeryHard,
        ).forEachIndexed { index, (size, difficulty) ->
            val config = resolveGameConfig(size, difficulty)
            val opening = GameEngine.buildPlayableOpening(
                config = config,
                random = Random(index + 7),
            )

            assertEquals(3, opening.pieces.size)
            assertTrue(GameEngine.hasAnyValidMove(opening.grid, opening.pieces, config.rules))
        }
    }

    @Test
    fun `shape pools respect the configured complexity and maximum dimension`() {
        val easyShapes = resolveGameConfig(GridSize(8), Difficulty.Easy).availableShapes
        val normalShapes = resolveGameConfig(GridSize(10), Difficulty.Normal).availableShapes
        val veryHardShapes = resolveGameConfig(GridSize(14), Difficulty.VeryHard).availableShapes

        assertTrue(easyShapes.all { shape ->
            val width = shape.cells.maxOf { it.dx } + 1
            val height = shape.cells.maxOf { it.dy } + 1
            width <= 2 && height <= 2
        })
        assertTrue(normalShapes.any { shape ->
            val width = shape.cells.maxOf { it.dx } + 1
            val height = shape.cells.maxOf { it.dy } + 1
            width == 3 || height == 3
        })
        assertTrue(normalShapes.none { shape ->
            val width = shape.cells.maxOf { it.dx } + 1
            val height = shape.cells.maxOf { it.dy } + 1
            width == 4 || height == 4
        })
        assertTrue(veryHardShapes.any { shape ->
            val width = shape.cells.maxOf { it.dx } + 1
            val height = shape.cells.maxOf { it.dy } + 1
            width == 4 || height == 4
        })
    }

    @Test
    fun `missing scores are rendered with a dash placeholder`() {
        assertEquals("-", formatBestScore(null, "-"))
        assertEquals("42", formatBestScore(42, "-"))
    }

    @Test
    fun `best score save helper only accepts better scores`() {
        assertTrue(BestScoreStore.shouldSaveBest(previousBest = null, score = 8))
        assertTrue(BestScoreStore.shouldSaveBest(previousBest = 12, score = 13))
        assertEquals(false, BestScoreStore.shouldSaveBest(previousBest = 12, score = 12))
        assertEquals(false, BestScoreStore.shouldSaveBest(previousBest = 12, score = 4))
    }

    @Test
    fun `navigation helpers maintain a back stack and pop to the previous screen`() {
        val rootStack = resetToRoot()

        assertEquals(listOf(AppScreen.LevelSelection), rootStack)
        assertEquals(
            listOf(AppScreen.LevelSelection, AppScreen.Rules),
            pushScreen(rootStack, AppScreen.Rules)
        )
        assertEquals(
            listOf(AppScreen.LevelSelection, AppScreen.Game),
            pushScreen(rootStack, AppScreen.Game)
        )
        assertEquals(
            listOf(AppScreen.LevelSelection),
            popScreen(listOf(AppScreen.LevelSelection, AppScreen.Scores))
        )
        assertEquals(
            listOf(AppScreen.LevelSelection),
            popScreen(rootStack)
        )
    }

    @Test
    fun `scroll state keys are stable per screen and mode`() {
        assertEquals(
            AppScreen.LevelSelection.name,
            scrollStateKey(AppScreen.LevelSelection, GridSize(10), Difficulty.Normal)
        )
        assertEquals(
            AppScreen.Scores.name,
            scrollStateKey(AppScreen.Scores, GridSize(8), Difficulty.Easy)
        )
        assertEquals(
            "Rules:10:Normal",
            scrollStateKey(AppScreen.Rules, GridSize(10), Difficulty.Normal)
        )
        assertEquals(
            "Rules:12:Hard",
            scrollStateKey(AppScreen.Rules, GridSize(12), Difficulty.Hard)
        )
    }

    @Test
    fun `app language storage mapping supports all shipped languages`() {
        assertEquals(null, AppLanguage.fromStorageValue(null))
        assertEquals(null, AppLanguage.fromStorageValue("unknown"))
        assertEquals(AppLanguage.English, AppLanguage.fromStorageValue("en"))
        assertEquals(AppLanguage.Turkish, AppLanguage.fromStorageValue("tr") )
        assertEquals(AppLanguage.Spanish, AppLanguage.fromStorageValue("es"))
        assertEquals(AppLanguage.French, AppLanguage.fromStorageValue("fr"))
        assertEquals(AppLanguage.German, AppLanguage.fromStorageValue("de"))
        assertEquals(AppLanguage.Russian, AppLanguage.fromStorageValue("ru"))
        assertEquals(AppLanguage.Arabic, AppLanguage.fromStorageValue("ar"))
    }

    @Test
    fun `device language resolution falls back to english when unsupported`() {
        assertEquals(AppLanguage.Turkish, AppLanguage.fromLanguageTag("tr-TR"))
        assertEquals(AppLanguage.Spanish, AppLanguage.fromLanguageTag("es_MX"))
        assertEquals(AppLanguage.English, AppLanguage.fromLanguageTag("it-IT"))
        assertEquals(AppLanguage.English, AppLanguage.fromLanguageTag(null))
        assertEquals(AppLanguage.English, AppLanguage.fromLanguageTag(""))
    }

    @Test
    fun `language picker labels stay in each language endonym`() {
        assertEquals("English", AppLanguage.English.endonym)
        assertEquals("Türkçe", AppLanguage.Turkish.endonym)
        assertEquals("Español", AppLanguage.Spanish.endonym)
        assertEquals("Français", AppLanguage.French.endonym)
        assertEquals("Deutsch", AppLanguage.German.endonym)
        assertEquals("Русский", AppLanguage.Russian.endonym)
        assertEquals("العربية", AppLanguage.Arabic.endonym)
    }

    @Test
    fun `language picker abbreviations stay compact and stable`() {
        assertEquals("EN", AppLanguage.English.abbreviation)
        assertEquals("TR", AppLanguage.Turkish.abbreviation)
        assertEquals("ES", AppLanguage.Spanish.abbreviation)
        assertEquals("FR", AppLanguage.French.abbreviation)
        assertEquals("DE", AppLanguage.German.abbreviation)
        assertEquals("РУ", AppLanguage.Russian.abbreviation)
        assertEquals("عر", AppLanguage.Arabic.abbreviation)
    }

    @Test
    fun `arabic is the only shipped rtl language`() {
        assertEquals(false, AppLanguage.English.isRtl)
        assertEquals(false, AppLanguage.Turkish.isRtl)
        assertEquals(false, AppLanguage.Spanish.isRtl)
        assertEquals(false, AppLanguage.French.isRtl)
        assertEquals(false, AppLanguage.German.isRtl)
        assertEquals(false, AppLanguage.Russian.isRtl)
        assertEquals(true, AppLanguage.Arabic.isRtl)
    }

    @Test
    fun `drag preview only snaps to origins that satisfy the full rule set`() {
        val grid = Grid(
            size = GridSize(4),
            cells = listOf(
                listOf(Cell(BlockColor.Red), Cell(BlockColor.Red), Cell(BlockColor.Blue), null),
                listOf(null, null, null, null),
                listOf(null, null, null, null),
                listOf(null, null, null, null),
            )
        )
        val rules = resolveGameConfig(GridSize(4), Difficulty.Hard).rules.copy(
            maxSameColorPerRow = 2,
            maxSameColorPerCol = null,
            maxAdjacentSameColor = null,
            minDistinctColorsInFullLine = null,
            moveLimit = null,
        )
        val piece = Piece(shape = Shapes.Single, color = BlockColor.Red)
        val validOrigins = GameEngine.findValidOrigins(grid, piece, rules)

        assertEquals(
            null,
            resolveValidDragOrigin(
                piece = piece,
                hoveredCell = CellCoord(3, 0),
                anchor = CellOffset(0, 0),
                gridCount = 4,
                isFingerInsideBoard = true,
                validOrigins = validOrigins,
            )
        )
        assertEquals(
            CellCoord(3, 1),
            resolveValidDragOrigin(
                piece = piece,
                hoveredCell = CellCoord(3, 1),
                anchor = CellOffset(0, 0),
                gridCount = 4,
                isFingerInsideBoard = true,
                validOrigins = validOrigins,
            )
        )
        assertEquals(
            null,
            resolveValidDragOrigin(
                piece = piece,
                hoveredCell = CellCoord(3, 1),
                anchor = CellOffset(0, 0),
                gridCount = 4,
                isFingerInsideBoard = false,
                validOrigins = validOrigins,
            )
        )
    }

    @Test
    fun `drag highlight cells expand from every valid origin for the dragged piece`() {
        val piece = Piece(shape = Shapes.Line3H, color = BlockColor.Blue)

        assertEquals(
            setOf(
                CellCoord(0, 0),
                CellCoord(1, 0),
                CellCoord(2, 0),
                CellCoord(2, 2),
                CellCoord(3, 2),
                CellCoord(4, 2),
            ),
            previewCellsForOrigins(
                piece = piece,
                origins = setOf(
                    CellCoord(0, 0),
                    CellCoord(2, 2),
                ),
            )
        )
    }
}