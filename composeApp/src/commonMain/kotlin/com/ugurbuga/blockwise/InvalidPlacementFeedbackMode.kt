package com.ugurbuga.blockwise

import androidx.compose.runtime.staticCompositionLocalOf

internal enum class InvalidPlacementFeedbackMode(
    val storageValue: String,
) {
    WhileDragging(storageValue = "while_dragging"),
    OnDrop(storageValue = "on_drop"),
    ;

    companion object {
        fun fromStorageValue(value: String?): InvalidPlacementFeedbackMode? {
            return entries.firstOrNull { it.storageValue == value }
        }
    }
}

internal val SelectableInvalidPlacementFeedbackModes = InvalidPlacementFeedbackMode.entries

internal object InvalidPlacementFeedbackModeStore {
    private const val INVALID_PLACEMENT_FEEDBACK_MODE_KEY = "invalid_placement_feedback_mode"

    fun loadSelectedInvalidPlacementFeedbackMode(): InvalidPlacementFeedbackMode? {
        return InvalidPlacementFeedbackMode.fromStorageValue(
            PlatformAppSettings.getString(INVALID_PLACEMENT_FEEDBACK_MODE_KEY)
        )
    }

    fun saveSelectedInvalidPlacementFeedbackMode(mode: InvalidPlacementFeedbackMode) {
        PlatformAppSettings.putString(INVALID_PLACEMENT_FEEDBACK_MODE_KEY, mode.storageValue)
    }
}

internal fun initializeInvalidPlacementFeedbackMode(): InvalidPlacementFeedbackMode {
    return InvalidPlacementFeedbackModeStore.loadSelectedInvalidPlacementFeedbackMode()
        ?: InvalidPlacementFeedbackMode.OnDrop.also(
            InvalidPlacementFeedbackModeStore::saveSelectedInvalidPlacementFeedbackMode
        )
}

internal val LocalInvalidPlacementFeedbackMode = staticCompositionLocalOf {
    InvalidPlacementFeedbackMode.OnDrop
}

