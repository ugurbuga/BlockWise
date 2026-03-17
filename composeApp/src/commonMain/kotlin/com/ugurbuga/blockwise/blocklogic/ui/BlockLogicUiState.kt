package com.ugurbuga.blockwise.blocklogic.ui

import com.ugurbuga.blockwise.blocklogic.domain.Grid
import com.ugurbuga.blockwise.blocklogic.domain.GridSize
import com.ugurbuga.blockwise.blocklogic.domain.Piece
import com.ugurbuga.blockwise.blocklogic.domain.CellCoord
import com.ugurbuga.blockwise.blocklogic.domain.Difficulty

data class BlockLogicUiState(
    val gridSize: GridSize,
    val difficulty: Difficulty,
    val grid: Grid,
    val score: Int,
    val pieces: List<Piece>,
    val selectedPieceIndex: Int?,
    val validOrigins: Set<CellCoord>,
    val validCells: Set<CellCoord>,
    val isGameOver: Boolean,
)
