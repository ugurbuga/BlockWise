package com.ugurbuga.blockwise.blocklogic.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitTouchSlopOrCancellation
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.zIndex
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ugurbuga.blockwise.blocklogic.domain.BlockColor
import com.ugurbuga.blockwise.blocklogic.domain.CellOffset
import com.ugurbuga.blockwise.blocklogic.domain.GameEngine
import com.ugurbuga.blockwise.blocklogic.domain.Difficulty
import com.ugurbuga.blockwise.blocklogic.domain.GridSize
import com.ugurbuga.blockwise.blocklogic.domain.PlacementFailure
import com.ugurbuga.blockwise.blocklogic.domain.RuleViolation
import com.ugurbuga.blockwise.blocklogic.domain.Piece
import com.ugurbuga.blockwise.blocklogic.domain.Shapes
import com.ugurbuga.blockwise.ui.theme.BlockWiseTheme
import com.ugurbuga.blockwise.ui.theme.toPaletteColor
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import blockwise.composeapp.generated.resources.Res
import blockwise.composeapp.generated.resources.difficulty
import blockwise.composeapp.generated.resources.difficulty_easy
import blockwise.composeapp.generated.resources.difficulty_hard
import blockwise.composeapp.generated.resources.difficulty_normal
import blockwise.composeapp.generated.resources.game_over
import blockwise.composeapp.generated.resources.game_over_no_moves
import blockwise.composeapp.generated.resources.grid_size
import blockwise.composeapp.generated.resources.grid_size_option
import blockwise.composeapp.generated.resources.invalid_placement
import blockwise.composeapp.generated.resources.invalid_placement_out_of_bounds
import blockwise.composeapp.generated.resources.invalid_placement_overlap
import blockwise.composeapp.generated.resources.menu
import blockwise.composeapp.generated.resources.no_moves_left
import blockwise.composeapp.generated.resources.new_game
import blockwise.composeapp.generated.resources.rule_color_limit
import blockwise.composeapp.generated.resources.rule_color_limit_col
import blockwise.composeapp.generated.resources.score
import blockwise.composeapp.generated.resources.select_piece

import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.hypot
import kotlin.math.roundToInt

private data class PieceLayoutInfo(
    val sizePx: IntSize,
)

private data class PieceSpan(
    val widthCells: Int,
    val heightCells: Int,
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

private fun Piece.boardCellsAt(originX: Int, originY: Int): List<Pair<Int, Int>> {
    return shape.cells
        .map { (originX + it.dx) to (originY + it.dy) }
        .sortedWith(compareBy<Pair<Int, Int>> { it.second }.thenBy { it.first })
}

private fun Piece.boardCoordLabelsAt(originX: Int, originY: Int): Map<Pair<Int, Int>, String> {
    return shape.cells.associate { cell ->
        (cell.dx to cell.dy) to "${originX + cell.dx},${originY + cell.dy}"
    }
}

private fun Offset.debugString(): String = "(${x.roundToInt()},${y.roundToInt()})"

private const val ENABLE_DRAG_DEBUG_LOGS = true

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
) {
    val vm: BlockLogicViewModel = viewModel(key = sessionKey) {
        BlockLogicViewModel(
            initialSize = initialSize,
            initialDifficulty = initialDifficulty,
        )
    }
    val state by vm.uiState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(vm) {
        vm.viewEvent.collect { event ->
            when (event) {
                is BlockLogicViewEvent.PlacementFailed -> {
                    val msg = when (val failure = event.failure) {
                        PlacementFailure.NoPieceSelected -> getString(Res.string.invalid_placement)
                        PlacementFailure.OutOfBounds -> getString(Res.string.invalid_placement_out_of_bounds)
                        PlacementFailure.Overlap -> getString(Res.string.invalid_placement_overlap)
                        is PlacementFailure.Rule -> {
                            when (val v = failure.violation) {
                                is RuleViolation.TooManySameColorInRow -> getString(Res.string.rule_color_limit, v.limit)
                                is RuleViolation.TooManySameColorInCol -> getString(Res.string.rule_color_limit_col, v.limit)
                            }
                        }
                    }
                    snackbarHostState.showSnackbar(msg)
                }
                BlockLogicViewEvent.GameOver -> {
                    snackbarHostState.showSnackbar(
                        getString(
                            Res.string.game_over_no_moves,
                            getString(Res.string.game_over),
                            getString(Res.string.no_moves_left),
                        )
                    )
                }
            }
        }
    }

    BlockLogicContent(
        modifier = modifier,
        state = state,
        snackbarHostState = snackbarHostState,
        onNewGame = vm::onNewGame,
        onMenu = onMenu,
        onCellTapped = vm::onCellTapped,
        onPieceDropped = vm::onPieceDropped,
        onPieceSelected = vm::onPieceSelected,
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
    modifier: Modifier = Modifier,
) {
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
    var dragLogSessionId by remember { mutableStateOf<Int?>(null) }
    var dragLogSequence by remember { mutableStateOf(0) }

    val gridCount = state.gridSize.value
    val gridGapDp = 2.dp
    val pieceContainerPaddingDp = 0.dp
    val pieceContainerInsetPx = with(density) { pieceContainerPaddingDp.toPx() }
    val gridGapPx = with(density) { gridGapDp.toPx() }

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
        dragLogSessionId = null
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

    fun computeDragResolution(piece: Piece, fingerInRoot: Offset): DragResolution? {
        val geometry = measuredGridGeometry() ?: return null
        val span = piece.spanInCells()
        val anchor = dragAnchor ?: return null
        val hoveredX = resolvePointerCellAxis(
            pointerPosition = fingerInRoot.x,
            axis = geometry.x,
            cellExtent = geometry.cellWidth,
            maxCellIndex = gridCount - 1,
        ) ?: return null
        val hoveredY = resolvePointerCellAxis(
            pointerPosition = fingerInRoot.y,
            axis = geometry.y,
            cellExtent = geometry.cellHeight,
            maxCellIndex = gridCount - 1,
        ) ?: return null
        val originX = (hoveredX - anchor.dx).coerceIn(0, gridCount - span.widthCells)
        val originY = (hoveredY - anchor.dy).coerceIn(0, gridCount - span.heightCells)
        val isInsideBoard = isFingerInsideBoard(fingerInRoot, geometry)

        return DragResolution(
            placement = if (isInsideBoard) {
                DragPlacement(
                    originX = originX,
                    originY = originY,
                )
            } else {
                null
            },
            hoveredCell = HoveredBoardCell(x = hoveredX, y = hoveredY),
            isFingerInsideBoard = isInsideBoard,
        )
    }

    val gridCellSizeDp = remember(gridSizePx, gridCount) {
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
            Text(
                text = stringResource(Res.string.score, state.score),
                style = MaterialTheme.typography.titleMedium,
            )

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

        SnackbarHost(hostState = snackbarHostState)

        if (state.isGameOver) {
            AssistChip(
                onClick = {},
                enabled = false,
                label = { Text(stringResource(Res.string.game_over)) },
                colors = AssistChipDefaults.assistChipColors(
                    disabledContainerColor = MaterialTheme.colorScheme.errorContainer,
                    disabledLabelColor = MaterialTheme.colorScheme.onErrorContainer,
                ),
            )
        }

            GridView(
                grid = state.grid,
                validCells = state.validCells,
                clearingRows = state.clearingRows,
                clearingCols = state.clearingCols,
                onCellTapped = onCellTapped,
                onGridMeasured = { topLeftInRoot, sizePx ->
                    gridTopLeftInRoot = topLeftInRoot
                    gridSizePx = sizePx
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
            )

        Text(stringResource(Res.string.select_piece), style = MaterialTheme.typography.titleSmall)

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
                        draggingOffsetPx = Offset.Zero
                        val startFinger = coords.localToRoot(startOffsetPx)
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
                        val resolution = if (geometry != null) computeDragResolution(piece, startFinger) else null
                        dragSnappedPlacement = resolution?.placement
                        logDrag(
                            buildDragLogMessage(
                                phase = "start",
                                sessionId = dragLogSessionId,
                                pieceIndex = index,
                                piece = piece,
                                fingerInRoot = startFinger,
                                dragOffsetPx = draggingOffsetPx,
                                startOffsetInPiece = dragStartOffsetInPiece,
                                startOffsetInContent = dragStartOffsetInContent,
                                anchor = dragAnchor,
                                geometry = geometry,
                                resolution = resolution,
                                freeOverlayTopLeftInRoot = freeOverlayTopLeftInRoot(startFinger, info),
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
                    val geometry = measuredGridGeometry()
                    val resolution = if (piece != null && currentFinger != null) {
                        computeDragResolution(piece, currentFinger)
                    } else {
                        null
                    }
                    draggingOffsetPx = newOffset
                    dragSnappedPlacement = resolution?.placement
                    logDrag(
                        buildDragLogMessage(
                            phase = "move",
                            sessionId = dragLogSessionId,
                            pieceIndex = state.pieces.indexOfFirst { it.id == pieceId }.takeIf { it >= 0 },
                            piece = piece,
                            fingerInRoot = currentFinger,
                            dragOffsetPx = newOffset,
                            startOffsetInPiece = dragStartOffsetInPiece,
                            startOffsetInContent = dragStartOffsetInContent,
                            anchor = dragAnchor,
                            geometry = geometry,
                            resolution = resolution,
                            freeOverlayTopLeftInRoot = if (currentFinger != null && pieceId != null) {
                                pieceLayoutInfos[pieceId]?.let { info -> freeOverlayTopLeftInRoot(currentFinger, info) }
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
                    val finger = dragStartFingerInRoot?.plus(draggingOffsetPx)
                    val geometry = measuredGridGeometry()
                    val resolution = if (piece != null && finger != null) computeDragResolution(piece, finger) else null
                    dragSnappedPlacement?.let { placement ->
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
                                snappedOverlayTopLeftInRoot = snappedOverlayTopLeftInRoot(placement),
                                overlayMode = "drop-snapped",
                                extra = piece?.let { formatPlacement(it, placement) }
                            )
                        )
                        if (pieceId != null) {
                            onPieceDropped(pieceId, placement.originX, placement.originY)
                        }
                    } ?: run {
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
                    }
                    clearDragState()
                },
                onDragCancel = {
                    if (draggingPieceId == null || draggingPieceSnapshot == null || dragStartFingerInRoot == null) {
                        return@PiecesRow
                    }
                    val pieceId = draggingPieceId
                    val piece = draggingPieceSnapshot
                    val finger = dragStartFingerInRoot?.plus(draggingOffsetPx)
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
                val fingerInRoot = startFingerInRoot + draggingOffsetPx
                val freeOverlayTopLeftInRoot = freeOverlayTopLeftInRoot(fingerInRoot, info)
                val freeOverlayTopLeftLocal = freeOverlayTopLeftInRoot - contentTopLeftInRoot
                val placement = dragSnappedPlacement
                val resolution = computeDragResolution(piece, fingerInRoot)
                val snappedOverlayTopLeftLocal = placement
                    ?.let(::snappedOverlayTopLeftInRoot)
                    ?.minus(contentTopLeftInRoot)
                val displayTopLeftLocal = if (resolution?.isFingerInsideBoard == true) {
                    snappedOverlayTopLeftLocal ?: freeOverlayTopLeftLocal
                } else {
                    freeOverlayTopLeftLocal
                }
                val dragCellLabels = placement?.let { piece.boardCoordLabelsAt(it.originX, it.originY) }.orEmpty()

                Box(
                    modifier = Modifier
                        .graphicsLayer {
                            translationX = displayTopLeftLocal.x
                            translationY = displayTopLeftLocal.y
                            alpha = if (snappedOverlayTopLeftLocal != null) 0.98f else 0.92f
                        }
                        .zIndex(10f)
                        .padding(pieceContainerPaddingDp)
                ) {
                    PiecePreview(
                        piece = piece,
                        cellSize = gridCellSizeDp,
                        cellLabels = dragCellLabels,
                        borderColor = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}

@Composable
private fun GridSizeMenu(
    selected: GridSize,
    onSelected: (GridSize) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Button(onClick = { expanded = true }) {
            Text(stringResource(Res.string.grid_size))
            Spacer(Modifier.width(8.dp))
            Text(stringResource(Res.string.grid_size_option, selected.value))
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            listOf(8, 10, 12).forEach { size ->
                DropdownMenuItem(
                    text = { Text(stringResource(Res.string.grid_size_option, size)) },
                    onClick = {
                        expanded = false
                        onSelected(GridSize(size))
                    },
                )
            }
        }
    }
}

@Composable
private fun GridView(
    grid: com.ugurbuga.blockwise.blocklogic.domain.Grid,
    validCells: Set<com.ugurbuga.blockwise.blocklogic.domain.CellCoord>,
    clearingRows: Set<Int>,
    clearingCols: Set<Int>,
    onCellTapped: (x: Int, y: Int) -> Unit,
    onGridMeasured: (topLeftInRoot: Offset, sizePx: IntSize) -> Unit,
    modifier: Modifier = Modifier,
) {
    val size = grid.size.value
    Column(
        modifier = modifier.onGloballyPositioned { coords ->
            onGridMeasured(coords.positionInRoot(), coords.size)
        },
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        for (y in 0 until size) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                for (x in 0 until size) {
                    val cell = grid[x, y]
                    val isValidCell = validCells.any { it.x == x && it.y == y }
                    val isClearingCell = y in clearingRows || x in clearingCols
                    val clearProgress by animateFloatAsState(
                        targetValue = if (isClearingCell) 0f else 1f,
                        animationSpec = tween(durationMillis = 220),
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .graphicsLayer {
                                alpha = clearProgress
                                scaleX = 0.85f + 0.15f * clearProgress
                                scaleY = 0.85f + 0.15f * clearProgress
                            }
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant)
                            .background(
                                when {
                                    cell != null -> cell.color.toPaletteColor()
                                    isValidCell -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.9f)
                                    else -> MaterialTheme.colorScheme.surface
                                }
                            )
                            .clickable { onCellTapped(x, y) },
                        contentAlignment = Alignment.Center,
                    )
                    {
                        Text(
                            text = "$x,$y",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DifficultyMenu(
    selected: Difficulty,
    onSelected: (Difficulty) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Button(onClick = { expanded = true }) {
            Text(stringResource(Res.string.difficulty))
            Spacer(Modifier.width(8.dp))
            Text(
                when (selected) {
                    Difficulty.Easy -> stringResource(Res.string.difficulty_easy)
                    Difficulty.Normal -> stringResource(Res.string.difficulty_normal)
                    Difficulty.Hard -> stringResource(Res.string.difficulty_hard)
                }
            )
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text(stringResource(Res.string.difficulty_easy)) },
                onClick = {
                    expanded = false
                    onSelected(Difficulty.Easy)
                },
            )
            DropdownMenuItem(
                text = { Text(stringResource(Res.string.difficulty_normal)) },
                onClick = {
                    expanded = false
                    onSelected(Difficulty.Normal)
                },
            )
            DropdownMenuItem(
                text = { Text(stringResource(Res.string.difficulty_hard)) },
                onClick = {
                    expanded = false
                    onSelected(Difficulty.Hard)
                },
            )
        }
    }
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
    cellSize: androidx.compose.ui.unit.Dp,
    containerPadding: androidx.compose.ui.unit.Dp,
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        itemsIndexed(
            items = pieces,
            key = { _, piece -> piece.id },
        ) { index, piece ->
            val isSelected = index == selectedIndex

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
                    .graphicsLayer(alpha = if (piece.id == draggingPieceId) 0.22f else 1f)
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

@Composable
private fun PiecePreview(
    piece: Piece,
    cellSize: Dp,
    cellLabels: Map<Pair<Int, Int>, String> = emptyMap(),
    borderColor: Color = MaterialTheme.colorScheme.outlineVariant,
) {
    val maxDx = piece.shape.cells.maxOf { it.dx }
    val maxDy = piece.shape.cells.maxOf { it.dy }
    val width = maxDx + 1
    val height = maxDy + 1

    val gap = 2.dp
    val totalWidth = cellSize * width + gap * (width - 1)
    val totalHeight = cellSize * height + gap * (height - 1)

    Box(
        modifier = Modifier
            .width(totalWidth)
            .height(totalHeight)
    ) {
        piece.shape.cells.forEach { cell ->
            val label = cellLabels[cell.dx to cell.dy]
            Box(
                modifier = Modifier
                    .offset(
                        x = (cellSize + gap) * cell.dx,
                        y = (cellSize + gap) * cell.dy,
                    )
                    .size(cellSize)
                    .border(1.dp, borderColor)
                    .background(piece.color.toPaletteColor()),
                contentAlignment = Alignment.Center,
            ) {
                if (label != null) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                }
            }
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
        Piece(shape = Shapes.Line3V, color = BlockColor.Blue),
        Piece(shape = Shapes.L3, color = BlockColor.Green),
    )
    BlockWiseTheme {
        BlockLogicContent(
            state = BlockLogicUiState(
                gridSize = size,
                difficulty = Difficulty.Normal,
                grid = grid,
                score = 42,
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
        )
    }
}
