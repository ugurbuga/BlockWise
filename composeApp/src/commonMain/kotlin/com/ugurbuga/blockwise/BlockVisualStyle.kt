package com.ugurbuga.blockwise

import androidx.compose.runtime.staticCompositionLocalOf

internal enum class BlockVisualStyle(val storageValue: String) {
    Flat("flat"),
    Raised3D("raised_3d"),
    LiquidGlass("liquid_glass"),
    Neon("neon"),
    ;

    companion object {
        fun fromStorageValue(value: String?): BlockVisualStyle? {
            return when (value) {
                null -> null
                "flat" -> Flat
                "soft_3d", "strong_3d", "pressed", "raised_3d" -> Raised3D
                "glossy", "liquid_glass" -> LiquidGlass
                "neon" -> Neon
                else -> entries.firstOrNull { it.storageValue == value }
            }
        }
    }
}

internal val SelectableBlockVisualStyles = BlockVisualStyle.entries

internal object BlockVisualStyleStore {
    private const val BLOCK_VISUAL_STYLE_KEY = "block_visual_style"

    fun loadSelectedBlockVisualStyle(): BlockVisualStyle? {
        return BlockVisualStyle.fromStorageValue(PlatformAppSettings.getString(BLOCK_VISUAL_STYLE_KEY))
    }

    fun saveSelectedBlockVisualStyle(style: BlockVisualStyle) {
        PlatformAppSettings.putString(BLOCK_VISUAL_STYLE_KEY, style.storageValue)
    }
}

internal fun initializeBlockVisualStyle(): BlockVisualStyle {
    return BlockVisualStyleStore.loadSelectedBlockVisualStyle()
        ?: BlockVisualStyle.Flat.also(BlockVisualStyleStore::saveSelectedBlockVisualStyle)
}

internal val LocalBlockVisualStyle = staticCompositionLocalOf { BlockVisualStyle.Flat }

