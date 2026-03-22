package com.ugurbuga.blockwise

import androidx.compose.runtime.staticCompositionLocalOf

internal enum class BlockColorPalette(val storageValue: String) {
    Classic("classic"),
    Candy("candy"),
    Neon("neon"),
    Earth("earth"),
    ;

    companion object {
        fun fromStorageValue(value: String?): BlockColorPalette? {
            return entries.firstOrNull { it.storageValue == value }
        }
    }
}

internal val SelectableBlockColorPalettes = BlockColorPalette.entries

internal object BlockColorPaletteStore {
    private const val BLOCK_COLOR_PALETTE_KEY = "block_color_palette"

    fun loadSelectedBlockColorPalette(): BlockColorPalette? {
        return BlockColorPalette.fromStorageValue(PlatformAppSettings.getString(BLOCK_COLOR_PALETTE_KEY))
    }

    fun saveSelectedBlockColorPalette(palette: BlockColorPalette) {
        PlatformAppSettings.putString(BLOCK_COLOR_PALETTE_KEY, palette.storageValue)
    }
}

internal fun initializeBlockColorPalette(): BlockColorPalette {
    return BlockColorPaletteStore.loadSelectedBlockColorPalette()
        ?: BlockColorPalette.Classic.also(BlockColorPaletteStore::saveSelectedBlockColorPalette)
}

internal val LocalBlockColorPalette = staticCompositionLocalOf { BlockColorPalette.Classic }

