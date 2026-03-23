package com.ugurbuga.blockwise

import androidx.compose.runtime.staticCompositionLocalOf

internal enum class BoardBlockStyleMode(val storageValue: String) {
    Flat("flat"),
    MatchSelectedBlockStyle("match_selected_block_style"),
    ;

    companion object {
        fun fromStorageValue(value: String?): BoardBlockStyleMode? {
            return when (value) {
                null -> null
                "flat", "always_flat", "board_flat" -> Flat
                "match_selected_block_style", "match_block_style", "follow_block_style" -> MatchSelectedBlockStyle
                else -> entries.firstOrNull { it.storageValue == value }
            }
        }
    }
}

internal val SelectableBoardBlockStyleModes = BoardBlockStyleMode.entries

internal object BoardBlockStyleModeStore {
    private const val BOARD_BLOCK_STYLE_MODE_KEY = "board_block_style_mode"

    fun loadSelectedBoardBlockStyleMode(): BoardBlockStyleMode? {
        return BoardBlockStyleMode.fromStorageValue(PlatformAppSettings.getString(BOARD_BLOCK_STYLE_MODE_KEY))
    }

    fun saveSelectedBoardBlockStyleMode(mode: BoardBlockStyleMode) {
        PlatformAppSettings.putString(BOARD_BLOCK_STYLE_MODE_KEY, mode.storageValue)
    }
}

internal fun initializeBoardBlockStyleMode(): BoardBlockStyleMode {
    return BoardBlockStyleModeStore.loadSelectedBoardBlockStyleMode()
        ?: BoardBlockStyleMode.Flat.also(BoardBlockStyleModeStore::saveSelectedBoardBlockStyleMode)
}

internal fun resolveBoardBlockShapeStyle(
    mode: BoardBlockStyleMode,
    selectedBlockStyle: BlockVisualStyle,
): BlockVisualStyle {
    return selectedBlockStyle
}

internal fun resolveBoardEmptyBlockRenderStyle(
    mode: BoardBlockStyleMode,
    selectedBlockStyle: BlockVisualStyle,
): BlockVisualStyle {
    return when (mode) {
        BoardBlockStyleMode.Flat -> BlockVisualStyle.Flat
        BoardBlockStyleMode.MatchSelectedBlockStyle -> selectedBlockStyle
    }
}

internal fun resolveBoardFilledBlockRenderStyle(
    mode: BoardBlockStyleMode,
    selectedBlockStyle: BlockVisualStyle,
): BlockVisualStyle {
    return selectedBlockStyle
}

internal val LocalBoardBlockStyleMode = staticCompositionLocalOf { BoardBlockStyleMode.Flat }

