package com.ugurbuga.blockwise.blocklogic.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ugurbuga.blockwise.blocklogic.domain.CellCoord
import com.ugurbuga.blockwise.blocklogic.domain.Difficulty
import com.ugurbuga.blockwise.blocklogic.domain.GameEngine
import com.ugurbuga.blockwise.blocklogic.domain.GridSize
import com.ugurbuga.blockwise.blocklogic.domain.Piece
import com.ugurbuga.blockwise.blocklogic.domain.PlacementFailure
import com.ugurbuga.blockwise.blocklogic.domain.resolveGameConfig
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class BlockLogicViewModel(
    initialSize: GridSize = GridSize(10),
    initialDifficulty: Difficulty = Difficulty.Normal,
) : ViewModel() {

    private val _viewEvent = MutableSharedFlow<BlockLogicViewEvent>(extraBufferCapacity = 1)
    val viewEvent: SharedFlow<BlockLogicViewEvent> = _viewEvent.asSharedFlow()

    private val _uiState = MutableStateFlow(newState(initialSize, initialDifficulty))
    val uiState: StateFlow<BlockLogicUiState> = _uiState.asStateFlow()

    fun onNewGame(size: GridSize = _uiState.value.gridSize) {
        _uiState.value = newState(size, _uiState.value.difficulty)
    }

    fun onGridSizeSelected(size: GridSize) {
        onNewGame(size)
    }

    fun onDifficultySelected(difficulty: Difficulty) {
        _uiState.value = newState(_uiState.value.gridSize, difficulty)
    }

    fun onPieceSelected(index: Int) {
        val state = _uiState.value
        if (state.isAnimatingClear) return
        val config = resolveGameConfig(state.gridSize, state.difficulty)
        val piece = state.pieces.getOrNull(index)
        val validOrigins = if (piece != null) GameEngine.findValidOrigins(state.grid, piece, config.rules) else emptySet()
        val validCells = if (piece != null) toValidCells(validOrigins, piece) else emptySet()
        _uiState.value = state.copy(selectedPieceIndex = index, validOrigins = validOrigins, validCells = validCells)
    }

    fun onCellTapped(x: Int, y: Int) {
        val selectedIndex = _uiState.value.selectedPieceIndex
        placePieceAt(pieceIndex = selectedIndex, x = x, y = y, source = "tap")
    }

    fun onPieceDropped(pieceId: Long, x: Int, y: Int) {
        placePieceAt(pieceId = pieceId, x = x, y = y, source = "drag")
    }

    fun clearPieceSelection() {
        val state = _uiState.value
        if (state.selectedPieceIndex == null && state.validOrigins.isEmpty() && state.validCells.isEmpty()) return
        _uiState.value = state.copy(
            selectedPieceIndex = null,
            validOrigins = emptySet(),
            validCells = emptySet(),
        )
    }

    private fun placePieceAt(pieceIndex: Int? = null, pieceId: Long? = null, x: Int, y: Int, source: String) {
        val state = _uiState.value
        if (state.isGameOver || state.isAnimatingClear) return

        val config = resolveGameConfig(state.gridSize, state.difficulty)
        val rules = config.rules
        if (state.movesRemaining == 0) {
            _viewEvent.tryEmit(BlockLogicViewEvent.GameOver)
            _uiState.value = state.copy(isGameOver = true)
            return
        }
        val resolvedIndex = when {
            pieceId != null -> state.pieces.indexOfFirst { it.id == pieceId }.takeIf { it >= 0 }
            pieceIndex != null -> pieceIndex
            else -> null
        } ?: run {
            println("BW_DROP source=$source target=($x,$y) failure=NoPieceSelected")
            _viewEvent.tryEmit(BlockLogicViewEvent.PlacementFailed(PlacementFailure.NoPieceSelected))
            return
        }
        val piece = state.pieces.getOrNull(resolvedIndex) ?: return

        println(
            "BW_DROP source=$source target=($x,$y) pieceIndex=$resolvedIndex pieceId=${piece.id} cells=${piece.absoluteCellsAt(x, y)}"
        )

        val failure = GameEngine.validatePlacement(state.grid, piece, x, y, rules)
        if (failure != null) {
            println("BW_DROP source=$source target=($x,$y) validationFailure=$failure")
            _viewEvent.tryEmit(BlockLogicViewEvent.PlacementFailed(failure))
            return
        }

        val result = GameEngine.place(
            grid = state.grid,
            piece = piece,
            originX = x,
            originY = y,
            rules = rules,
        )

        val violation = result.ruleViolation
        if (violation != null) {
            println("BW_DROP source=$source target=($x,$y) ruleViolation=$violation")
            _viewEvent.tryEmit(BlockLogicViewEvent.PlacementFailed(PlacementFailure.Rule(violation)))
            return
        }

        println(
            "BW_DROP source=$source placedOrigin=($x,$y) pieceId=${piece.id} placedCells=${piece.absoluteCellsAt(x, y)} " +
                "clearedRows=${result.clearedRows} clearedCols=${result.clearedCols}"
        )

        val clearedScore = (result.clearedRows + result.clearedCols) * 10
        val newScore = state.score + 1 + clearedScore
        val remainingMoves = state.movesRemaining?.minus(1)?.coerceAtLeast(0)

        val remainingPieces = state.pieces.toMutableList().also { it.removeAt(resolvedIndex) }
        val replenishedPieces = remainingPieces.toMutableList().also { pieces ->
            while (pieces.size < 3) {
                pieces += GameEngine.randomPiece(availableShapes = config.availableShapes)
            }
        }

        val isGameOver = (remainingMoves == 0) || !GameEngine.hasAnyValidMove(result.grid, replenishedPieces, rules)
        val hasClearAnimation = result.clearedRowIndices.isNotEmpty() || result.clearedColIndices.isNotEmpty()

        if (!hasClearAnimation) {
            if (isGameOver) {
                _viewEvent.tryEmit(BlockLogicViewEvent.GameOver)
            }

            _uiState.value = state.copy(
                grid = result.grid,
                score = newScore,
                movesRemaining = remainingMoves,
                pieces = replenishedPieces,
                selectedPieceIndex = null,
                validOrigins = emptySet(),
                validCells = emptySet(),
                clearingRows = emptySet(),
                clearingCols = emptySet(),
                isAnimatingClear = false,
                isGameOver = isGameOver,
            )
            return
        }

        _uiState.value = state.copy(
            grid = result.placedGrid,
            score = newScore,
            movesRemaining = remainingMoves,
            pieces = emptyList(),
            selectedPieceIndex = null,
            validOrigins = emptySet(),
            validCells = emptySet(),
            clearingRows = result.clearedRowIndices,
            clearingCols = result.clearedColIndices,
            isAnimatingClear = true,
            isGameOver = false,
        )

        viewModelScope.launch {
            delay(CLEAR_ANIMATION_DURATION_MS)
            _uiState.value = _uiState.value.copy(
                grid = result.grid,
                pieces = replenishedPieces,
                clearingRows = emptySet(),
                clearingCols = emptySet(),
                isAnimatingClear = false,
                isGameOver = isGameOver,
            )
            if (isGameOver) {
                _viewEvent.tryEmit(BlockLogicViewEvent.GameOver)
            }
        }
    }

    private fun newState(size: GridSize, difficulty: Difficulty): BlockLogicUiState {
        val config = resolveGameConfig(size, difficulty)
        val opening = GameEngine.buildPlayableOpening(config)
        val grid = opening.grid
        val pieces = opening.pieces
        val rules = config.rules
        val isGameOver = !GameEngine.hasAnyValidMove(grid, pieces, rules)
        return BlockLogicUiState(
            gridSize = size,
            difficulty = difficulty,
            grid = grid,
            score = 0,
            movesRemaining = config.difficultyConfig.moveLimit,
            pieces = pieces,
            selectedPieceIndex = null,
            validOrigins = emptySet(),
            validCells = emptySet(),
            clearingRows = emptySet(),
            clearingCols = emptySet(),
            isAnimatingClear = false,
            isGameOver = isGameOver,
        )
    }

    private fun toValidCells(origins: Set<CellCoord>, piece: Piece): Set<CellCoord> {
        val result = HashSet<CellCoord>()
        origins.forEach { origin ->
            piece.shape.cells.forEach { offset ->
                result += CellCoord(x = origin.x + offset.dx, y = origin.y + offset.dy)
            }
        }
        return result
    }
}

private fun Piece.absoluteCellsAt(originX: Int, originY: Int): List<Pair<Int, Int>> {
    return shape.cells
        .map { (originX + it.dx) to (originY + it.dy) }
        .sortedWith(compareBy<Pair<Int, Int>> { it.second }.thenBy { it.first })
}

