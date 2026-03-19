package com.ugurbuga.blockwise.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val BlockWiseColorScheme = lightColorScheme(
    primary = BlockWisePalette.primary,
    onPrimary = Color.White,
    primaryContainer = BlockWisePalette.primaryContainer,
    onPrimaryContainer = Color(0xFF1D255C),
    secondary = BlockWisePalette.secondary,
    onSecondary = Color.White,
    secondaryContainer = BlockWisePalette.secondaryContainer,
    onSecondaryContainer = Color(0xFF003739),
    tertiary = BlockWisePalette.tertiary,
    onTertiary = Color(0xFF4A2800),
    tertiaryContainer = BlockWisePalette.tertiaryContainer,
    onTertiaryContainer = Color(0xFF613B00),
    background = BlockWisePalette.background,
    onBackground = BlockWisePalette.onBackground,
    surface = BlockWisePalette.surface,
    onSurface = BlockWisePalette.onSurface,
    surfaceVariant = BlockWisePalette.surfaceVariant,
    onSurfaceVariant = BlockWisePalette.onSurfaceVariant,
    outline = BlockWisePalette.outline,
    outlineVariant = BlockWisePalette.outlineVariant,
    error = BlockWisePalette.error,
    onError = Color.White,
    errorContainer = BlockWisePalette.errorContainer,
    onErrorContainer = Color(0xFF410002),
)

@Composable
fun BlockWiseTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = BlockWiseColorScheme,
        content = content,
    )
}
