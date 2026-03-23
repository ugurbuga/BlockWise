package com.ugurbuga.blockwise

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

internal enum class BlockGapSpacing(
    val storageValue: String,
    val gapDp: Dp,
) {
    None(storageValue = "none", gapDp = 0.dp),
    Low(storageValue = "low", gapDp = 2.dp),
    High(storageValue = "high", gapDp = 6.dp),
    ;

    companion object {
        fun fromStorageValue(value: String?): BlockGapSpacing? {
            return when (value) {
                null -> null
                "none" -> None
                "low", "small", "medium", "default" -> Low
                "high", "large" -> High
                else -> entries.firstOrNull { it.storageValue == value }
            }
        }
    }
}

internal val SelectableBlockGapSpacings = BlockGapSpacing.entries

internal object BlockGapSpacingStore {
    private const val BLOCK_GAP_SPACING_KEY = "block_gap_spacing"

    fun loadSelectedBlockGapSpacing(): BlockGapSpacing? {
        return BlockGapSpacing.fromStorageValue(PlatformAppSettings.getString(BLOCK_GAP_SPACING_KEY))
    }

    fun saveSelectedBlockGapSpacing(spacing: BlockGapSpacing) {
        PlatformAppSettings.putString(BLOCK_GAP_SPACING_KEY, spacing.storageValue)
    }
}

internal fun initializeBlockGapSpacing(): BlockGapSpacing {
    return BlockGapSpacingStore.loadSelectedBlockGapSpacing()
        ?: BlockGapSpacing.Low.also(BlockGapSpacingStore::saveSelectedBlockGapSpacing)
}

internal val LocalBlockGapSpacing = staticCompositionLocalOf { BlockGapSpacing.Low }

