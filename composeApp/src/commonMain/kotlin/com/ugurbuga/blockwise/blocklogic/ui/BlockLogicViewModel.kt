package com.ugurbuga.blockwise.blocklogic.ui

import androidx.lifecycle.ViewModel
import com.ugurbuga.blockwise.blocklogic.domain.CellCoord
import com.ugurbuga.blockwise.blocklogic.domain.Difficulty
import com.ugurbuga.blockwise.blocklogic.domain.GameEngine
import com.ugurbuga.blockwise.blocklogic.domain.GridSize
import com.ugurbuga.blockwise.blocklogic.domain.Piece
import com.ugurbuga.blockwise.blocklogic.domain.PlacementFailure
import com.ugurbuga.blockwise.blocklogic.domain.RuleViolation
import com.ugurbuga.blockwise.blocklogic.domain.toRules
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

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
        val rules = state.difficulty.toRules()
        val piece = state.pieces.getOrNull(index)
        val validOrigins = if (piece != null) GameEngine.findValidOrigins(state.grid, piece, rules) else emptySet()
        val validCells = if (piece != null) toValidCells(validOrigins, piece) else emptySet()
        _uiState.value = state.copy(selectedPieceIndex = index, validOrigins = validOrigins, validCells = validCells)
    }

    fun onCellTapped(x: Int, y: Int) {
        val selectedIndex = _uiState.value.selectedPieceIndex
        placePieceAt(pieceIndex = selectedIndex, x = x, y = y, source = "tap")
    }

    fun onPieceDropped(pieceIndex: Int, x: Int, y: Int) {
        placePieceAt(pieceIndex = pieceIndex, x = x, y = y, source = "drag")
    }

    private fun placePieceAt(pieceIndex: Int?, x: Int, y: Int, source: String) {
        val state = _uiState.value
        if (state.isGameOver) return

        val rules = state.difficulty.toRules()
        val resolvedIndex = pieceIndex ?: run {
            println("BW_DROP source=$source target=($x,$y) failure=NoPieceSelected")
            _viewEvent.tryEmit(BlockLogicViewEvent.PlacementFailed(PlacementFailure.NoPieceSelected))
            return
        }
        val piece = state.pieces.getOrNull(resolvedIndex) ?: return

        println(
            "BW_DROP source=$source target=($x,$y) pieceIndex=$resolvedIndex cells=${piece.absoluteCellsAt(x, y)}"
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
            "BW_DROP source=$source placedOrigin=($x,$y) placedCells=${piece.absoluteCellsAt(x, y)} " +
                "clearedRows=${result.clearedRows} clearedCols=${result.clearedCols}"
        )

        val clearedScore = (result.clearedRows + result.clearedCols) * 10
        val newScore = state.score + 1 + clearedScore

        val newPieces = state.pieces.toMutableList().also { it.removeAt(resolvedIndex) }
        if (newPieces.size < 3) {
            while (newPieces.size < 3) newPieces += GameEngine.randomPiece()
        }

        val isGameOver = !GameEngine.hasAnyValidMove(result.grid, newPieces, rules)
        if (isGameOver) {
            _viewEvent.tryEmit(BlockLogicViewEvent.GameOver)
        }

        _uiState.value = state.copy(
            grid = result.grid,
            score = newScore,
            pieces = newPieces,
            selectedPieceIndex = null,
            validOrigins = emptySet(),
            validCells = emptySet(),
            isGameOver = isGameOver,
        )
    }

    private fun newState(size: GridSize, difficulty: Difficulty): BlockLogicUiState {
        val grid = GameEngine.newGrid(size)
        val pieces = List(3) { GameEngine.randomPiece() }
        val rules = difficulty.toRules()
        val isGameOver = !GameEngine.hasAnyValidMove(grid, pieces, rules)
        return BlockLogicUiState(
            gridSize = size,
            difficulty = difficulty,
            grid = grid,
            score = 0,
            pieces = pieces,
            selectedPieceIndex = null,
            validOrigins = emptySet(),
            validCells = emptySet(),
            isGameOver = isGameOver,
        )
    }

    private fun toValidCells(origins: Set<CellCoord>, piece: com.ugurbuga.blockwise.blocklogic.domain.Piece): Set<CellCoord> {
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

