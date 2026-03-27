package com.ugurbuga.blockwise.blocklogic.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitTouchSlopOrCancellation
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ugurbuga.blockwise.AppColorPalette
import com.ugurbuga.blockwise.BlockVisualStyle
import com.ugurbuga.blockwise.LocalBoardBlockStyleMode
import com.ugurbuga.blockwise.InvalidPlacementFeedbackMode
import com.ugurbuga.blockwise.LocalAppColorPalette
import com.ugurbuga.blockwise.LocalAppLanguage
import com.ugurbuga.blockwise.LocalBlockColorPalette
import com.ugurbuga.blockwise.LocalBlockGapSpacing
import com.ugurbuga.blockwise.LocalBlockVisualStyle
import com.ugurbuga.blockwise.LocalDragFingerOffsetLevel
import com.ugurbuga.blockwise.LocalInvalidPlacementFeedbackMode
import com.ugurbuga.blockwise.LocalPaletteIsDarkTheme
import com.ugurbuga.blockwise.localizedGetString
import com.ugurbuga.blockwise.resolveBoardEmptyBlockRenderStyle
import com.ugurbuga.blockwise.resolveBoardFilledBlockRenderStyle
import com.ugurbuga.blockwise.resolveBoardBlockShapeStyle
import com.ugurbuga.blockwise.localizedStringResource as stringResource
import com.ugurbuga.blockwise.blocklogic.domain.BlockColor
import com.ugurbuga.blockwise.blocklogic.domain.CellCoord
import com.ugurbuga.blockwise.blocklogic.domain.CellOffset
import com.ugurbuga.blockwise.blocklogic.domain.GameEngine
import com.ugurbuga.blockwise.blocklogic.domain.Difficulty
import com.ugurbuga.blockwise.blocklogic.domain.GameRules
import com.ugurbuga.blockwise.blocklogic.domain.GridSize
import com.ugurbuga.blockwise.blocklogic.domain.Grid
import com.ugurbuga.blockwise.blocklogic.domain.PlacementFailure
import com.ugurbuga.blockwise.blocklogic.domain.RuleViolation
import com.ugurbuga.blockwise.blocklogic.domain.Piece
import com.ugurbuga.blockwise.blocklogic.domain.Shapes
import com.ugurbuga.blockwise.blocklogic.domain.resolveGameConfig
import com.ugurbuga.blockwise.ui.theme.BlockWisePalette
import com.ugurbuga.blockwise.ui.theme.BlockWiseThemePalette
import com.ugurbuga.blockwise.ui.theme.BlockWiseTheme
import com.ugurbuga.blockwise.ui.theme.toPaletteColor
import blockwise.composeapp.generated.resources.Res
import blockwise.composeapp.generated.resources.game_over
import blockwise.composeapp.generated.resources.game_over_message
import blockwise.composeapp.generated.resources.invalid_placement
import blockwise.composeapp.generated.resources.invalid_placement_adjacent_col
import blockwise.composeapp.generated.resources.invalid_placement_adjacent_row
import blockwise.composeapp.generated.resources.invalid_placement_distinct_col
import blockwise.composeapp.generated.resources.invalid_placement_distinct_row
import blockwise.composeapp.generated.resources.invalid_placement_out_of_bounds
import blockwise.composeapp.generated.resources.invalid_placement_overlap
import blockwise.composeapp.generated.resources.menu
import blockwise.composeapp.generated.resources.moves_remaining
import blockwise.composeapp.generated.resources.new_game
import blockwise.composeapp.generated.resources.ok
import blockwise.composeapp.generated.resources.rule_color_limit_row
import blockwise.composeapp.generated.resources.rule_color_limit_col
import blockwise.composeapp.generated.resources.score
import blockwise.composeapp.generated.resources.select_piece
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

import kotlin.math.abs
import kotlin.math.PI
import kotlin.math.floor
import kotlin.math.hypot
import kotlin.math.roundToInt
import kotlin.math.sin

private data class PieceLayoutInfo(
    val sizePx: IntSize,
)

private data class PieceSpan(
    val widthCells: Int,
    val heightCells: Int,
)

internal data class DragClearPreview(
    val rowIndices: Set<Int> = emptySet(),
    val colIndices: Set<Int> = emptySet(),
)

private data class PieceContentSize(
    val widthPx: Float,
    val heightPx: Float,
)

private data class DragPlacement(
    val originX: Int,
    val originY: Int,
)

private data class HoveredBoardCell(
    val x: Int,
    val y: Int,
)

private data class DragAnchor(
    val dx: Int,
    val dy: Int,
)

private data class DragResolution(
    val placement: DragPlacement?,
    val hoveredCell: HoveredBoardCell?,
    val isFingerInsideBoard: Boolean,
)

internal data class GridAxisGeometry(
    val firstStart: Float,
    val step: Float,
)

internal data class GridGeometry(
    val x: GridAxisGeometry,
    val y: GridAxisGeometry,
    val cellWidth: Float,
    val cellHeight: Float,
)

internal fun cellExtentPx(boardExtentPx: Float, gridCount: Int, gapPx: Float): Float? {
    if (gridCount <= 0) return null
    val totalGap = gapPx * (gridCount - 1).coerceAtLeast(0)
    val cellsExtent = boardExtentPx - totalGap
    if (cellsExtent <= 0f) return null
    return cellsExtent / gridCount
}

internal fun buildGridGeometry(
    gridTopLeftInRoot: Offset,
    gridSizePx: IntSize,
    gridCount: Int,
    gapPx: Float,
): GridGeometry? {
    val cellWidth = cellExtentPx(gridSizePx.width.toFloat(), gridCount, gapPx) ?: return null
    val cellHeight = cellExtentPx(gridSizePx.height.toFloat(), gridCount, gapPx) ?: return null
    return GridGeometry(
        x = GridAxisGeometry(
            firstStart = gridTopLeftInRoot.x,
            step = cellWidth + gapPx,
        ),
        y = GridAxisGeometry(
            firstStart = gridTopLeftInRoot.y,
            step = cellHeight + gapPx,
        ),
        cellWidth = cellWidth,
        cellHeight = cellHeight,
    )
}

private fun Piece.spanInCells(): PieceSpan {
    val maxDx = shape.cells.maxOf { it.dx }
    val maxDy = shape.cells.maxOf { it.dy }
    return PieceSpan(
        widthCells = maxDx + 1,
        heightCells = maxDy + 1,
    )
}

internal fun resolveSnappedOriginAxis(
    targetContentStart: Float,
    axis: GridAxisGeometry,
    maxOrigin: Int,
): Int? {
    if (maxOrigin < 0) return null
    val resolved = ((targetContentStart - axis.firstStart) / axis.step).roundToInt()
    return resolved.coerceIn(0, maxOrigin)
}

internal fun resolveNearestCellAxis(
    pointerPosition: Float,
    axis: GridAxisGeometry,
    cellExtent: Float,
    maxCellIndex: Int,
): Int? {
    if (maxCellIndex < 0) return null
    val resolved = ((pointerPosition - axis.firstStart - cellExtent / 2f) / axis.step).roundToInt()
    return resolved.coerceIn(0, maxCellIndex)
}

internal fun resolvePointerCellAxis(
    pointerPosition: Float,
    axis: GridAxisGeometry,
    cellExtent: Float,
    maxCellIndex: Int,
): Int? {
    if (maxCellIndex < 0) return null
    if (axis.step <= 0f || cellExtent <= 0f) return null

    val relative = pointerPosition - axis.firstStart
    if (relative <= 0f) return 0

    val rawIndex = floor(relative / axis.step).toInt()
    val clampedIndex = rawIndex.coerceIn(0, maxCellIndex)
    val offsetInStride = relative - rawIndex * axis.step
    val gapExtent = (axis.step - cellExtent).coerceAtLeast(0f)

    if (offsetInStride <= cellExtent || gapExtent == 0f) {
        return clampedIndex
    }

    val nextIndex = (rawIndex + 1).coerceIn(0, maxCellIndex)
    val distanceToCurrentCell = offsetInStride - cellExtent
    val distanceToNextCell = axis.step - offsetInStride
    return if (distanceToCurrentCell <= distanceToNextCell) clampedIndex else nextIndex
}

internal fun resolveAttemptedPointerCellAxis(
    pointerPosition: Float,
    axis: GridAxisGeometry,
    cellExtent: Float,
): Int? {
    if (axis.step <= 0f || cellExtent <= 0f) return null

    val relative = pointerPosition - axis.firstStart
    val rawIndex = floor(relative / axis.step).toInt()
    val offsetInStride = relative - rawIndex * axis.step
    val gapExtent = (axis.step - cellExtent).coerceAtLeast(0f)

    if (offsetInStride <= cellExtent || gapExtent == 0f) {
        return rawIndex
    }

    val distanceToCurrentCell = offsetInStride - cellExtent
    val distanceToNextCell = axis.step - offsetInStride
    return if (distanceToCurrentCell <= distanceToNextCell) rawIndex else rawIndex + 1
}

internal fun resolveDraggedOriginAxis(
    targetContentStart: Float,
    axisStarts: List<Float>,
    pieceSpanCells: Int,
): Int? {
    if (axisStarts.isEmpty()) return null
    val candidateCount = (axisStarts.size - pieceSpanCells + 1).coerceAtLeast(0)
    if (candidateCount == 0) return null
    val candidateStarts = axisStarts.sorted().take(candidateCount)
    return candidateStarts.indices.minByOrNull { index ->
        abs(candidateStarts[index] - targetContentStart)
    }
}

internal fun resolveAttemptedDraggedOriginAxis(
    targetContentStart: Float,
    axis: GridAxisGeometry,
): Int? {
    if (axis.step <= 0f) return null
    return ((targetContentStart - axis.firstStart) / axis.step).roundToInt()
}

internal fun normalizeDragStartOffsetInContent(
    startOffsetInPiece: Float,
    pieceContainerInsetPx: Float,
    contentSizePx: Float,
): Float {
    return (startOffsetInPiece - pieceContainerInsetPx).coerceIn(0f, contentSizePx)
}

internal fun resolveDragAnchor(
    piece: Piece,
    offsetInContent: Offset,
    cellWidthPx: Float,
    cellHeightPx: Float,
    gapPx: Float,
): CellOffset {
    return piece.shape.cells.minByOrNull { cell ->
        val centerX = cell.dx * (cellWidthPx + gapPx) + cellWidthPx / 2f
        val centerY = cell.dy * (cellHeightPx + gapPx) + cellHeightPx / 2f
        hypot(offsetInContent.x - centerX, offsetInContent.y - centerY)
    } ?: piece.shape.cells.first()
}

internal fun resolveValidDragOrigin(
    piece: Piece,
    hoveredCell: CellCoord,
    anchor: CellOffset,
    gridCount: Int,
    isFingerInsideBoard: Boolean,
    validOrigins: Set<CellCoord>,
): CellCoord? {
    if (!isFingerInsideBoard) return null

    val span = piece.spanInCells()
    val maxOriginX = (gridCount - span.widthCells).coerceAtLeast(0)
    val maxOriginY = (gridCount - span.heightCells).coerceAtLeast(0)
    val origin = CellCoord(
        x = (hoveredCell.x - anchor.dx).coerceIn(0, maxOriginX),
        y = (hoveredCell.y - anchor.dy).coerceIn(0, maxOriginY),
    )
    return origin.takeIf { it in validOrigins }
}

internal fun resolveAttemptedDragOrigin(
    hoveredCell: CellCoord,
    anchor: CellOffset,
): CellCoord {
    return CellCoord(
        x = hoveredCell.x - anchor.dx,
        y = hoveredCell.y - anchor.dy,
    )
}

internal fun previewCellsForPlacement(piece: Piece, origin: CellCoord?): Set<CellCoord> {
    if (origin == null) return emptySet()
    return piece.shape.cells
        .mapTo(linkedSetOf()) { offset ->
            CellCoord(
                x = origin.x + offset.dx,
                y = origin.y + offset.dy,
            )
        }
}

internal fun previewCellsForOrigins(piece: Piece, origins: Set<CellCoord>): Set<CellCoord> {
    if (origins.isEmpty()) return emptySet()
    return origins.flatMapTo(linkedSetOf()) { origin ->
        piece.shape.cells.map { offset ->
            CellCoord(
                x = origin.x + offset.dx,
                y = origin.y + offset.dy,
            )
        }
    }
}

internal fun previewClearLinesForPlacement(
    grid: Grid,
    piece: Piece,
    origin: CellCoord?,
    rules: GameRules,
): DragClearPreview {
    if (origin == null) return DragClearPreview()
    if (GameEngine.validatePlacement(grid, piece, origin.x, origin.y, rules) != null) {
        return DragClearPreview()
    }

    val result = GameEngine.place(
        grid = grid,
        piece = piece,
        originX = origin.x,
        originY = origin.y,
        rules = rules,
    )
    if (result.ruleViolation != null) return DragClearPreview()

    return DragClearPreview(
        rowIndices = result.clearedRowIndices,
        colIndices = result.clearedColIndices,
    )
}

private fun Piece.boardCellsAt(originX: Int, originY: Int): List<Pair<Int, Int>> {
    return shape.cells
        .map { (originX + it.dx) to (originY + it.dy) }
        .sortedWith(compareBy<Pair<Int, Int>> { it.second }.thenBy { it.first })
}


private fun Offset.debugString(): String = "(${x.roundToInt()},${y.roundToInt()})"

private const val ENABLE_DRAG_DEBUG_LOGS = true

private fun adjustedDragFingerInRoot(rawFingerInRoot: Offset, offsetPx: Float): Offset {
    return rawFingerInRoot.copy(y = rawFingerInRoot.y - offsetPx)
}

private fun placementFailureMessage(language: com.ugurbuga.blockwise.AppLanguage, failure: PlacementFailure): String {
    return when (failure) {
        PlacementFailure.NoPieceSelected -> localizedGetString(language, Res.string.invalid_placement)
        PlacementFailure.OutOfBounds -> localizedGetString(language, Res.string.invalid_placement_out_of_bounds)
        PlacementFailure.Overlap -> localizedGetString(language, Res.string.invalid_placement_overlap)
        is PlacementFailure.Rule -> {
            when (val v = failure.violation) {
                is RuleViolation.TooManySameColorInRow -> localizedGetString(language, Res.string.rule_color_limit_row, v.limit)
                is RuleViolation.TooManySameColorInCol -> localizedGetString(language, Res.string.rule_color_limit_col, v.limit)
                is RuleViolation.TooManyAdjacentSameColorInRow -> localizedGetString(language, Res.string.invalid_placement_adjacent_row, v.limit)
                is RuleViolation.TooManyAdjacentSameColorInCol -> localizedGetString(language, Res.string.invalid_placement_adjacent_col, v.limit)
                is RuleViolation.NotEnoughDistinctColorsInRow -> localizedGetString(language, Res.string.invalid_placement_distinct_row, v.required)
                is RuleViolation.NotEnoughDistinctColorsInCol -> localizedGetString(language, Res.string.invalid_placement_distinct_col, v.required)
            }
        }
    }
}

private fun logDrag(message: String) {
    if (ENABLE_DRAG_DEBUG_LOGS) {
        println("BW_DRAG $message")
    }
}

private fun DragAnchor?.debugString(): String = this?.let { "(${it.dx},${it.dy})" } ?: "null"

private fun HoveredBoardCell?.debugString(): String = this?.let { "(${it.x},${it.y})" } ?: "null"

private fun Piece.spanDebugString(): String {
    val span = spanInCells()
    return "${span.widthCells}x${span.heightCells}"
}

private fun GridGeometry.debugString(): String {
    return "origin=(${x.firstStart.roundToInt()},${y.firstStart.roundToInt()}) cell=${cellWidth.roundToInt()}x${cellHeight.roundToInt()} step=${x.step.roundToInt()}"
}

private fun buildDragLogMessage(
    phase: String,
    sessionId: Int?,
    pieceIndex: Int?,
    piece: Piece?,
    fingerInRoot: Offset?,
    dragOffsetPx: Offset?,
    startOffsetInPiece: Offset?,
    startOffsetInContent: Offset?,
    anchor: DragAnchor?,
    geometry: GridGeometry?,
    resolution: DragResolution?,
    freeOverlayTopLeftInRoot: Offset? = null,
    snappedOverlayTopLeftInRoot: Offset? = null,
    overlayMode: String? = null,
    extra: String? = null,
): String {
    return buildString {
        append("phase=$phase")
        append(" session=")
        append(sessionId ?: -1)
        append(" pieceIndex=")
        append(pieceIndex ?: -1)
        if (piece != null) {
            append(" span=")
            append(piece.spanDebugString())
            append(" cells=")
            append(piece.shape.cells.joinToString(prefix = "[", postfix = "]") { "${it.dx},${it.dy}" })
        }
        append(" finger=")
        append(fingerInRoot?.debugString() ?: "null")
        append(" dragOffset=")
        append(dragOffsetPx?.debugString() ?: "null")
        append(" startPiece=")
        append(startOffsetInPiece?.debugString() ?: "null")
        append(" startContent=")
        append(startOffsetInContent?.debugString() ?: "null")
        append(" anchor=")
        append(anchor.debugString())
        append(" hovered=")
        append(resolution?.hoveredCell.debugString())
        append(" insideBoard=")
        append(resolution?.isFingerInsideBoard ?: false)
        append(" origin=")
        append(resolution?.placement?.let { "(${it.originX},${it.originY})" } ?: "null")
        append(" geometry=")
        append(geometry?.debugString() ?: "null")
        append(" freeOverlay=")
        append(freeOverlayTopLeftInRoot?.debugString() ?: "null")
        append(" snappedOverlay=")
        append(snappedOverlayTopLeftInRoot?.debugString() ?: "null")
        append(" overlayMode=")
        append(overlayMode ?: "null")
        if (extra != null) {
            append(' ')
            append(extra)
        }
    }
}

@Composable
fun BlockLogicScreen(
    modifier: Modifier = Modifier,
    initialSize: GridSize,
    initialDifficulty: Difficulty,
    sessionKey: String,
    onMenu: () -> Unit,
    onRecordScore: (GridSize, Difficulty, Int) -> Unit,
) {
    val vm: BlockLogicViewModel = viewModel(key = sessionKey) {
        BlockLogicViewModel(
            initialSize = initialSize,
            initialDifficulty = initialDifficulty,
        )
    }
    val state by vm.uiState.collectAsState()
    val appLanguage = LocalAppLanguage.current
    val invalidPlacementFeedbackMode = LocalInvalidPlacementFeedbackMode.current

    val snackbarHostState = remember { SnackbarHostState() }
    val snackbarScope = rememberCoroutineScope()
    var snackbarJob by remember { mutableStateOf<Job?>(null) }
    var suppressNextDragPlacementFailure by remember { mutableStateOf(false) }
    var hasRecordedGameOverScore by remember(sessionKey) { mutableStateOf(false) }

    fun recordCurrentScore() {
        onRecordScore(state.gridSize, state.difficulty, state.score)
    }

    fun dismissPlacementMessage() {
        snackbarJob?.cancel()
        snackbarJob = null
        snackbarHostState.currentSnackbarData?.dismiss()
    }

    LaunchedEffect(vm, appLanguage) {
        vm.viewEvent.collect { event ->
            when (event) {
                is BlockLogicViewEvent.PlacementFailed -> {
                    if (suppressNextDragPlacementFailure) {
                        suppressNextDragPlacementFailure = false
                    } else {
                        val msg = placementFailureMessage(appLanguage, event.failure)
                        dismissPlacementMessage()
                        snackbarJob = snackbarScope.launch {
                            snackbarHostState.showSnackbar(msg)
                        }
                    }
                }
                BlockLogicViewEvent.GameOver -> Unit
            }
        }
    }

    LaunchedEffect(state.isGameOver, state.score, state.gridSize, state.difficulty) {
        if (!state.isGameOver) {
            hasRecordedGameOverScore = false
        } else if (!hasRecordedGameOverScore) {
            hasRecordedGameOverScore = true
            recordCurrentScore()
        }
    }

    BlockLogicContent(
        modifier = modifier,
        state = state,
        snackbarHostState = snackbarHostState,
        onNewGame = {
            dismissPlacementMessage()
            suppressNextDragPlacementFailure = false
            recordCurrentScore()
            vm.onNewGame()
        },
        onMenu = {
            recordCurrentScore()
            onMenu()
        },
        onCellTapped = vm::onCellTapped,
        onPieceDropped = vm::onPieceDropped,
        onPieceSelected = vm::onPieceSelected,
        onPieceSelectionCleared = vm::clearPieceSelection,
        onDragPlacementFailed = { failure ->
            if (invalidPlacementFeedbackMode == InvalidPlacementFeedbackMode.WhileDragging) {
                val msg = placementFailureMessage(appLanguage, failure)
                dismissPlacementMessage()
                snackbarJob = snackbarScope.launch {
                    snackbarHostState.showSnackbar(msg)
                }
            }
        },
        onDragPlacementFeedbackCleared = {
            if (invalidPlacementFeedbackMode == InvalidPlacementFeedbackMode.WhileDragging) {
                dismissPlacementMessage()
            }
        },
        onSuppressNextDragDropFailure = {
            suppressNextDragPlacementFailure = true
        },
    )
}

@Composable
fun BlockLogicContent(
    state: BlockLogicUiState,
    snackbarHostState: SnackbarHostState,
    onNewGame: () -> Unit,
    onMenu: () -> Unit,
    onCellTapped: (x: Int, y: Int) -> Unit,
    onPieceDropped: (pieceId: Long, x: Int, y: Int) -> Unit,
    onPieceSelected: (Int) -> Unit,
    onPieceSelectionCleared: () -> Unit,
    onDragPlacementFailed: (PlacementFailure) -> Unit,
    onDragPlacementFeedbackCleared: () -> Unit,
    onSuppressNextDragDropFailure: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dragFingerOffsetPx = LocalDragFingerOffsetLevel.current.offsetPx
    val invalidPlacementFeedbackMode = LocalInvalidPlacementFeedbackMode.current
    val gridGapDp = LocalBlockGapSpacing.current.gapDp
    val density = LocalDensity.current
    var contentTopLeftInRoot by remember { mutableStateOf(Offset.Zero) }
    var gridTopLeftInRoot by remember(state.gridSize) { mutableStateOf(Offset.Zero) }
    var gridSizePx by remember { mutableStateOf(IntSize.Zero) }
    val pieceLayoutInfos = remember { mutableStateMapOf<Long, PieceLayoutInfo>() }
    val pieceCoordinates = remember { mutableStateMapOf<Long, LayoutCoordinates>() }

    var draggingPieceId by remember { mutableStateOf<Long?>(null) }
    var draggingPieceSnapshot by remember { mutableStateOf<Piece?>(null) }
    var draggingOffsetPx by remember { mutableStateOf(Offset.Zero) }
    var dragStartFingerInRoot by remember { mutableStateOf<Offset?>(null) }
    var dragStartOffsetInPiece by remember { mutableStateOf<Offset?>(null) }
    var dragStartOffsetInContent by remember { mutableStateOf<Offset?>(null) }
    var dragAnchor by remember { mutableStateOf<DragAnchor?>(null) }
    var dragSnappedPlacement by remember { mutableStateOf<DragPlacement?>(null) }
    var dragValidOrigins by remember { mutableStateOf<Set<CellCoord>>(emptySet()) }
    var dragValidCells by remember { mutableStateOf<Set<CellCoord>>(emptySet()) }
    var lastDragPlacementFailure by remember { mutableStateOf<PlacementFailure?>(null) }
    var dragLogSessionId by remember { mutableStateOf<Int?>(null) }
    var dragLogSequence by remember { mutableStateOf(0) }

    val gridCount = state.gridSize.value
    val pieceContainerPaddingDp = 0.dp
    val pieceContainerInsetPx = with(density) { pieceContainerPaddingDp.toPx() }
    val gridGapPx = with(density) { gridGapDp.toPx() }
    val rules = remember(state.gridSize, state.difficulty) {
        resolveGameConfig(state.gridSize, state.difficulty).rules
    }
    val activeHighlightedPiece = draggingPieceSnapshot
        ?: state.selectedPieceIndex?.let(state.pieces::getOrNull)
    val highlightedCells = if (draggingPieceId != null) dragValidCells else state.validCells
    val highlightedCellColor = activeHighlightedPiece
        ?.color
        ?.toPaletteColor()
        ?.copy(alpha = 0.15f)
        ?: MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.9f)
    val dragPreviewClear = draggingPieceSnapshot?.let { piece ->
        previewClearLinesForPlacement(
            grid = state.grid,
            piece = piece,
            origin = dragSnappedPlacement?.let { CellCoord(it.originX, it.originY) },
            rules = rules,
        )
    } ?: DragClearPreview()

    fun formatPlacement(piece: Piece, placement: DragPlacement?): String {
        return if (placement == null) {
            "snap=null"
        } else {
            "origin=(${placement.originX},${placement.originY}) cells=${piece.boardCellsAt(placement.originX, placement.originY)}"
        }
    }

    fun clearDragState() {
        val geometry = buildGridGeometry(
            gridTopLeftInRoot = gridTopLeftInRoot,
            gridSizePx = gridSizePx,
            gridCount = gridCount,
            gapPx = gridGapPx,
        )
        logDrag(
            buildDragLogMessage(
                phase = "clear",
                sessionId = dragLogSessionId,
                pieceIndex = state.pieces.indexOfFirst { it.id == draggingPieceId }.takeIf { it >= 0 },
                piece = draggingPieceSnapshot,
                fingerInRoot = dragStartFingerInRoot?.plus(draggingOffsetPx),
                dragOffsetPx = draggingOffsetPx,
                startOffsetInPiece = dragStartOffsetInPiece,
                startOffsetInContent = dragStartOffsetInContent,
                anchor = dragAnchor,
                geometry = geometry,
                resolution = dragSnappedPlacement?.let {
                    DragResolution(
                        placement = it,
                        hoveredCell = null,
                        isFingerInsideBoard = false,
                    )
                },
                extra = "reason=reset"
            )
        )
        draggingPieceId = null
        draggingPieceSnapshot = null
        draggingOffsetPx = Offset.Zero
        dragStartFingerInRoot = null
        dragStartOffsetInPiece = null
        dragStartOffsetInContent = null
        dragAnchor = null
        dragSnappedPlacement = null
        dragValidOrigins = emptySet()
        dragValidCells = emptySet()
        lastDragPlacementFailure = null
        dragLogSessionId = null
    }

    fun updateDragPlacementFeedback(piece: Piece?, resolution: DragResolution?, fingerInRoot: Offset?) {
        if (invalidPlacementFeedbackMode != InvalidPlacementFeedbackMode.WhileDragging) {
            if (lastDragPlacementFailure != null) {
                lastDragPlacementFailure = null
                onDragPlacementFeedbackCleared()
            }
            return
        }

        if (piece == null || fingerInRoot == null || resolution?.isFingerInsideBoard != true) {
            if (lastDragPlacementFailure != null) {
                lastDragPlacementFailure = null
                onDragPlacementFeedbackCleared()
            }
            return
        }

        val failure: PlacementFailure? = if (resolution.placement == null) {
            val geometry = buildGridGeometry(
                gridTopLeftInRoot = gridTopLeftInRoot,
                gridSizePx = gridSizePx,
                gridCount = gridCount,
                gapPx = gridGapPx,
            ) ?: return
            val contentTopLeft = dragStartOffsetInContent?.let { fingerInRoot - it } ?: return
            val span = piece.spanInCells()
            val attemptedPlacement = DragPlacement(
                originX = (resolveAttemptedDraggedOriginAxis(
                    targetContentStart = contentTopLeft.x,
                    axis = geometry.x,
                ) ?: return).coerceAtMost(gridCount - span.widthCells),
                originY = (resolveAttemptedDraggedOriginAxis(
                    targetContentStart = contentTopLeft.y,
                    axis = geometry.y,
                ) ?: return).coerceAtMost(gridCount - span.heightCells),
            )
            GameEngine.validatePlacement(
                state.grid,
                piece,
                attemptedPlacement.originX,
                attemptedPlacement.originY,
                rules,
            )
        } else {
            null
        }

        when {
            failure != null && failure != lastDragPlacementFailure -> {
                lastDragPlacementFailure = failure
                onDragPlacementFailed(failure)
            }
            failure == null && lastDragPlacementFailure != null -> {
                lastDragPlacementFailure = null
                onDragPlacementFeedbackCleared()
            }
        }
    }

    LaunchedEffect(state.pieces.map { it.id }) {
        val activePieceIds = state.pieces.mapTo(mutableSetOf()) { it.id }
        pieceLayoutInfos.keys.toList()
            .filter { it !in activePieceIds }
            .forEach(pieceLayoutInfos::remove)
        pieceCoordinates.keys.toList()
            .filter { it !in activePieceIds }
            .forEach(pieceCoordinates::remove)
        logDrag(
            "phase=piecesChanged count=${state.pieces.size} activeIds=$activePieceIds draggingPieceId=$draggingPieceId"
        )
        if (draggingPieceId != null && draggingPieceId !in activePieceIds) {
            clearDragState()
        }
    }

    fun measuredGridGeometry(): GridGeometry? {
        return buildGridGeometry(
            gridTopLeftInRoot = gridTopLeftInRoot,
            gridSizePx = gridSizePx,
            gridCount = gridCount,
            gapPx = gridGapPx,
        )
    }

    fun freeOverlayTopLeftInRoot(fingerInRoot: Offset, info: PieceLayoutInfo): Offset {
        val offsetInPiece = dragStartOffsetInPiece
        return if (offsetInPiece != null) {
            fingerInRoot - offsetInPiece
        } else {
            Offset(
                x = fingerInRoot.x - info.sizePx.width / 2f,
                y = fingerInRoot.y - info.sizePx.height / 2f,
            )
        }
    }

    fun measuredPieceContentSize(piece: Piece): PieceContentSize? {
        val geometry = measuredGridGeometry() ?: return null
        val span = piece.spanInCells()
        return PieceContentSize(
            widthPx = geometry.cellWidth * span.widthCells + gridGapPx * (span.widthCells - 1).coerceAtLeast(0),
            heightPx = geometry.cellHeight * span.heightCells + gridGapPx * (span.heightCells - 1).coerceAtLeast(0),
        )
    }

    fun snappedOverlayTopLeftInRoot(placement: DragPlacement): Offset? {
        val geometry = measuredGridGeometry() ?: return null
        return Offset(
            x = geometry.x.firstStart + geometry.x.step * placement.originX - pieceContainerInsetPx,
            y = geometry.y.firstStart + geometry.y.step * placement.originY - pieceContainerInsetPx,
        )
    }

    fun isFingerInsideBoard(fingerInRoot: Offset, geometry: GridGeometry): Boolean {
        val boardRight = geometry.x.firstStart + gridSizePx.width
        val boardBottom = geometry.y.firstStart + gridSizePx.height
        return fingerInRoot.x in geometry.x.firstStart..boardRight &&
            fingerInRoot.y in geometry.y.firstStart..boardBottom
    }

    fun draggedContentTopLeftInRoot(fingerInRoot: Offset): Offset? {
        val offsetInContent = dragStartOffsetInContent ?: return null
        return fingerInRoot - offsetInContent
    }

    fun computeDragResolution(piece: Piece, fingerInRoot: Offset): DragResolution? {
        val geometry = measuredGridGeometry() ?: return null
        val validOrigins = if (draggingPieceId != null) dragValidOrigins else state.validOrigins
        val span = piece.spanInCells()
        val contentTopLeft = draggedContentTopLeftInRoot(fingerInRoot) ?: return null
        val isInsideBoard = isFingerInsideBoard(fingerInRoot, geometry)
        val maxOriginX = (gridCount - span.widthCells).coerceAtLeast(0)
        val maxOriginY = (gridCount - span.heightCells).coerceAtLeast(0)
        val originX = resolveSnappedOriginAxis(
            targetContentStart = contentTopLeft.x,
            axis = geometry.x,
            maxOrigin = maxOriginX,
        ) ?: return null
        val originY = resolveSnappedOriginAxis(
            targetContentStart = contentTopLeft.y,
            axis = geometry.y,
            maxOrigin = maxOriginY,
        ) ?: return null
        val origin = CellCoord(x = originX, y = originY).takeIf {
            isInsideBoard && it in validOrigins
        }
        val hoveredCell = dragAnchor?.let { anchor ->
            HoveredBoardCell(
                x = (originX + anchor.dx).coerceIn(0, gridCount - 1),
                y = (originY + anchor.dy).coerceIn(0, gridCount - 1),
            )
        }

        return DragResolution(
            placement = origin?.let {
                DragPlacement(
                    originX = it.x,
                    originY = it.y,
                )
            },
            hoveredCell = hoveredCell,
            isFingerInsideBoard = isInsideBoard,
        )
    }

    fun computeAttemptedDragPlacement(piece: Piece, fingerInRoot: Offset): DragPlacement? {
        val geometry = measuredGridGeometry() ?: return null
        val span = piece.spanInCells()
        val contentTopLeft = draggedContentTopLeftInRoot(fingerInRoot) ?: return null
        val originX = resolveAttemptedDraggedOriginAxis(
            targetContentStart = contentTopLeft.x,
            axis = geometry.x,
        ) ?: return null
        val originY = resolveAttemptedDraggedOriginAxis(
            targetContentStart = contentTopLeft.y,
            axis = geometry.y,
        ) ?: return null
        return DragPlacement(
            originX = originX.coerceAtMost(gridCount - span.widthCells),
            originY = originY.coerceAtMost(gridCount - span.heightCells),
        )
    }

    val gridCellSizeDp = remember(gridSizePx, gridCount, gridGapDp, density) {
        if (gridSizePx.width <= 0 || gridCount <= 0) 16.dp
        else with(density) {
            val gapPx = gridGapDp.toPx()
            val cellPx = (gridSizePx.width.toFloat() - gapPx * (gridCount - 1)) / gridCount
            cellPx.toDp()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .onGloballyPositioned { coords ->
                contentTopLeftInRoot = coords.positionInRoot()
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = stringResource(Res.string.score, state.score),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                state.movesRemaining?.let { movesRemaining ->
                    Text(
                        text = stringResource(Res.string.moves_remaining, movesRemaining),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(onClick = onMenu) {
                    Text(stringResource(Res.string.menu))
                }
                Spacer(Modifier.width(8.dp))
                Button(onClick = onNewGame) {
                    Text(stringResource(Res.string.new_game))
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp)
                .zIndex(100f),
            contentAlignment = Alignment.TopCenter,
        ) {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                snackbar = { data ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.errorContainer,
                        tonalElevation = 2.dp,
                    ) {
                        Text(
                            text = data.visuals.message,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            )
        }

            GridView(
                grid = state.grid,
                highlightedCells = highlightedCells,
                highlightedCellColor = highlightedCellColor,
                clearingRows = state.clearingRows,
                clearingCols = state.clearingCols,
                previewClearingRows = dragPreviewClear.rowIndices,
                previewClearingCols = dragPreviewClear.colIndices,
                onCellTapped = onCellTapped,
                onGridMeasured = { topLeftInRoot, sizePx ->
                    gridTopLeftInRoot = topLeftInRoot
                    gridSizePx = sizePx
                },
                cellSize = gridCellSizeDp,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            )

        Text(
            text = stringResource(Res.string.select_piece),
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )

            PiecesRow(
                pieces = state.pieces,
                selectedIndex = state.selectedPieceIndex,
                draggingPieceId = draggingPieceId,
                onPieceSelected = onPieceSelected,
                onPieceMeasured = { pieceId, info -> pieceLayoutInfos[pieceId] = info },
                onPieceCoordinates = { pieceId, coords -> pieceCoordinates[pieceId] = coords },
                onDragStart = { index, piece, startOffsetPx ->
                    if (!state.isGameOver && !state.isAnimatingClear) {
                        val coords = pieceCoordinates[piece.id]
                        val info = pieceLayoutInfos[piece.id]
                        if (coords == null || info == null) {
                            logDrag(
                                buildDragLogMessage(
                                    phase = "start-blocked",
                                    sessionId = dragLogSessionId,
                                    pieceIndex = index,
                                    piece = piece,
                                    fingerInRoot = null,
                                    dragOffsetPx = null,
                                    startOffsetInPiece = startOffsetPx,
                                    startOffsetInContent = null,
                                    anchor = null,
                                    geometry = measuredGridGeometry(),
                                    resolution = null,
                                    extra = "coordsMissing=${coords == null} infoMissing=${info == null}"
                                )
                            )
                            return@PiecesRow
                        }

                        onPieceSelected(index)
                        dragLogSequence += 1
                        dragLogSessionId = dragLogSequence
                        draggingPieceId = piece.id
                        draggingPieceSnapshot = piece
                        val validOrigins = GameEngine.findValidOrigins(state.grid, piece, rules)
                        dragValidOrigins = validOrigins
                        draggingOffsetPx = Offset.Zero
                        val startFinger = coords.localToRoot(startOffsetPx)
                        val displayedStartFinger = adjustedDragFingerInRoot(startFinger, dragFingerOffsetPx)
                        dragStartFingerInRoot = startFinger
                        dragStartOffsetInPiece = startOffsetPx

                        val geometry = measuredGridGeometry()
                        dragStartOffsetInContent = measuredPieceContentSize(piece)?.let { contentSize ->
                            Offset(
                                x = normalizeDragStartOffsetInContent(
                                    startOffsetInPiece = startOffsetPx.x,
                                    pieceContainerInsetPx = pieceContainerInsetPx,
                                    contentSizePx = contentSize.widthPx,
                                ),
                                y = normalizeDragStartOffsetInContent(
                                    startOffsetInPiece = startOffsetPx.y,
                                    pieceContainerInsetPx = pieceContainerInsetPx,
                                    contentSizePx = contentSize.heightPx,
                                ),
                            )
                        }
                        dragAnchor = if (geometry != null) {
                            dragStartOffsetInContent?.let { offsetInContent ->
                                resolveDragAnchor(
                                    piece = piece,
                                    offsetInContent = offsetInContent,
                                    cellWidthPx = geometry.cellWidth,
                                    cellHeightPx = geometry.cellHeight,
                                    gapPx = gridGapPx,
                                ).let { anchorCell ->
                                    DragAnchor(dx = anchorCell.dx, dy = anchorCell.dy)
                                }
                            }
                        } else {
                            null
                        }
                        val resolution = if (geometry != null) computeDragResolution(piece, displayedStartFinger) else null
                        dragSnappedPlacement = resolution?.placement
                        dragValidCells = previewCellsForPlacement(
                            piece = piece,
                            origin = resolution?.placement?.let { p -> CellCoord(x = p.originX, y = p.originY) },
                        )
                        updateDragPlacementFeedback(piece, resolution, displayedStartFinger)
                        logDrag(
                            buildDragLogMessage(
                                phase = "start",
                                sessionId = dragLogSessionId,
                                pieceIndex = index,
                                piece = piece,
                                fingerInRoot = displayedStartFinger,
                                dragOffsetPx = draggingOffsetPx,
                                startOffsetInPiece = dragStartOffsetInPiece,
                                startOffsetInContent = dragStartOffsetInContent,
                                anchor = dragAnchor,
                                geometry = geometry,
                                resolution = resolution,
                                freeOverlayTopLeftInRoot = freeOverlayTopLeftInRoot(displayedStartFinger, info),
                                snappedOverlayTopLeftInRoot = resolution?.placement?.let(::snappedOverlayTopLeftInRoot),
                                overlayMode = if (resolution?.placement != null) "snapped" else "free-outside-board",
                                extra = formatPlacement(piece, resolution?.placement)
                            )
                        )
                    } else {
                        logDrag(
                            buildDragLogMessage(
                                phase = "start-blocked",
                                sessionId = dragLogSessionId,
                                pieceIndex = index,
                                piece = piece,
                                fingerInRoot = null,
                                dragOffsetPx = null,
                                startOffsetInPiece = startOffsetPx,
                                startOffsetInContent = null,
                                anchor = null,
                                geometry = measuredGridGeometry(),
                                resolution = null,
                                extra = "gameOver=${state.isGameOver} animating=${state.isAnimatingClear}"
                            )
                        )
                    }
                },
                onDrag = { delta ->
                    if (draggingPieceId == null || draggingPieceSnapshot == null || dragStartFingerInRoot == null) {
                        return@PiecesRow
                    }
                    val newOffset = draggingOffsetPx + delta
                    val pieceId = draggingPieceId
                    val startFinger = dragStartFingerInRoot
                    val piece = draggingPieceSnapshot
                    val currentFinger = startFinger?.plus(newOffset)
                    val displayedFinger = currentFinger?.let { adjustedDragFingerInRoot(it, dragFingerOffsetPx) }
                    val geometry = measuredGridGeometry()
                    val resolution = if (piece != null && displayedFinger != null) {
                        computeDragResolution(piece, displayedFinger)
                    } else {
                        null
                    }
                    draggingOffsetPx = newOffset
                    dragSnappedPlacement = resolution?.placement
                    dragValidCells = piece?.let {
                        previewCellsForPlacement(
                            piece = it,
                            origin = resolution?.placement?.let { p -> CellCoord(x = p.originX, y = p.originY) },
                        )
                    } ?: emptySet()
                    updateDragPlacementFeedback(piece, resolution, displayedFinger)
                    logDrag(
                        buildDragLogMessage(
                            phase = "move",
                            sessionId = dragLogSessionId,
                            pieceIndex = state.pieces.indexOfFirst { it.id == pieceId }.takeIf { it >= 0 },
                            piece = piece,
                            fingerInRoot = displayedFinger,
                            dragOffsetPx = newOffset,
                            startOffsetInPiece = dragStartOffsetInPiece,
                            startOffsetInContent = dragStartOffsetInContent,
                            anchor = dragAnchor,
                            geometry = geometry,
                            resolution = resolution,
                            freeOverlayTopLeftInRoot = if (displayedFinger != null && pieceId != null) {
                                pieceLayoutInfos[pieceId]?.let { info -> freeOverlayTopLeftInRoot(displayedFinger, info) }
                            } else null,
                            snappedOverlayTopLeftInRoot = resolution?.placement?.let(::snappedOverlayTopLeftInRoot),
                            overlayMode = when {
                                resolution == null -> "no-geometry"
                                resolution.placement != null -> "snapped"
                                else -> "free-outside-board"
                            },
                            extra = piece?.let { formatPlacement(it, resolution?.placement) }
                        )
                    )
                },
                onDragEnd = {
                    if (draggingPieceId == null || draggingPieceSnapshot == null || dragStartFingerInRoot == null) {
                        return@PiecesRow
                    }
                    val pieceId = draggingPieceId
                    val piece = draggingPieceSnapshot
                    val rawFinger = dragStartFingerInRoot?.plus(draggingOffsetPx)
                    val finger = rawFinger?.let { adjustedDragFingerInRoot(it, dragFingerOffsetPx) }
                    val geometry = measuredGridGeometry()
                    val resolution = if (piece != null && finger != null) computeDragResolution(piece, finger) else null
                    val attemptedPlacement = if (piece != null && finger != null) {
                        computeAttemptedDragPlacement(piece, finger)
                    } else {
                        null
                    }
                    val placement = if (resolution != null) {
                        resolution.placement
                    } else {
                        dragSnappedPlacement
                    }

                    placement?.let { resolvedPlacement ->
                        logDrag(
                            buildDragLogMessage(
                                phase = "end-drop",
                                sessionId = dragLogSessionId,
                                pieceIndex = state.pieces.indexOfFirst { it.id == pieceId }.takeIf { it >= 0 },
                                piece = piece,
                                fingerInRoot = finger,
                                dragOffsetPx = draggingOffsetPx,
                                startOffsetInPiece = dragStartOffsetInPiece,
                                startOffsetInContent = dragStartOffsetInContent,
                                anchor = dragAnchor,
                                geometry = geometry,
                                resolution = resolution,
                                freeOverlayTopLeftInRoot = if (finger != null && pieceId != null) {
                                    pieceLayoutInfos[pieceId]?.let { info -> freeOverlayTopLeftInRoot(finger, info) }
                                } else null,
                                snappedOverlayTopLeftInRoot = snappedOverlayTopLeftInRoot(resolvedPlacement),
                                overlayMode = "drop-snapped",
                                extra = piece?.let { formatPlacement(it, resolvedPlacement) }
                            )
                        )
                        if (pieceId != null) {
                            onPieceDropped(pieceId, resolvedPlacement.originX, resolvedPlacement.originY)
                        }
                    } ?: run {
                        val shouldReportInvalidDrop = resolution?.isFingerInsideBoard == true && attemptedPlacement != null
                        if (shouldReportInvalidDrop) {
                            val attempted = attemptedPlacement
                            if (invalidPlacementFeedbackMode == InvalidPlacementFeedbackMode.WhileDragging) {
                                onSuppressNextDragDropFailure()
                            }
                            logDrag(
                                buildDragLogMessage(
                                    phase = "end-invalid-drop",
                                    sessionId = dragLogSessionId,
                                    pieceIndex = state.pieces.indexOfFirst { it.id == pieceId }.takeIf { it >= 0 },
                                    piece = piece,
                                    fingerInRoot = finger,
                                    dragOffsetPx = draggingOffsetPx,
                                    startOffsetInPiece = dragStartOffsetInPiece,
                                    startOffsetInContent = dragStartOffsetInContent,
                                    anchor = dragAnchor,
                                    geometry = geometry,
                                    resolution = resolution,
                                    freeOverlayTopLeftInRoot = if (finger != null && pieceId != null) {
                                        pieceLayoutInfos[pieceId]?.let { info -> freeOverlayTopLeftInRoot(finger, info) }
                                    } else null,
                                    snappedOverlayTopLeftInRoot = snappedOverlayTopLeftInRoot(attempted),
                                    overlayMode = "drop-invalid-inside-board",
                                    extra = piece?.let { draggedPiece -> formatPlacement(draggedPiece, attempted) },
                                )
                            )
                            if (pieceId != null) {
                                onPieceDropped(pieceId, attempted.originX, attempted.originY)
                            }
                        } else {
                            logDrag(
                                buildDragLogMessage(
                                    phase = "end-no-drop",
                                    sessionId = dragLogSessionId,
                                    pieceIndex = state.pieces.indexOfFirst { it.id == pieceId }.takeIf { it >= 0 },
                                    piece = piece,
                                    fingerInRoot = finger,
                                    dragOffsetPx = draggingOffsetPx,
                                    startOffsetInPiece = dragStartOffsetInPiece,
                                    startOffsetInContent = dragStartOffsetInContent,
                                    anchor = dragAnchor,
                                    geometry = geometry,
                                    resolution = resolution,
                                    freeOverlayTopLeftInRoot = if (finger != null && pieceId != null) {
                                        pieceLayoutInfos[pieceId]?.let { info -> freeOverlayTopLeftInRoot(finger, info) }
                                    } else null,
                                    snappedOverlayTopLeftInRoot = null,
                                    overlayMode = if (resolution?.isFingerInsideBoard == true) "free-inside-board" else "free-outside-board",
                                    extra = "snap=null"
                                )
                            )
                            onPieceSelectionCleared()
                        }
                    }
                    onDragPlacementFeedbackCleared()
                    clearDragState()
                },
                onDragCancel = {
                    if (draggingPieceId == null || draggingPieceSnapshot == null || dragStartFingerInRoot == null) {
                        return@PiecesRow
                    }
                    val pieceId = draggingPieceId
                    val piece = draggingPieceSnapshot
                    val rawFinger = dragStartFingerInRoot?.plus(draggingOffsetPx)
                    val finger = rawFinger?.let { adjustedDragFingerInRoot(it, dragFingerOffsetPx) }
                    val geometry = measuredGridGeometry()
                    val resolution = if (piece != null && finger != null) computeDragResolution(piece, finger) else null
                    logDrag(
                        buildDragLogMessage(
                            phase = "cancel",
                            sessionId = dragLogSessionId,
                            pieceIndex = state.pieces.indexOfFirst { it.id == pieceId }.takeIf { it >= 0 },
                            piece = piece,
                            fingerInRoot = finger,
                            dragOffsetPx = draggingOffsetPx,
                            startOffsetInPiece = dragStartOffsetInPiece,
                            startOffsetInContent = dragStartOffsetInContent,
                            anchor = dragAnchor,
                            geometry = geometry,
                            resolution = resolution,
                            freeOverlayTopLeftInRoot = if (finger != null && pieceId != null) {
                                pieceLayoutInfos[pieceId]?.let { info -> freeOverlayTopLeftInRoot(finger, info) }
                            } else null,
                            snappedOverlayTopLeftInRoot = dragSnappedPlacement?.let(::snappedOverlayTopLeftInRoot),
                            overlayMode = if (dragSnappedPlacement != null) "cancel-snapped" else "cancel-free",
                            extra = piece?.let { formatPlacement(it, dragSnappedPlacement) }
                        )
                    )
                    onDragPlacementFeedbackCleared()
                    onPieceSelectionCleared()
                    clearDragState()
                },
                cellSize = gridCellSizeDp,
                containerPadding = pieceContainerPaddingDp,
            )
        }

        val dragPieceId = draggingPieceId
        if (dragPieceId != null) {
            val piece = draggingPieceSnapshot
            val info = pieceLayoutInfos[dragPieceId]
            val startFingerInRoot = dragStartFingerInRoot
            if (piece != null && info != null && startFingerInRoot != null) {
                val fingerInRoot = adjustedDragFingerInRoot(startFingerInRoot + draggingOffsetPx, dragFingerOffsetPx)
                val freeOverlayTopLeftInRoot = freeOverlayTopLeftInRoot(fingerInRoot, info)
                val freeOverlayTopLeftLocal = freeOverlayTopLeftInRoot - contentTopLeftInRoot
                val resolution = computeDragResolution(piece, fingerInRoot)
                val placement = resolution?.placement
                val isInvalidPlacement = resolution?.isFingerInsideBoard == true && placement == null
                val dragBorderBaseColor = piece.color.toPaletteColor()
                val invalidBorderColor = Color(0xFF7A0F1A)

                Box(
                    modifier = Modifier
                        .graphicsLayer {
                            translationX = freeOverlayTopLeftLocal.x
                            translationY = freeOverlayTopLeftLocal.y
                            alpha = 0.92f
                        }
                        .zIndex(10f)
                        .padding(pieceContainerPaddingDp)
                ) {
                    PiecePreview(
                        piece = piece,
                        cellSize = gridCellSizeDp,
                        pressed = true,
                        borderColor = if (isInvalidPlacement) invalidBorderColor else dragBorderBaseColor,
                        borderWidth = dragPreviewBorderWidth(isInvalidPlacement),
                    )
                }
            }
        }

        if (state.isGameOver) {
            AlertDialog(
                onDismissRequest = onMenu,
                title = { Text(stringResource(Res.string.game_over)) },
                text = { Text(stringResource(Res.string.game_over_message)) },
                confirmButton = {
                    Button(onClick = onNewGame) {
                        Text(stringResource(Res.string.new_game))
                    }
                },
                dismissButton = {
                    TextButton(onClick = onMenu) {
                        Text(stringResource(Res.string.ok))
                    }
                },
            )
        }
    }
}

@Composable
private fun GridView(
    grid: com.ugurbuga.blockwise.blocklogic.domain.Grid,
    highlightedCells: Set<CellCoord>,
    highlightedCellColor: Color,
    clearingRows: Set<Int>,
    clearingCols: Set<Int>,
    previewClearingRows: Set<Int>,
    previewClearingCols: Set<Int>,
    onCellTapped: (x: Int, y: Int) -> Unit,
    onGridMeasured: (topLeftInRoot: Offset, sizePx: IntSize) -> Unit,
    cellSize: Dp,
    modifier: Modifier = Modifier,
) {
    val selectedBlockStyle = LocalBlockVisualStyle.current
    val boardBlockStyleMode = LocalBoardBlockStyleMode.current
    val boardShapeStyle = resolveBoardBlockShapeStyle(boardBlockStyleMode, selectedBlockStyle)
    val emptyCellRenderStyle = resolveBoardEmptyBlockRenderStyle(boardBlockStyleMode, selectedBlockStyle)
    val filledCellRenderStyle = resolveBoardFilledBlockRenderStyle(boardBlockStyleMode, selectedBlockStyle)
    val boardColorStyle = BlockVisualStyle.Flat
    val gap = LocalBlockGapSpacing.current.gapDp
    val density = LocalDensity.current
    val appColorPalette = LocalAppColorPalette.current
    val useDarkTheme = LocalPaletteIsDarkTheme.current
    val paletteColors = BlockWisePalette.blockColors(
        palette = LocalBlockColorPalette.current,
        darkTheme = useDarkTheme,
    )
    val themePalette = BlockWisePalette.themePalette(appColorPalette, useDarkTheme)
    val paletteAccent = themedBoardPaletteAccent(
        appColorPalette = appColorPalette,
        style = boardColorStyle,
        colors = listOf(
            paletteColors.red,
            paletteColors.green,
            paletteColors.blue,
            paletteColors.yellow,
        ),
    )
    val boardBaseSurfaceColor = themedBoardBaseSurfaceColor(
        appColorPalette = appColorPalette,
        style = boardColorStyle,
        themePalette = themePalette,
    )
    val resolvedBoardBaseSurfaceColor = if (useDarkTheme) {
        boardBaseSurfaceColor
    } else {
        blendColors(boardBaseSurfaceColor, Color.Black, 0.035f)
    }
    val boardBaseBorderColor = themedBoardBaseBorderColor(
        appColorPalette = appColorPalette,
        style = boardColorStyle,
        themePalette = themePalette,
    )
    val emptyCellColor = themedEmptyBoardCellColor(
        baseColor = resolvedBoardBaseSurfaceColor,
        paletteAccent = paletteAccent,
        style = boardColorStyle,
    )
    val emptyCellBorderColor = themedEmptyBoardCellBorderColor(
        baseColor = boardBaseBorderColor,
        paletteAccent = paletteAccent,
        style = boardColorStyle,
    )
    val highlightedEmptyCellColor = themedHighlightedBoardCellColor(
        baseColor = emptyCellColor,
        highlightColor = highlightedCellColor,
        style = boardColorStyle,
    )
    val highlightedEmptyCellBorderColor = themedHighlightedBoardCellBorderColor(
        baseColor = emptyCellBorderColor,
        highlightColor = highlightedCellColor,
        style = boardColorStyle,
    )
    val previewClearTransition = rememberInfiniteTransition(label = "preview-clear-lines")
    val previewClearScale = previewClearTransition.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.075f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 560, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "preview-clear-scale",
    ).value
    val previewClearAccent = previewClearTransition.animateFloat(
        initialValue = 0.24f,
        targetValue = 0.72f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 560, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "preview-clear-accent",
    ).value
    val hasActiveClearAnimation = clearingRows.isNotEmpty() || clearingCols.isNotEmpty()
    val clearAnimationProgress = remember { Animatable(0f) }
    LaunchedEffect(clearingRows, clearingCols) {
        if (hasActiveClearAnimation) {
            clearAnimationProgress.stop()
            clearAnimationProgress.snapTo(0f)
            clearAnimationProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = CLEAR_ANIMATION_DURATION_MS.toInt(),
                    easing = FastOutSlowInEasing,
                ),
            )
        } else {
            clearAnimationProgress.stop()
            clearAnimationProgress.snapTo(0f)
        }
    }
    val clearProgress = if (hasActiveClearAnimation) clearAnimationProgress.value else 0f
    val clearFlashStrength = when {
        clearProgress <= 0f -> 0f
        clearProgress < 0.18f -> clearProgress / 0.18f
        clearProgress < 0.46f -> 1f
        else -> (1f - (clearProgress - 0.46f) / 0.54f).coerceIn(0f, 1f)
    }
    val clearDissolveStrength = clearProgress.coerceIn(0f, 1f)
    val boardShakeAmplitudePx = with(density) { 8.dp.toPx() }
    val boardShakeDecay = (1f - clearProgress).coerceIn(0f, 1f)
    val boardShakeX = if (hasActiveClearAnimation && clearingCols.isNotEmpty()) {
        sin(clearProgress * PI.toFloat() * 14f) * boardShakeAmplitudePx * boardShakeDecay
    } else {
        0f
    }
    val boardShakeY = if (hasActiveClearAnimation && clearingRows.isNotEmpty()) {
        sin(clearProgress * PI.toFloat() * 12f) * boardShakeAmplitudePx * 0.72f * boardShakeDecay
    } else {
        0f
    }
    val boardCornerRadius = (cellSize * 0.22f).coerceAtLeast(6.dp)
    val size = grid.size.value
    Column(
        modifier = modifier
            .graphicsLayer {
                translationX = boardShakeX
                translationY = boardShakeY
            }
            .onGloballyPositioned { coords ->
                onGridMeasured(coords.positionInRoot(), coords.size)
            },
        verticalArrangement = Arrangement.spacedBy(gap),
    ) {
        for (y in 0 until size) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(gap)) {
                for (x in 0 until size) {
                    val cell = grid[x, y]
                    val isHighlightedCell = CellCoord(x, y) in highlightedCells
                    val isClearingCell = y in clearingRows || x in clearingCols
                    val isPreviewClearingCell = !isClearingCell &&
                        (y in previewClearingRows || x in previewClearingCols)
                    val isLockedCell = cell?.isLocked == true
                    val isPrefilledCell = cell?.isPrefilled == true
                    val isCrossClearCell = y in clearingRows && x in clearingCols
                    val cellFillColor = when {
                        cell != null -> cell.color.toPaletteColor()
                        isHighlightedCell -> highlightedEmptyCellColor
                        else -> emptyCellColor
                    }
                    val cellBorderColor = when {
                        isLockedCell -> MaterialTheme.colorScheme.primary
                        isPrefilledCell -> MaterialTheme.colorScheme.secondary
                        isHighlightedCell -> highlightedEmptyCellBorderColor
                        else -> emptyCellBorderColor
                    }
                    val previewFillColor = if (isPreviewClearingCell) {
                        blendColors(
                            cellFillColor,
                            paletteAccent.lighten(0.42f).copy(alpha = 1f),
                            0.26f + previewClearAccent * 0.5f,
                        )
                    } else {
                        cellFillColor
                    }
                    val previewBorderColor = if (isPreviewClearingCell) {
                        blendColors(
                            cellBorderColor,
                            paletteAccent.lighten(0.54f).copy(alpha = 1f),
                            0.42f + previewClearAccent * 0.44f,
                        )
                    } else {
                        cellBorderColor
                    }
                    val clearingFillColor = if (isClearingCell) {
                        blendColors(
                            blendColors(
                                previewFillColor,
                                paletteAccent.lighten(0.62f).copy(alpha = 1f),
                                0.34f + clearFlashStrength * 0.34f,
                            ),
                            Color.White.copy(alpha = 1f),
                            0.18f + clearFlashStrength * if (isCrossClearCell) 0.42f else 0.3f,
                        )
                    } else {
                        previewFillColor
                    }
                    val clearingBorderColor = if (isClearingCell) {
                        blendColors(
                            previewBorderColor,
                            Color.White.copy(alpha = 1f),
                            0.38f + clearFlashStrength * if (isCrossClearCell) 0.48f else 0.34f,
                        )
                    } else {
                        previewBorderColor
                    }
                    val previewHighlightBrush = if (isPreviewClearingCell) {
                        Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = (0.3f + previewClearAccent * 0.26f).coerceIn(0f, 0.72f)),
                                paletteAccent.lighten(0.5f).copy(alpha = (0.18f + previewClearAccent * 0.2f).coerceIn(0f, 0.54f)),
                                Color.Transparent,
                            ),
                        )
                    } else {
                        null
                    }
                    val previewTopSheenBrush = if (isPreviewClearingCell) {
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = (0.28f + previewClearAccent * 0.22f).coerceIn(0f, 0.6f)),
                                paletteAccent.lighten(0.32f).copy(alpha = (0.12f + previewClearAccent * 0.12f).coerceIn(0f, 0.28f)),
                                Color.Transparent,
                            ),
                        )
                    } else {
                        null
                    }
                    val clearingHighlightBrush = if (isClearingCell) {
                        Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = (0.38f + clearFlashStrength * 0.34f).coerceIn(0f, 0.92f)),
                                paletteAccent.lighten(0.7f).copy(alpha = (0.26f + clearFlashStrength * 0.26f).coerceIn(0f, 0.68f)),
                                Color.Transparent,
                            ),
                        )
                    } else {
                        null
                    }
                    val clearingStreakBrush = if (isClearingCell) {
                        Brush.linearGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.White.copy(alpha = (0.32f + clearFlashStrength * 0.3f).coerceIn(0f, 0.86f)),
                                paletteAccent.lighten(0.64f).copy(alpha = (0.22f + clearFlashStrength * 0.22f).coerceIn(0f, 0.58f)),
                                Color.Transparent,
                            ),
                        )
                    } else {
                        null
                    }
                    val previewPulseScale = if (isPreviewClearingCell) previewClearScale else 1f
                    val previewPulseAlpha = if (isPreviewClearingCell) {
                        (0.96f + previewClearAccent * 0.08f).coerceIn(0f, 1f)
                    } else {
                        1f
                    }
                    val actualClearAlpha = if (isClearingCell) {
                        (1f - clearDissolveStrength * 0.72f).coerceIn(0f, 1f)
                    } else {
                        1f
                    }
                    val actualClearScale = if (isClearingCell) {
                        val pop = 1f + clearFlashStrength * if (isCrossClearCell) 0.16f else 0.1f
                        val collapse = 1f - clearDissolveStrength * if (isCrossClearCell) 0.92f else 0.84f
                        maxOf(0.08f, pop * collapse)
                    } else {
                        1f
                    }
                    val actualClearRotation = if (isClearingCell) {
                        sin(clearProgress * PI.toFloat() * 10f) *
                            (if (isCrossClearCell) 3.4f else 2.2f) *
                            boardShakeDecay
                    } else {
                        0f
                    }
                    BlockTile3D(
                        fillColor = clearingFillColor,
                        borderColor = clearingBorderColor,
                        borderWidth = when {
                            isClearingCell -> if (isCrossClearCell) 3.8.dp else 3.2.dp
                            isLockedCell -> 2.dp
                            isPreviewClearingCell -> 2.6.dp
                            else -> 1.dp
                        },
                        cornerRadius = boardCornerRadius,
                        recessed = cell == null,
                        elevation = when {
                            isClearingCell -> if (isCrossClearCell) 10.dp else 8.dp
                            isPreviewClearingCell -> 6.dp
                            cell != null -> 3.dp
                            else -> 0.dp
                        },
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .graphicsLayer {
                                alpha = previewPulseAlpha * actualClearAlpha
                                scaleX = previewPulseScale * actualClearScale
                                scaleY = previewPulseScale * actualClearScale
                                rotationZ = actualClearRotation
                            }
                            .clickable { onCellTapped(x, y) },
                        renderStyleOverride = when {
                            isClearingCell -> BlockVisualStyle.Flat
                            cell != null -> filledCellRenderStyle
                            else -> emptyCellRenderStyle
                        },
                        shapeStyleOverride = boardShapeStyle,
                    ) {
                        clearingHighlightBrush?.let { brush ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .graphicsLayer {
                                        alpha = (0.24f + clearFlashStrength * 0.22f).coerceIn(0f, 0.46f)
                                    }
                                    .background(brush)
                            )
                        }
                        clearingStreakBrush?.let { brush ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.74f)
                                    .fillMaxHeight(1.12f)
                                    .align(Alignment.Center)
                                    .graphicsLayer {
                                        rotationZ = if (y in clearingRows && x !in clearingCols) {
                                            90f
                                        } else if (x in clearingCols && y !in clearingRows) {
                                            0f
                                        } else {
                                            45f
                                        }
                                        alpha = (0.38f + clearFlashStrength * 0.28f).coerceIn(0f, 0.72f)
                                    }
                                    .background(brush)
                            )
                        }
                        previewHighlightBrush?.let { brush ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .graphicsLayer {
                                        alpha = (0.18f + previewClearAccent * 0.16f).coerceIn(0f, 0.34f)
                                    }
                                    .background(brush)
                            )
                        }
                        previewTopSheenBrush?.let { brush ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(0.28f)
                                    .align(Alignment.TopCenter)
                                    .graphicsLayer {
                                        alpha = (0.5f + previewClearAccent * 0.2f).coerceIn(0f, 0.82f)
                                    }
                                    .background(brush)
                            )
                        }
                    }
                }
            }
        }
    }
}

internal fun themedBoardPaletteAccent(
    appColorPalette: AppColorPalette,
    style: BlockVisualStyle,
    colors: List<Color>,
): Color {
    val average = averageColor(colors)
    val vivid = colors.maxByOrNull(::colorVividness) ?: average
    val vividTarget = when (style) {
        BlockVisualStyle.Flat -> vivid
        BlockVisualStyle.Bubble -> vivid.lighten(0.1f)
        BlockVisualStyle.Outline -> vivid
        BlockVisualStyle.Sharp3D -> vivid.lighten(0.02f)
        BlockVisualStyle.Wood -> vivid.lighten(0.03f)
        BlockVisualStyle.LiquidGlass -> vivid.lighten(0.1f)
        BlockVisualStyle.Neon -> vivid.lighten(0.16f)
    }
    val amount = when (appColorPalette) {
        AppColorPalette.Classic -> when (style) {
            BlockVisualStyle.Flat -> 0.14f
            BlockVisualStyle.Bubble -> 0.22f
            BlockVisualStyle.Outline -> 0.14f
            BlockVisualStyle.Sharp3D -> 0.17f
            BlockVisualStyle.Wood -> 0.19f
            BlockVisualStyle.LiquidGlass -> 0.24f
            BlockVisualStyle.Neon -> 0.3f
        }

        AppColorPalette.Aurora -> when (style) {
            BlockVisualStyle.Flat -> 0.16f
            BlockVisualStyle.Bubble -> 0.24f
            BlockVisualStyle.Outline -> 0.16f
            BlockVisualStyle.Sharp3D -> 0.19f
            BlockVisualStyle.Wood -> 0.21f
            BlockVisualStyle.LiquidGlass -> 0.26f
            BlockVisualStyle.Neon -> 0.32f
        }

        AppColorPalette.Sunset -> when (style) {
            BlockVisualStyle.Flat -> 0.15f
            BlockVisualStyle.Bubble -> 0.23f
            BlockVisualStyle.Outline -> 0.15f
            BlockVisualStyle.Sharp3D -> 0.18f
            BlockVisualStyle.Wood -> 0.2f
            BlockVisualStyle.LiquidGlass -> 0.25f
            BlockVisualStyle.Neon -> 0.31f
        }
    }
    return blendColors(average, vividTarget, amount)
}

internal fun themedBoardBaseSurfaceColor(
    appColorPalette: AppColorPalette,
    style: BlockVisualStyle,
    themePalette: BlockWiseThemePalette,
): Color {
    val paletteSurface = when (appColorPalette) {
        AppColorPalette.Classic -> themePalette.surfaceVariant
        AppColorPalette.Aurora -> blendColors(themePalette.surfaceVariant, themePalette.secondaryContainer, 0.08f)
        AppColorPalette.Sunset -> blendColors(themePalette.surfaceVariant, themePalette.primaryContainer, 0.08f)
    }
    val amount = when (appColorPalette) {
        AppColorPalette.Classic -> when (style) {
            BlockVisualStyle.Flat -> 0.24f
            BlockVisualStyle.Bubble -> 0.31f
            BlockVisualStyle.Outline -> 0.22f
            BlockVisualStyle.Sharp3D -> 0.27f
            BlockVisualStyle.Wood -> 0.29f
            BlockVisualStyle.LiquidGlass -> 0.3f
            BlockVisualStyle.Neon -> 0.32f
        }

        AppColorPalette.Aurora -> when (style) {
            BlockVisualStyle.Flat -> 0.2f
            BlockVisualStyle.Bubble -> 0.27f
            BlockVisualStyle.Outline -> 0.18f
            BlockVisualStyle.Sharp3D -> 0.23f
            BlockVisualStyle.Wood -> 0.25f
            BlockVisualStyle.LiquidGlass -> 0.26f
            BlockVisualStyle.Neon -> 0.28f
        }

        AppColorPalette.Sunset -> when (style) {
            BlockVisualStyle.Flat -> 0.22f
            BlockVisualStyle.Bubble -> 0.29f
            BlockVisualStyle.Outline -> 0.2f
            BlockVisualStyle.Sharp3D -> 0.25f
            BlockVisualStyle.Wood -> 0.27f
            BlockVisualStyle.LiquidGlass -> 0.28f
            BlockVisualStyle.Neon -> 0.3f
        }
    }
    return blendColors(themePalette.background, paletteSurface, amount)
}

internal fun themedBoardBaseBorderColor(
    appColorPalette: AppColorPalette,
    style: BlockVisualStyle,
    themePalette: BlockWiseThemePalette,
): Color {
    val paletteOutline = when (appColorPalette) {
        AppColorPalette.Classic -> themePalette.outlineVariant
        AppColorPalette.Aurora -> blendColors(themePalette.outlineVariant, themePalette.secondary, 0.08f)
        AppColorPalette.Sunset -> blendColors(themePalette.outlineVariant, themePalette.tertiary, 0.06f)
    }
    val amount = when (appColorPalette) {
        AppColorPalette.Classic -> when (style) {
            BlockVisualStyle.Flat -> 0.42f
            BlockVisualStyle.Bubble -> 0.49f
            BlockVisualStyle.Outline -> 0.4f
            BlockVisualStyle.Sharp3D -> 0.5f
            BlockVisualStyle.Wood -> 0.47f
            BlockVisualStyle.LiquidGlass -> 0.5f
            BlockVisualStyle.Neon -> 0.54f
        }

        AppColorPalette.Aurora -> when (style) {
            BlockVisualStyle.Flat -> 0.38f
            BlockVisualStyle.Bubble -> 0.45f
            BlockVisualStyle.Outline -> 0.36f
            BlockVisualStyle.Sharp3D -> 0.46f
            BlockVisualStyle.Wood -> 0.43f
            BlockVisualStyle.LiquidGlass -> 0.46f
            BlockVisualStyle.Neon -> 0.5f
        }

        AppColorPalette.Sunset -> when (style) {
            BlockVisualStyle.Flat -> 0.4f
            BlockVisualStyle.Bubble -> 0.47f
            BlockVisualStyle.Outline -> 0.38f
            BlockVisualStyle.Sharp3D -> 0.48f
            BlockVisualStyle.Wood -> 0.45f
            BlockVisualStyle.LiquidGlass -> 0.48f
            BlockVisualStyle.Neon -> 0.52f
        }
    }
    return blendColors(themePalette.background, paletteOutline, amount)
}

internal fun themedEmptyBoardCellColor(
    baseColor: Color,
    paletteAccent: Color,
    style: BlockVisualStyle,
): Color {
    val amount = when (style) {
        BlockVisualStyle.Flat -> 0.08f
        BlockVisualStyle.Bubble -> 0.14f
        BlockVisualStyle.Outline -> 0.06f
        BlockVisualStyle.Sharp3D -> 0.1f
        BlockVisualStyle.Wood -> 0.12f
        BlockVisualStyle.LiquidGlass -> 0.14f
        BlockVisualStyle.Neon -> 0.18f
    }
    return blendColors(baseColor, paletteAccent, amount)
}

internal fun themedEmptyBoardCellBorderColor(
    baseColor: Color,
    paletteAccent: Color,
    style: BlockVisualStyle,
): Color {
    val amount = when (style) {
        BlockVisualStyle.Flat -> 0.1f
        BlockVisualStyle.Bubble -> 0.16f
        BlockVisualStyle.Outline -> 0.08f
        BlockVisualStyle.Sharp3D -> 0.16f
        BlockVisualStyle.Wood -> 0.14f
        BlockVisualStyle.LiquidGlass -> 0.16f
        BlockVisualStyle.Neon -> 0.2f
    }
    return blendColors(baseColor, paletteAccent.darken(0.14f), amount)
}

private fun themedHighlightedBoardCellColor(
    baseColor: Color,
    highlightColor: Color,
    style: BlockVisualStyle,
): Color {
    val amount = when (style) {
        BlockVisualStyle.Flat -> 0.24f
        BlockVisualStyle.Bubble -> 0.33f
        BlockVisualStyle.Outline -> 0.2f
        BlockVisualStyle.Sharp3D -> 0.3f
        BlockVisualStyle.Wood -> 0.29f
        BlockVisualStyle.LiquidGlass -> 0.33f
        BlockVisualStyle.Neon -> 0.38f
    }
    val alpha = when (style) {
        BlockVisualStyle.Flat -> 0.88f
        BlockVisualStyle.Bubble -> 0.9f
        BlockVisualStyle.Outline -> 0.86f
        BlockVisualStyle.Sharp3D -> 0.9f
        BlockVisualStyle.Wood -> 0.88f
        BlockVisualStyle.LiquidGlass -> 0.82f
        BlockVisualStyle.Neon -> 0.84f
    }
    return blendColors(baseColor, highlightColor.copy(alpha = 1f), amount).copy(alpha = alpha)
}

private fun themedHighlightedBoardCellBorderColor(
    baseColor: Color,
    highlightColor: Color,
    style: BlockVisualStyle,
): Color {
    val amount = when (style) {
        BlockVisualStyle.Flat -> 0.32f
        BlockVisualStyle.Bubble -> 0.39f
        BlockVisualStyle.Outline -> 0.3f
        BlockVisualStyle.Sharp3D -> 0.4f
        BlockVisualStyle.Wood -> 0.36f
        BlockVisualStyle.LiquidGlass -> 0.4f
        BlockVisualStyle.Neon -> 0.45f
    }
    return blendColors(
        baseColor,
        highlightColor.copy(alpha = 1f).lighten(0.08f),
        amount,
    ).copy(alpha = 0.74f)
}

internal fun averageColor(colors: List<Color>): Color {
    val count = colors.size.coerceAtLeast(1)
    return Color(
        red = colors.sumOf { it.red.toDouble() }.toFloat() / count,
        green = colors.sumOf { it.green.toDouble() }.toFloat() / count,
        blue = colors.sumOf { it.blue.toDouble() }.toFloat() / count,
        alpha = colors.sumOf { it.alpha.toDouble() }.toFloat() / count,
    )
}

internal fun colorVividness(color: Color): Float {
    val max = maxOf(color.red, color.green, color.blue)
    val min = minOf(color.red, color.green, color.blue)
    return (max - min) + max * 0.18f
}

internal fun blendColors(start: Color, end: Color, fraction: Float): Color {
    val clampedFraction = fraction.coerceIn(0f, 1f)
    return Color(
        red = start.red + (end.red - start.red) * clampedFraction,
        green = start.green + (end.green - start.green) * clampedFraction,
        blue = start.blue + (end.blue - start.blue) * clampedFraction,
        alpha = start.alpha + (end.alpha - start.alpha) * clampedFraction,
    )
}

internal fun dragPreviewBorderColor(pieceColor: Color, invalidPlacement: Boolean): Color {
    return if (invalidPlacement) {
        pieceColor.lighten(0.34f)
    } else {
        pieceColor.darken(0.12f)
    }
}

internal fun dragPreviewBorderWidth(invalidPlacement: Boolean): Dp {
    return if (invalidPlacement) 2.6.dp else 1.6.dp
}


@Composable
private fun PiecesRow(
    pieces: List<Piece>,
    selectedIndex: Int?,
    draggingPieceId: Long?,
    onPieceSelected: (Int) -> Unit,
    onPieceMeasured: (pieceId: Long, info: PieceLayoutInfo) -> Unit,
    onPieceCoordinates: (pieceId: Long, coords: LayoutCoordinates) -> Unit,
    onDragStart: (index: Int, piece: Piece, startOffsetPx: Offset) -> Unit,
    onDrag: (deltaPx: Offset) -> Unit,
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit,
    cellSize: Dp,
    containerPadding: Dp,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(3) { index ->
            val piece = pieces.getOrNull(index)
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                if (piece != null) {
                    val isSelected = index == selectedIndex
                    val trayAlpha = if (piece.id == draggingPieceId) 0.22f else 1f

                    Box(
                        modifier = Modifier
                            .onGloballyPositioned { coords ->
                                onPieceCoordinates(piece.id, coords)
                                onPieceMeasured(
                                    piece.id,
                                    PieceLayoutInfo(
                                        sizePx = coords.size,
                                    )
                                )
                            }
                            .pointerInput(piece.id) {
                                awaitEachGesture {
                                    val down = awaitFirstDown(requireUnconsumed = false)
                                    val pointerId = down.id
                                    var dragStarted = false

                                    val dragChange = awaitTouchSlopOrCancellation(pointerId) { change, overSlop ->
                                        change.consume()
                                        if (!dragStarted) {
                                            dragStarted = true
                                            onDragStart(index, piece, down.position)
                                        }
                                        if (overSlop != Offset.Zero) {
                                            onDrag(overSlop)
                                        }
                                    }

                                    if (dragChange == null) {
                                        val up = waitForUpOrCancellation()
                                        if (up != null) {
                                            onPieceSelected(index)
                                        } else {
                                            onDragCancel()
                                        }
                                        return@awaitEachGesture
                                    }

                                    drag(pointerId) { change ->
                                        val delta = change.positionChange()
                                        if (delta != Offset.Zero) {
                                            change.consume()
                                            onDrag(delta)
                                        }
                                    }
                                    onDragEnd()
                                }
                            }
                            .padding(containerPadding)
                            .graphicsLayer(alpha = trayAlpha)
                    ) {
                        PiecePreview(
                            piece = piece,
                            cellSize = cellSize,
                            borderColor = if (isSelected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.outlineVariant
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun PiecePreview(
    piece: Piece,
    cellSize: Dp,
    pressed: Boolean = false,
    borderColor: Color = MaterialTheme.colorScheme.outlineVariant,
    borderWidth: Dp = 1.2.dp,
) {
    val gap = LocalBlockGapSpacing.current.gapDp
    val maxDx = piece.shape.cells.maxOf { it.dx }
    val maxDy = piece.shape.cells.maxOf { it.dy }
    val width = maxDx + 1
    val height = maxDy + 1

    val totalWidth = cellSize * width + gap * (width - 1)
    val totalHeight = cellSize * height + gap * (height - 1)

    Box(
        modifier = Modifier
            .width(totalWidth)
            .height(totalHeight)
    ) {
        piece.shape.cells.forEach { cell ->
            BlockTile3D(
                fillColor = piece.color.toPaletteColor(),
                borderColor = borderColor,
                borderWidth = borderWidth,
                cornerRadius = (cellSize * 0.22f).coerceAtLeast(6.dp),
                elevation = 5.dp,
                interaction = if (pressed) {
                    BlockTileInteraction.Pressed
                } else {
                    BlockTileInteraction.Normal
                },
                modifier = Modifier
                    .absoluteOffset(
                        x = (cellSize + gap) * cell.dx,
                        y = (cellSize + gap) * cell.dy,
                    )
                    .size(cellSize)
            ) {}
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BlockLogicContentPreview() {
    val size = GridSize(10)
    val grid = GameEngine.newGrid(size)
    val pieces = listOf(
        Piece(shape = Shapes.Square2, color = BlockColor.Red),
        Piece(shape = Shapes.Line3H, color = BlockColor.Blue),
        Piece(shape = Shapes.L3, color = BlockColor.Green),
    )
    BlockWiseTheme {
        BlockLogicContent(
            state = BlockLogicUiState(
                gridSize = size,
                difficulty = Difficulty.Normal,
                grid = grid,
                score = 42,
                movesRemaining = null,
                pieces = pieces,
                selectedPieceIndex = 1,
                validOrigins = emptySet(),
                validCells = emptySet(),
                clearingRows = emptySet(),
                clearingCols = emptySet(),
                isAnimatingClear = false,
                isGameOver = false,
            ),
            snackbarHostState = SnackbarHostState(),
            onNewGame = {},
            onMenu = {},
            onCellTapped = { _, _ -> },
            onPieceDropped = { _, _, _ -> },
            onPieceSelected = {},
            onPieceSelectionCleared = {},
            onDragPlacementFailed = {},
            onDragPlacementFeedbackCleared = {},
            onSuppressNextDragDropFailure = {},
        )
    }
}
