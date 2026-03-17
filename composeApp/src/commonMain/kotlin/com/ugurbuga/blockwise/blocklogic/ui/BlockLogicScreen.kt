package com.ugurbuga.blockwise.blocklogic.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.zIndex
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ugurbuga.blockwise.blocklogic.domain.BlockColor
import com.ugurbuga.blockwise.blocklogic.domain.GameEngine
import com.ugurbuga.blockwise.blocklogic.domain.Difficulty
import com.ugurbuga.blockwise.blocklogic.domain.GridSize
import com.ugurbuga.blockwise.blocklogic.domain.PlacementFailure
import com.ugurbuga.blockwise.blocklogic.domain.RuleViolation
import com.ugurbuga.blockwise.blocklogic.domain.Piece
import com.ugurbuga.blockwise.blocklogic.domain.Shapes
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
import kotlin.math.roundToInt

private data class PieceLayoutInfo(
    val topLeftInRoot: Offset,
    val sizePx: IntSize,
)

private data class PieceSpan(
    val widthCells: Int,
    val heightCells: Int,
)

private data class DragPlacement(
    val originX: Int,
    val originY: Int,
)

private data class DragAnchor(
    val dx: Int,
    val dy: Int,
    val fingerToCellCenter: Offset,
)

internal data class GridAxisGeometry(
    val firstStart: Float,
    val step: Float,
)

private data class GridGeometry(
    val x: GridAxisGeometry,
    val y: GridAxisGeometry,
    val cellWidth: Float,
    val cellHeight: Float,
)

private fun Piece.spanInCells(): PieceSpan {
    val maxDx = shape.cells.maxOf { it.dx }
    val maxDy = shape.cells.maxOf { it.dy }
    return PieceSpan(
        widthCells = maxDx + 1,
        heightCells = maxDy + 1,
    )
}

private fun axisGeometry(axisStarts: Map<Int, Float>, fallbackStep: Float): GridAxisGeometry? {
    val ordered = axisStarts.entries.sortedBy { it.key }
    val firstStart = ordered.firstOrNull()?.value ?: return null
    val step = if (ordered.size >= 2) {
        ordered.zipWithNext { a, b -> b.value - a.value }
            .average()
            .toFloat()
    } else {
        fallbackStep
    }
    return GridAxisGeometry(firstStart = firstStart, step = step)
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

private fun resolveFingerCellAxis(
    targetCenter: Float,
    axis: GridAxisGeometry,
    cellSize: Float,
    gridCount: Int,
): Int? {
    if (gridCount <= 0) return null
    val firstCenter = axis.firstStart + cellSize / 2f
    val minCenter = firstCenter - axis.step
    val maxCenter = firstCenter + (gridCount - 1) * axis.step + axis.step
    if (targetCenter < minCenter || targetCenter > maxCenter) return null

    val resolved = ((targetCenter - firstCenter) / axis.step).roundToInt()
    return resolved.coerceIn(0, gridCount - 1)
}

internal fun resolveDraggedOriginAxis(
    fingerInRoot: Float,
    fingerToAnchorCellCenter: Float,
    cellCenters: List<Float>,
    anchorCellOffset: Int,
    pieceSpanCells: Int,
): Int? {
    if (cellCenters.isEmpty()) return null
    val sortedCenters = cellCenters.sorted()
    val targetCenter = fingerInRoot + fingerToAnchorCellCenter
    val tolerance = if (sortedCenters.size >= 2) {
        sortedCenters.zipWithNext { a, b -> b - a }
            .average()
            .toFloat()
    } else {
        0f
    }
    if (
        tolerance > 0f &&
            (targetCenter < sortedCenters.first() - tolerance || targetCenter > sortedCenters.last() + tolerance)
    ) {
        return null
    }
    val anchorCell = sortedCenters.indices.minByOrNull { index ->
        abs(sortedCenters[index] - targetCenter)
    } ?: return null
    val maxOrigin = (sortedCenters.size - pieceSpanCells).coerceAtLeast(0)
    return (anchorCell - anchorCellOffset).coerceIn(0, maxOrigin)
}

private fun measuredCellCenters(
    axisStarts: Map<Int, Float>,
    cellSize: Float,
    gridCount: Int,
    fallbackAxis: GridAxisGeometry,
): List<Float> {
    val measuredCenters = (0 until gridCount).mapNotNull { index ->
        axisStarts[index]?.plus(cellSize / 2f)
    }
    if (measuredCenters.size == gridCount) {
        return measuredCenters
    }
    return List(gridCount) { index ->
        fallbackAxis.firstStart + fallbackAxis.step * index + cellSize / 2f
    }
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

private const val ENABLE_DRAG_DEBUG_LOGS = false

private fun logDrag(message: String) {
    if (ENABLE_DRAG_DEBUG_LOGS) {
        println("BW_DRAG $message")
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
    onPieceDropped: (pieceIndex: Int, x: Int, y: Int) -> Unit,
    onPieceSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    var contentTopLeftInRoot by remember { mutableStateOf(Offset.Zero) }
    var gridSizePx by remember { mutableStateOf(IntSize.Zero) }
    var gridCellSizeMeasuredPx by remember { mutableStateOf<IntSize?>(null) }
    val gridColXs = remember { mutableStateMapOf<Int, Float>() }
    val gridRowYs = remember { mutableStateMapOf<Int, Float>() }
    val pieceLayoutInfos = remember { mutableStateMapOf<Int, PieceLayoutInfo>() }
    val pieceCoordinates = remember { mutableStateMapOf<Int, LayoutCoordinates>() }

    var draggingPieceIndex by remember { mutableStateOf<Int?>(null) }
    var draggingOffsetPx by remember { mutableStateOf(Offset.Zero) }
    var dragStartFingerInRoot by remember { mutableStateOf<Offset?>(null) }
    var dragStartOffsetInPiece by remember { mutableStateOf<Offset?>(null) }
    var dragAnchor by remember { mutableStateOf<DragAnchor?>(null) }
    var dragSnappedPlacement by remember { mutableStateOf<DragPlacement?>(null) }

    val gridCount = state.gridSize.value
    val gridGapDp = 2.dp
    val pieceContainerPaddingDp = 6.dp
    val pieceContainerBorderDp = 2.dp
    val pieceContainerInsetPx = with(density) { pieceContainerBorderDp.toPx() + pieceContainerPaddingDp.toPx() }
    val gridGapPx = with(density) { gridGapDp.toPx() }

    fun formatPlacement(piece: Piece, placement: DragPlacement?): String {
        return if (placement == null) {
            "snap=null"
        } else {
            "origin=(${placement.originX},${placement.originY}) cells=${piece.boardCellsAt(placement.originX, placement.originY)}"
        }
    }

    fun clearDragState() {
        draggingPieceIndex = null
        draggingOffsetPx = Offset.Zero
        dragStartFingerInRoot = null
        dragStartOffsetInPiece = null
        dragAnchor = null
        dragSnappedPlacement = null
    }

    fun measuredGridGeometry(): GridGeometry? {
        val cellSizePx = gridCellSizeMeasuredPx ?: return null
        val cellW = cellSizePx.width.toFloat()
        val cellH = cellSizePx.height.toFloat()
        val xAxis = axisGeometry(gridColXs, fallbackStep = cellW + gridGapPx) ?: return null
        val yAxis = axisGeometry(gridRowYs, fallbackStep = cellH + gridGapPx) ?: return null
        return GridGeometry(
            x = xAxis,
            y = yAxis,
            cellWidth = cellW,
            cellHeight = cellH,
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

    fun detectDragAnchor(piece: Piece, startOffsetPx: Offset): DragAnchor {
        val geometry = measuredGridGeometry()
        val fallbackCellSizePx = if (gridSizePx.width > 0 && gridCount > 0) {
            ((gridSizePx.width.toFloat() - gridGapPx * (gridCount - 1)) / gridCount).coerceAtLeast(0f)
        } else {
            with(density) { 16.dp.toPx() }
        }
        val cellW = geometry?.cellWidth ?: fallbackCellSizePx
        val cellH = geometry?.cellHeight ?: fallbackCellSizePx
        val stepX = geometry?.x?.step ?: (cellW + gridGapPx)
        val stepY = geometry?.y?.step ?: (cellH + gridGapPx)
        val localInContent = startOffsetPx - Offset(pieceContainerInsetPx, pieceContainerInsetPx)

        val nearest = piece.shape.cells.minByOrNull { cell ->
            val center = Offset(
                x = cell.dx * stepX + cellW / 2f,
                y = cell.dy * stepY + cellH / 2f,
            )
            val dx = center.x - localInContent.x
            val dy = center.y - localInContent.y
            dx * dx + dy * dy
        } ?: piece.shape.cells.first()

        val nearestCenter = Offset(
            x = nearest.dx * stepX + cellW / 2f,
            y = nearest.dy * stepY + cellH / 2f,
        )

        return DragAnchor(
            dx = nearest.dx,
            dy = nearest.dy,
            fingerToCellCenter = nearestCenter - localInContent,
        )
    }

    fun computeDragPlacement(piece: Piece, fingerInRoot: Offset): DragPlacement? {
        val geometry = measuredGridGeometry() ?: return null
        val span = piece.spanInCells()
        val anchor = dragAnchor ?: return null
        val xCenters = measuredCellCenters(
            axisStarts = gridColXs,
            cellSize = geometry.cellWidth,
            gridCount = gridCount,
            fallbackAxis = geometry.x,
        )
        val yCenters = measuredCellCenters(
            axisStarts = gridRowYs,
            cellSize = geometry.cellHeight,
            gridCount = gridCount,
            fallbackAxis = geometry.y,
        )
        val originX = resolveDraggedOriginAxis(
            fingerInRoot = fingerInRoot.x,
            fingerToAnchorCellCenter = anchor.fingerToCellCenter.x,
            cellCenters = xCenters,
            anchorCellOffset = anchor.dx,
            pieceSpanCells = span.widthCells,
        ) ?: return null
        val originY = resolveDraggedOriginAxis(
            fingerInRoot = fingerInRoot.y,
            fingerToAnchorCellCenter = anchor.fingerToCellCenter.y,
            cellCenters = yCenters,
            anchorCellOffset = anchor.dy,
            pieceSpanCells = span.heightCells,
        ) ?: return null

        return DragPlacement(
            originX = originX,
            originY = originY,
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
                onCellTapped = onCellTapped,
                onCellMeasured = { x, y, topLeftInRoot, sizePx ->
                    if (x == 0 && y == 0) {
                        gridCellSizeMeasuredPx = sizePx
                    }
                    if (y == 0) gridColXs[x] = topLeftInRoot.x
                    if (x == 0) gridRowYs[y] = topLeftInRoot.y
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .onGloballyPositioned { coords ->
                        gridSizePx = coords.size
                    },
            )

        Text(stringResource(Res.string.select_piece), style = MaterialTheme.typography.titleSmall)

            PiecesRow(
                pieces = state.pieces,
                selectedIndex = state.selectedPieceIndex,
                draggingIndex = draggingPieceIndex,
                onPieceSelected = onPieceSelected,
                onPieceMeasured = { index, info -> pieceLayoutInfos[index] = info },
                onPieceCoordinates = { index, coords -> pieceCoordinates[index] = coords },
                onDragStart = { index, startOffsetPx ->
                    if (!state.isGameOver) {
                        onPieceSelected(index)
                        draggingPieceIndex = index
                        draggingOffsetPx = Offset.Zero
                        val coords = pieceCoordinates[index]
                        val startFinger = coords?.localToRoot(startOffsetPx)
                        dragStartFingerInRoot = startFinger
                        dragStartOffsetInPiece = startOffsetPx

                        val piece = state.pieces.getOrNull(index)
                        dragAnchor = piece?.let { detectDragAnchor(it, startOffsetPx) }
                        dragSnappedPlacement = if (piece != null && startFinger != null) {
                            computeDragPlacement(piece, startFinger)
                        } else {
                            null
                        }
                        if (piece != null && startFinger != null) {
                            logDrag(
                                "start index=$index finger=${startFinger.debugString()} ${formatPlacement(piece, dragSnappedPlacement)}"
                            )
                        }
                    }
                },
                onDrag = { delta ->
                    val newOffset = draggingOffsetPx + delta
                    val index = draggingPieceIndex
                    val startFinger = dragStartFingerInRoot
                    val piece = if (index != null) state.pieces.getOrNull(index) else null
                    val nextPlacement = if (piece != null && startFinger != null) {
                        computeDragPlacement(piece, startFinger + newOffset)
                    } else {
                        null
                    }
                    if (
                        ENABLE_DRAG_DEBUG_LOGS &&
                            piece != null &&
                            startFinger != null &&
                            nextPlacement != dragSnappedPlacement
                    ) {
                        logDrag(
                            "move index=$index finger=${(startFinger + newOffset).debugString()} ${formatPlacement(piece, nextPlacement)}"
                        )
                    }
                    draggingOffsetPx = newOffset
                    dragSnappedPlacement = nextPlacement
                },
                onDragEnd = {
                    val index = draggingPieceIndex
                    val piece = if (index != null) state.pieces.getOrNull(index) else null
                    val finger = dragStartFingerInRoot?.plus(draggingOffsetPx)
                    dragSnappedPlacement?.let { placement ->
                        if (piece != null) {
                            logDrag(
                                "drop index=$index finger=${finger?.debugString() ?: "null"} ${formatPlacement(piece, placement)}"
                            )
                        }
                        if (index != null) {
                            onPieceDropped(index, placement.originX, placement.originY)
                        }
                    } ?: run {
                        logDrag(
                            "drop index=$index finger=${finger?.debugString() ?: "null"} snap=null"
                        )
                    }
                    clearDragState()
                },
                onDragCancel = {
                    val index = draggingPieceIndex
                    val piece = if (index != null) state.pieces.getOrNull(index) else null
                    val finger = dragStartFingerInRoot?.plus(draggingOffsetPx)
                    if (piece != null) {
                        logDrag(
                            "cancel index=$index finger=${finger?.debugString() ?: "null"} ${formatPlacement(piece, dragSnappedPlacement)}"
                        )
                    }
                    clearDragState()
                },
                cellSize = gridCellSizeDp,
                containerPadding = pieceContainerPaddingDp,
            )
        }

        val dragIndex = draggingPieceIndex
        if (dragIndex != null) {
            val piece = state.pieces.getOrNull(dragIndex)
            val info = pieceLayoutInfos[dragIndex]
            val startFingerInRoot = dragStartFingerInRoot
            if (piece != null && info != null && startFingerInRoot != null) {
                val fingerInRoot = startFingerInRoot + draggingOffsetPx
                val placement = dragSnappedPlacement
                val overlayTopLeftInRoot = placement?.let {
                    val originColX = gridColXs[it.originX]
                    val originRowY = gridRowYs[it.originY]
                    if (originColX != null && originRowY != null) {
                        Offset(
                            x = originColX - pieceContainerInsetPx,
                            y = originRowY - pieceContainerInsetPx,
                        )
                    } else {
                        freeOverlayTopLeftInRoot(fingerInRoot, info)
                    }
                } ?: freeOverlayTopLeftInRoot(fingerInRoot, info)
                val overlayTopLeftLocal = overlayTopLeftInRoot - contentTopLeftInRoot
                val dragCellLabels = placement?.let { piece.boardCoordLabelsAt(it.originX, it.originY) }.orEmpty()

                Box(
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                x = overlayTopLeftLocal.x.roundToInt(),
                                y = overlayTopLeftLocal.y.roundToInt(),
                            )
                        }
                        .graphicsLayer {
                            alpha = 1f
                            shadowElevation = 12f
                        }
                        .zIndex(10f)
                        .border(pieceContainerBorderDp, MaterialTheme.colorScheme.primary)
                        .padding(pieceContainerPaddingDp)
                ) {
                    PiecePreview(piece, cellSize = gridCellSizeDp, cellLabels = dragCellLabels)
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
    onCellTapped: (x: Int, y: Int) -> Unit,
    onCellMeasured: (x: Int, y: Int, topLeftInRoot: Offset, sizePx: IntSize) -> Unit,
    modifier: Modifier = Modifier,
) {
    val size = grid.size.value
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        for (y in 0 until size) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                for (x in 0 until size) {
                    val cell = grid[x, y]
                    val isValidCell = validCells.any { it.x == x && it.y == y }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .onGloballyPositioned { coords ->
                                onCellMeasured(x, y, coords.positionInRoot(), coords.size)
                            }
                            .border(1.dp, MaterialTheme.colorScheme.outline)
                            .background(
                                when {
                                    cell != null -> cell.color.toComposeColor()
                                    isValidCell -> MaterialTheme.colorScheme.primary.copy(alpha = 0.20f)
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
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f),
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
    draggingIndex: Int?,
    onPieceSelected: (Int) -> Unit,
    onPieceMeasured: (index: Int, info: PieceLayoutInfo) -> Unit,
    onPieceCoordinates: (index: Int, coords: LayoutCoordinates) -> Unit,
    onDragStart: (index: Int, startOffsetPx: Offset) -> Unit,
    onDrag: (deltaPx: Offset) -> Unit,
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit,
    cellSize: androidx.compose.ui.unit.Dp,
    containerPadding: androidx.compose.ui.unit.Dp,
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        itemsIndexed(pieces) { index, piece ->
            val borderColor = if (index == selectedIndex) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline

            Box(
                modifier = Modifier
                    .onGloballyPositioned { coords ->
                        onPieceCoordinates(index, coords)
                        onPieceMeasured(
                            index,
                            PieceLayoutInfo(
                                topLeftInRoot = coords.positionInRoot(),
                                sizePx = coords.size,
                            )
                        )
                    }
                    .pointerInput(index) {
                        detectDragGestures(
                            onDragStart = { startOffset -> onDragStart(index, startOffset) },
                            onDragEnd = { onDragEnd() },
                            onDragCancel = { onDragCancel() },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                onDrag(Offset(dragAmount.x, dragAmount.y))
                            },
                        )
                    }
                    .clickable { onPieceSelected(index) }
                    .border(2.dp, borderColor)
                    .padding(containerPadding)
                    .graphicsLayer(alpha = if (index == draggingIndex) 0f else 1f)
            ) {
                PiecePreview(piece, cellSize = cellSize)
            }
        }
    }
}

@Composable
private fun PiecePreview(
    piece: Piece,
    cellSize: androidx.compose.ui.unit.Dp,
    cellLabels: Map<Pair<Int, Int>, String> = emptyMap(),
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
                    .border(1.dp, MaterialTheme.colorScheme.outline)
                    .background(piece.color.toComposeColor()),
                contentAlignment = Alignment.Center,
            ) {
                if (label != null) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
    }
}

private fun BlockColor.toComposeColor(): Color {
    return when (this) {
        BlockColor.Red -> Color(0xFFE57373)
        BlockColor.Green -> Color(0xFF81C784)
        BlockColor.Blue -> Color(0xFF64B5F6)
        BlockColor.Yellow -> Color(0xFFFFF176)
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
