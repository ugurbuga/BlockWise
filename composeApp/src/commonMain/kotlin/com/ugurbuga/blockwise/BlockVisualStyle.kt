package com.ugurbuga.blockwise

import androidx.compose.runtime.staticCompositionLocalOf

internal enum class BlockVisualStyle(val storageValue: String) {
    Flat("flat"),
    Bubble("bubble"),
    Outline("outline"),
    Sharp3D("sharp_3d"),
    Wood("wood"),
    LiquidGlass("liquid_glass"),
    Neon("neon"),
    ;

    companion object {
        fun fromStorageValue(value: String?): BlockVisualStyle? {
            return when (value) {
                null -> null
                "flat" -> Flat
                "bubble", "candy" -> Bubble
                "outline", "wireframe" -> Outline
                "soft_3d", "rounded_soft_3d" -> Bubble
                "neumorphism", "soft_ui", "softui" -> Flat
                "clay", "claymorphism" -> Flat
                "pressed", "raised_3d" -> Sharp3D
                "strong_3d", "sharp_3d" -> Sharp3D
                "wood", "wooden" -> Wood
                "glossy", "liquid_glass" -> LiquidGlass
                "neon" -> Neon
                else -> entries.firstOrNull { it.storageValue == value }
            }
        }
    }
}

internal val SelectableBlockVisualStyles = listOf(
    BlockVisualStyle.Flat,
    BlockVisualStyle.Bubble,
    BlockVisualStyle.Outline,
    BlockVisualStyle.Sharp3D,
    BlockVisualStyle.Wood,
    BlockVisualStyle.LiquidGlass,
    BlockVisualStyle.Neon,
)

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

