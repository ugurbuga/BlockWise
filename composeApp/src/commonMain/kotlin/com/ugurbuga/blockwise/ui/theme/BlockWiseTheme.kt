package com.ugurbuga.blockwise.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import com.ugurbuga.blockwise.AppColorPalette
import com.ugurbuga.blockwise.LocalAppColorPalette
import com.ugurbuga.blockwise.LocalPaletteIsDarkTheme
import com.ugurbuga.blockwise.AppThemeMode

private fun lightScheme(palette: BlockWiseThemePalette): ColorScheme = lightColorScheme(
    primary = palette.primary,
    onPrimary = Color.White,
    primaryContainer = palette.primaryContainer,
    onPrimaryContainer = Color(0xFF1D255C),
    secondary = palette.secondary,
    onSecondary = Color.White,
    secondaryContainer = palette.secondaryContainer,
    onSecondaryContainer = Color(0xFF003739),
    tertiary = palette.tertiary,
    onTertiary = Color(0xFF4A2800),
    tertiaryContainer = palette.tertiaryContainer,
    onTertiaryContainer = Color(0xFF613B00),
    background = palette.background,
    onBackground = palette.onBackground,
    surface = palette.surface,
    onSurface = palette.onSurface,
    surfaceVariant = palette.surfaceVariant,
    onSurfaceVariant = palette.onSurfaceVariant,
    outline = palette.outline,
    outlineVariant = palette.outlineVariant,
    error = palette.error,
    onError = Color.White,
    errorContainer = palette.errorContainer,
    onErrorContainer = Color(0xFF410002),
)

private fun darkScheme(palette: BlockWiseThemePalette): ColorScheme = darkColorScheme(
    primary = palette.primary,
    onPrimary = Color(0xFF13256F),
    primaryContainer = palette.primaryContainer,
    onPrimaryContainer = Color(0xFFE0E5FF),
    secondary = palette.secondary,
    onSecondary = Color(0xFF003733),
    secondaryContainer = palette.secondaryContainer,
    onSecondaryContainer = Color(0xFFA4F1E8),
    tertiary = palette.tertiary,
    onTertiary = Color(0xFF472A00),
    tertiaryContainer = palette.tertiaryContainer,
    onTertiaryContainer = Color(0xFFFFDEB5),
    background = palette.background,
    onBackground = palette.onBackground,
    surface = palette.surface,
    onSurface = palette.onSurface,
    surfaceVariant = palette.surfaceVariant,
    onSurfaceVariant = palette.onSurfaceVariant,
    outline = palette.outline,
    outlineVariant = palette.outlineVariant,
    error = palette.error,
    onError = Color(0xFF690005),
    errorContainer = palette.errorContainer,
    onErrorContainer = Color(0xFFFFDAD6),
)

@Composable
internal fun BlockWiseTheme(
    themeMode: AppThemeMode = AppThemeMode.System,
    colorPalette: AppColorPalette = AppColorPalette.Classic,
    content: @Composable () -> Unit,
) {
    val useDarkTheme = when (themeMode) {
        AppThemeMode.System -> isSystemInDarkTheme()
        AppThemeMode.Light -> false
        AppThemeMode.Dark -> true
    }

    val palette = BlockWisePalette.themePalette(colorPalette, useDarkTheme)
    val colorScheme = if (useDarkTheme) darkScheme(palette) else lightScheme(palette)

    CompositionLocalProvider(
        LocalAppColorPalette provides colorPalette,
        LocalPaletteIsDarkTheme provides useDarkTheme,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
        ) {
            PlatformSystemBarsEffect(
                statusBarColor = colorScheme.background,
                navigationBarColor = colorScheme.surfaceVariant,
                darkTheme = useDarkTheme,
            )
            content()
        }
    }
}
