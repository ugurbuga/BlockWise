package com.ugurbuga.blockwise

import androidx.compose.runtime.staticCompositionLocalOf

internal enum class AppColorPalette(val storageValue: String) {
    Classic("classic"),
    Aurora("aurora"),
    Sunset("sunset"),
    ;

    companion object {
        fun fromStorageValue(value: String?): AppColorPalette? {
            return entries.firstOrNull { it.storageValue == value }
        }
    }
}

internal val SelectableAppColorPalettes = AppColorPalette.entries

internal object AppColorPaletteStore {
    private const val COLOR_PALETTE_KEY = "app_color_palette"

    fun loadSelectedColorPalette(): AppColorPalette? {
        return AppColorPalette.fromStorageValue(PlatformAppSettings.getString(COLOR_PALETTE_KEY))
    }

    fun saveSelectedColorPalette(palette: AppColorPalette) {
        PlatformAppSettings.putString(COLOR_PALETTE_KEY, palette.storageValue)
    }
}

internal fun initializeAppColorPalette(): AppColorPalette {
    return AppColorPaletteStore.loadSelectedColorPalette()
        ?: AppColorPalette.Classic.also(AppColorPaletteStore::saveSelectedColorPalette)
}

internal val LocalAppColorPalette = staticCompositionLocalOf { AppColorPalette.Classic }
internal val LocalPaletteIsDarkTheme = staticCompositionLocalOf { false }

