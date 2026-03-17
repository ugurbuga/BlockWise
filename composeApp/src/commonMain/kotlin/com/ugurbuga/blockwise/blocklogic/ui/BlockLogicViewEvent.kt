package com.ugurbuga.blockwise.blocklogic.ui

import com.ugurbuga.blockwise.blocklogic.domain.PlacementFailure

sealed interface BlockLogicViewEvent {
    data class PlacementFailed(val failure: PlacementFailure) : BlockLogicViewEvent
    data object GameOver : BlockLogicViewEvent
}
