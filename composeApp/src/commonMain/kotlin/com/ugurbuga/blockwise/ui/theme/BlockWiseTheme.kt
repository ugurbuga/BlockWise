package com.ugurbuga.blockwise.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.ugurbuga.blockwise.AppThemeMode

private val BlockWiseLightColorScheme = lightColorScheme(
    primary = BlockWisePalette.Light.primary,
    onPrimary = Color.White,
    primaryContainer = BlockWisePalette.Light.primaryContainer,
    onPrimaryContainer = Color(0xFF1D255C),
    secondary = BlockWisePalette.Light.secondary,
    onSecondary = Color.White,
    secondaryContainer = BlockWisePalette.Light.secondaryContainer,
    onSecondaryContainer = Color(0xFF003739),
    tertiary = BlockWisePalette.Light.tertiary,
    onTertiary = Color(0xFF4A2800),
    tertiaryContainer = BlockWisePalette.Light.tertiaryContainer,
    onTertiaryContainer = Color(0xFF613B00),
    background = BlockWisePalette.Light.background,
    onBackground = BlockWisePalette.Light.onBackground,
    surface = BlockWisePalette.Light.surface,
    onSurface = BlockWisePalette.Light.onSurface,
    surfaceVariant = BlockWisePalette.Light.surfaceVariant,
    onSurfaceVariant = BlockWisePalette.Light.onSurfaceVariant,
    outline = BlockWisePalette.Light.outline,
    outlineVariant = BlockWisePalette.Light.outlineVariant,
    error = BlockWisePalette.Light.error,
    onError = Color.White,
    errorContainer = BlockWisePalette.Light.errorContainer,
    onErrorContainer = Color(0xFF410002),
)

private val BlockWiseDarkColorScheme = darkColorScheme(
    primary = BlockWisePalette.Dark.primary,
    onPrimary = Color(0xFF13256F),
    primaryContainer = BlockWisePalette.Dark.primaryContainer,
    onPrimaryContainer = Color(0xFFE0E5FF),
    secondary = BlockWisePalette.Dark.secondary,
    onSecondary = Color(0xFF003733),
    secondaryContainer = BlockWisePalette.Dark.secondaryContainer,
    onSecondaryContainer = Color(0xFFA4F1E8),
    tertiary = BlockWisePalette.Dark.tertiary,
    onTertiary = Color(0xFF472A00),
    tertiaryContainer = BlockWisePalette.Dark.tertiaryContainer,
    onTertiaryContainer = Color(0xFFFFDEB5),
    background = BlockWisePalette.Dark.background,
    onBackground = BlockWisePalette.Dark.onBackground,
    surface = BlockWisePalette.Dark.surface,
    onSurface = BlockWisePalette.Dark.onSurface,
    surfaceVariant = BlockWisePalette.Dark.surfaceVariant,
    onSurfaceVariant = BlockWisePalette.Dark.onSurfaceVariant,
    outline = BlockWisePalette.Dark.outline,
    outlineVariant = BlockWisePalette.Dark.outlineVariant,
    error = BlockWisePalette.Dark.error,
    onError = Color(0xFF690005),
    errorContainer = BlockWisePalette.Dark.errorContainer,
    onErrorContainer = Color(0xFFFFDAD6),
)

@Composable
internal fun BlockWiseTheme(
    themeMode: AppThemeMode = AppThemeMode.System,
    content: @Composable () -> Unit,
) {
    val useDarkTheme = when (themeMode) {
        AppThemeMode.System -> isSystemInDarkTheme()
        AppThemeMode.Light -> false
        AppThemeMode.Dark -> true
    }

    val colorScheme = if (useDarkTheme) BlockWiseDarkColorScheme else BlockWiseLightColorScheme

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
