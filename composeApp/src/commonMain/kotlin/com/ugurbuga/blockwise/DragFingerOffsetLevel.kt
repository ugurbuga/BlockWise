package com.ugurbuga.blockwise

import androidx.compose.runtime.staticCompositionLocalOf

internal enum class DragFingerOffsetLevel(
    val storageValue: String,
    val offsetPx: Float,
) {
    None(storageValue = "none", offsetPx = 0f),
    Low(storageValue = "low", offsetPx = 150f),
    Medium(storageValue = "medium", offsetPx = 300f),
    High(storageValue = "high", offsetPx = 450f),
    ;

    companion object {
        fun fromStorageValue(value: String?): DragFingerOffsetLevel? {
            return entries.firstOrNull { it.storageValue == value }
        }
    }
}

internal val SelectableDragFingerOffsetLevels = DragFingerOffsetLevel.entries

internal object DragFingerOffsetLevelStore {
    private const val DRAG_FINGER_OFFSET_LEVEL_KEY = "drag_finger_offset_level"

    fun loadSelectedDragFingerOffsetLevel(): DragFingerOffsetLevel? {
        return DragFingerOffsetLevel.fromStorageValue(
            PlatformAppSettings.getString(DRAG_FINGER_OFFSET_LEVEL_KEY)
        )
    }

    fun saveSelectedDragFingerOffsetLevel(level: DragFingerOffsetLevel) {
        PlatformAppSettings.putString(DRAG_FINGER_OFFSET_LEVEL_KEY, level.storageValue)
    }
}

internal fun initializeDragFingerOffsetLevel(): DragFingerOffsetLevel {
    return DragFingerOffsetLevelStore.loadSelectedDragFingerOffsetLevel()
        ?: DragFingerOffsetLevel.Medium.also(DragFingerOffsetLevelStore::saveSelectedDragFingerOffsetLevel)
}

internal val LocalDragFingerOffsetLevel = staticCompositionLocalOf { DragFingerOffsetLevel.Medium }

