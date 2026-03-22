package com.ugurbuga.blockwise.blocklogic.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.draw.shadow
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import blockwise.composeapp.generated.resources.Res
import blockwise.composeapp.generated.resources.back
import blockwise.composeapp.generated.resources.block_style_flat
import blockwise.composeapp.generated.resources.block_style
import blockwise.composeapp.generated.resources.block_style_liquid_glass
import blockwise.composeapp.generated.resources.block_style_neon
import blockwise.composeapp.generated.resources.block_style_raised
import blockwise.composeapp.generated.resources.block_color_palette
import blockwise.composeapp.generated.resources.block_palette_candy
import blockwise.composeapp.generated.resources.block_palette_classic
import blockwise.composeapp.generated.resources.block_palette_earth
import blockwise.composeapp.generated.resources.block_palette_neon
import blockwise.composeapp.generated.resources.neon_pulse_speed
import blockwise.composeapp.generated.resources.neon_pulse_speed_fast
import blockwise.composeapp.generated.resources.neon_pulse_speed_normal
import blockwise.composeapp.generated.resources.neon_pulse_speed_slow
import blockwise.composeapp.generated.resources.color_palette
import blockwise.composeapp.generated.resources.color_palette_aurora
import blockwise.composeapp.generated.resources.color_palette_classic
import blockwise.composeapp.generated.resources.color_palette_sunset
import blockwise.composeapp.generated.resources.drag_finger_offset
import blockwise.composeapp.generated.resources.drag_finger_offset_high
import blockwise.composeapp.generated.resources.drag_finger_offset_low
import blockwise.composeapp.generated.resources.drag_finger_offset_medium
import blockwise.composeapp.generated.resources.drag_finger_offset_none
import blockwise.composeapp.generated.resources.invalid_placement_feedback_mode
import blockwise.composeapp.generated.resources.invalid_placement_feedback_mode_on_drop
import blockwise.composeapp.generated.resources.invalid_placement_feedback_mode_while_dragging
import blockwise.composeapp.generated.resources.language
import blockwise.composeapp.generated.resources.settings
import blockwise.composeapp.generated.resources.theme
import blockwise.composeapp.generated.resources.theme_dark
import blockwise.composeapp.generated.resources.theme_light
import blockwise.composeapp.generated.resources.theme_system
import com.ugurbuga.blockwise.AppColorPalette
import com.ugurbuga.blockwise.BlockColorPalette
import com.ugurbuga.blockwise.BlockVisualStyle
import com.ugurbuga.blockwise.DragFingerOffsetLevel
import com.ugurbuga.blockwise.InvalidPlacementFeedbackMode
import com.ugurbuga.blockwise.LocalBlockColorPalette
import com.ugurbuga.blockwise.LocalBlockVisualStyle
import com.ugurbuga.blockwise.LocalPaletteIsDarkTheme
import com.ugurbuga.blockwise.NeonPulseSpeed
import com.ugurbuga.blockwise.SelectableAppColorPalettes
import com.ugurbuga.blockwise.SelectableBlockColorPalettes
import com.ugurbuga.blockwise.SelectableBlockVisualStyles
import com.ugurbuga.blockwise.SelectableDragFingerOffsetLevels
import com.ugurbuga.blockwise.SelectableInvalidPlacementFeedbackModes
import com.ugurbuga.blockwise.SelectableNeonPulseSpeeds
import com.ugurbuga.blockwise.AppLanguage
import com.ugurbuga.blockwise.AppThemeMode
import com.ugurbuga.blockwise.SelectableAppLanguages
import com.ugurbuga.blockwise.SelectableThemeModes
import com.ugurbuga.blockwise.blocklogic.domain.BlockColor
import com.ugurbuga.blockwise.localizedStringResource as stringResource
import com.ugurbuga.blockwise.ui.theme.BlockWisePalette
import com.ugurbuga.blockwise.ui.theme.BlockWiseTheme
import com.ugurbuga.blockwise.ui.theme.toPaletteColor

@Composable
internal fun SettingsScreen(
    selectedLanguage: AppLanguage,
    selectedThemeMode: AppThemeMode,
    selectedThemeColorPalette: AppColorPalette,
    selectedBlockColorPalette: BlockColorPalette,
    selectedBlockVisualStyle: BlockVisualStyle,
    selectedNeonPulseSpeed: NeonPulseSpeed,
    selectedDragFingerOffsetLevel: DragFingerOffsetLevel,
    selectedInvalidPlacementFeedbackMode: InvalidPlacementFeedbackMode,
    onLanguageSelected: (AppLanguage) -> Unit,
    onThemeModeSelected: (AppThemeMode) -> Unit,
    onThemeColorPaletteSelected: (AppColorPalette) -> Unit,
    onBlockColorPaletteSelected: (BlockColorPalette) -> Unit,
    onBlockVisualStyleSelected: (BlockVisualStyle) -> Unit,
    onNeonPulseSpeedSelected: (NeonPulseSpeed) -> Unit,
    onDragFingerOffsetLevelSelected: (DragFingerOffsetLevel) -> Unit,
    onInvalidPlacementFeedbackModeSelected: (InvalidPlacementFeedbackMode) -> Unit,
    onBack: () -> Unit,
    initialScroll: Int = 0,
    onScrollChanged: (Int) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberPersistedScrollState(
        initialScroll = initialScroll,
        onScrollChanged = onScrollChanged,
    )
    val languageOptions = SelectableAppLanguages.map { language ->
        SettingsChipOption(
            value = language,
            label = language.endonym,
        )
    }
    val themeModeOptions = SelectableThemeModes.map { themeMode ->
        SettingsChipOption(
            value = themeMode,
            label = themeModeLabel(themeMode),
        )
    }
    val colorPaletteOptions = SelectableAppColorPalettes.map { palette ->
        SettingsChipOption(
            value = palette,
            label = colorPaletteLabel(palette),
            preview = { ThemePalettePreview(palette = palette) },
        )
    }
    val blockColorPaletteOptions = SelectableBlockColorPalettes.map { palette ->
        SettingsChipOption(
            value = palette,
            label = blockColorPaletteLabel(palette),
            preview = {
                BlockColorPalettePreview(
                    palette = palette,
                    style = selectedBlockVisualStyle,
                )
            },
        )
    }
    val blockVisualStyleOptions = SelectableBlockVisualStyles.map { style ->
        SettingsChipOption(
            value = style,
            label = blockVisualStyleLabel(style),
            preview = {
                BlockStylePreview(
                    style = style,
                    palette = selectedBlockColorPalette,
                )
            },
        )
    }
    val neonPulseOptions = SelectableNeonPulseSpeeds.map { speed ->
        SettingsChipOption(
            value = speed,
            label = neonPulseSpeedLabel(speed),
        )
    }
    val dragFingerOffsetOptions = SelectableDragFingerOffsetLevels.map { level ->
        SettingsChipOption(
            value = level,
            label = dragFingerOffsetLevelLabel(level),
        )
    }
    val invalidPlacementFeedbackOptions = SelectableInvalidPlacementFeedbackModes.map { mode ->
        SettingsChipOption(
            value = mode,
            label = invalidPlacementFeedbackModeLabel(mode),
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = ScreenContentMaxWidth),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(Res.string.back),
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = stringResource(Res.string.settings),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = ScreenContentMaxWidth),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                tonalElevation = 3.dp,
                color = MaterialTheme.colorScheme.surface,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    SettingsChipGroup(
                        title = stringResource(Res.string.language),
                        selectedValue = selectedLanguage,
                        options = languageOptions,
                        onSelected = onLanguageSelected,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(24.dp),
                tonalElevation = 2.dp,
                color = MaterialTheme.colorScheme.surface,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    SettingsChipGroup(
                        title = stringResource(Res.string.theme),
                        selectedValue = selectedThemeMode,
                        options = themeModeOptions,
                        onSelected = onThemeModeSelected,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    SettingsChipGroup(
                        title = stringResource(Res.string.color_palette),
                        selectedValue = selectedThemeColorPalette,
                        options = colorPaletteOptions,
                        onSelected = onThemeColorPaletteSelected,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    SettingsChipGroup(
                        title = stringResource(Res.string.block_color_palette),
                        selectedValue = selectedBlockColorPalette,
                        options = blockColorPaletteOptions,
                        onSelected = onBlockColorPaletteSelected,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    SettingsChipGroup(
                        title = stringResource(Res.string.block_style),
                        selectedValue = selectedBlockVisualStyle,
                        options = blockVisualStyleOptions,
                        onSelected = onBlockVisualStyleSelected,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    if (selectedBlockVisualStyle == BlockVisualStyle.Neon) {
                        SettingsChipGroup(
                            title = stringResource(Res.string.neon_pulse_speed),
                            selectedValue = selectedNeonPulseSpeed,
                            options = neonPulseOptions,
                            onSelected = onNeonPulseSpeedSelected,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    SettingsChipGroup(
                        title = stringResource(Res.string.drag_finger_offset),
                        selectedValue = selectedDragFingerOffsetLevel,
                        options = dragFingerOffsetOptions,
                        onSelected = onDragFingerOffsetLevelSelected,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    SettingsChipGroup(
                        title = stringResource(Res.string.invalid_placement_feedback_mode),
                        selectedValue = selectedInvalidPlacementFeedbackMode,
                        options = invalidPlacementFeedbackOptions,
                        onSelected = onInvalidPlacementFeedbackModeSelected,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@Composable
private fun <T> SettingsChipGroup(
    title: String,
    selectedValue: T,
    options: List<SettingsChipOption<T>>,
    onSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            options.forEach { option ->
                val isSelected = option.value == selectedValue
                val chipShape = RoundedCornerShape(22.dp)
                FilterChip(
                    modifier = Modifier.shadow(
                        elevation = if (isSelected) 10.dp else 1.dp,
                        shape = chipShape,
                        ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = if (isSelected) 0.24f else 0.06f),
                        spotColor = MaterialTheme.colorScheme.primary.copy(alpha = if (isSelected) 0.24f else 0.06f),
                    ),
                    selected = isSelected,
                    onClick = { onSelected(option.value) },
                    shape = chipShape,
                    label = {
                        Row(
                            modifier = Modifier.padding(vertical = 1.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            option.preview?.invoke()
                            Text(
                                text = option.label,
                                style = if (isSelected) {
                                    MaterialTheme.typography.labelLarge
                                } else {
                                    MaterialTheme.typography.labelMedium
                                },
                            )
                        }
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.96f),
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.38f),
                        labelColor = MaterialTheme.colorScheme.onSurface,
                    ),
                    elevation = FilterChipDefaults.filterChipElevation(
                        elevation = 0.dp,
                        pressedElevation = 0.dp,
                        focusedElevation = 0.dp,
                        hoveredElevation = 0.dp,
                        draggedElevation = 0.dp,
                        disabledElevation = 0.dp,
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.76f),
                        selectedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.94f),
                    ),
                )
            }
        }
    }
}


@Composable
private fun neonPulseSpeedLabel(speed: NeonPulseSpeed): String {
    return when (speed) {
        NeonPulseSpeed.Slow -> stringResource(Res.string.neon_pulse_speed_slow)
        NeonPulseSpeed.Normal -> stringResource(Res.string.neon_pulse_speed_normal)
        NeonPulseSpeed.Fast -> stringResource(Res.string.neon_pulse_speed_fast)
    }
}

@Composable
private fun dragFingerOffsetLevelLabel(level: DragFingerOffsetLevel): String {
    return when (level) {
        DragFingerOffsetLevel.None -> stringResource(Res.string.drag_finger_offset_none)
        DragFingerOffsetLevel.Low -> stringResource(Res.string.drag_finger_offset_low)
        DragFingerOffsetLevel.Medium -> stringResource(Res.string.drag_finger_offset_medium)
        DragFingerOffsetLevel.High -> stringResource(Res.string.drag_finger_offset_high)
    }
}

@Composable
private fun invalidPlacementFeedbackModeLabel(mode: InvalidPlacementFeedbackMode): String {
    return when (mode) {
        InvalidPlacementFeedbackMode.WhileDragging -> stringResource(
            Res.string.invalid_placement_feedback_mode_while_dragging,
        )
        InvalidPlacementFeedbackMode.OnDrop -> stringResource(
            Res.string.invalid_placement_feedback_mode_on_drop,
        )
    }
}

@Composable
private fun themeModeLabel(themeMode: AppThemeMode): String {
    return when (themeMode) {
        AppThemeMode.System -> stringResource(Res.string.theme_system)
        AppThemeMode.Light -> stringResource(Res.string.theme_light)
        AppThemeMode.Dark -> stringResource(Res.string.theme_dark)
    }
}

@Composable
private fun blockVisualStyleLabel(style: BlockVisualStyle): String {
    return when (style) {
        BlockVisualStyle.Flat -> stringResource(Res.string.block_style_flat)
        BlockVisualStyle.Raised3D -> stringResource(Res.string.block_style_raised)
        BlockVisualStyle.LiquidGlass -> stringResource(Res.string.block_style_liquid_glass)
        BlockVisualStyle.Neon -> stringResource(Res.string.block_style_neon)
    }
}

@Composable
private fun colorPaletteLabel(palette: AppColorPalette): String {
    return when (palette) {
        AppColorPalette.Classic -> stringResource(Res.string.color_palette_classic)
        AppColorPalette.Aurora -> stringResource(Res.string.color_palette_aurora)
        AppColorPalette.Sunset -> stringResource(Res.string.color_palette_sunset)
    }
}

@Composable
private fun blockColorPaletteLabel(palette: BlockColorPalette): String {
    return when (palette) {
        BlockColorPalette.Classic -> stringResource(Res.string.block_palette_classic)
        BlockColorPalette.Candy -> stringResource(Res.string.block_palette_candy)
        BlockColorPalette.Neon -> stringResource(Res.string.block_palette_neon)
        BlockColorPalette.Earth -> stringResource(Res.string.block_palette_earth)
    }
}

@Composable
private fun BlockStylePreview(style: BlockVisualStyle, palette: BlockColorPalette) {
    CompositionLocalProvider(
        LocalBlockVisualStyle provides style,
        LocalBlockColorPalette provides palette,
    ) {
        PreviewMiniCard {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BlockStylePreviewTile(
                    color = BlockColor.Red.toPaletteColor(),
                    interaction = BlockTileInteraction.Normal,
                )
                BlockStylePreviewTile(
                    color = BlockColor.Blue.toPaletteColor(),
                    interaction = if (style == BlockVisualStyle.Raised3D) {
                        BlockTileInteraction.Pressed
                    } else {
                        BlockTileInteraction.Normal
                    },
                )
                BlockStylePreviewTile(
                    color = BlockColor.Yellow.toPaletteColor(),
                    interaction = BlockTileInteraction.Normal,
                )
            }
        }
    }
}

@Composable
private fun ThemePalettePreview(
    palette: AppColorPalette,
) {
    val resolvedPalette = BlockWisePalette.themePalette(
        palette = palette,
        darkTheme = LocalPaletteIsDarkTheme.current,
    )
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ThemePalettePreviewSwatch(resolvedPalette.primary)
        ThemePalettePreviewSwatch(resolvedPalette.secondary)
        ThemePalettePreviewSwatch(resolvedPalette.tertiary)
        ThemePalettePreviewSwatch(resolvedPalette.surfaceVariant)
    }
}

@Composable
private fun BlockColorPalettePreview(
    palette: BlockColorPalette,
    style: BlockVisualStyle,
) {
    CompositionLocalProvider(
        LocalBlockVisualStyle provides style,
        LocalBlockColorPalette provides palette,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BlockStylePreviewTile(color = BlockColor.Red.toPaletteColor(), interaction = BlockTileInteraction.Normal)
            BlockStylePreviewTile(color = BlockColor.Green.toPaletteColor(), interaction = BlockTileInteraction.Normal)
            BlockStylePreviewTile(color = BlockColor.Blue.toPaletteColor(), interaction = BlockTileInteraction.Normal)
            BlockStylePreviewTile(color = BlockColor.Yellow.toPaletteColor(), interaction = BlockTileInteraction.Normal)
        }
    }
}

@Composable
private fun PreviewMiniCard(content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier.padding(end = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        content = content,
    )
}

@Composable
private fun ThemePalettePreviewSwatch(color: Color) {
    Surface(
        modifier = Modifier.size(width = 14.dp, height = 14.dp),
        shape = RoundedCornerShape(6.dp),
        color = color,
        tonalElevation = 1.dp,
    ) {}
}

@Composable
private fun BlockStylePreviewTile(
    color: Color,
    interaction: BlockTileInteraction,
) {
    Box(modifier = Modifier.size(14.dp)) {
        BlockTile3D(
            fillColor = color,
            borderWidth = 1.dp,
            cornerRadius = 4.dp,
            elevation = 3.dp,
            interaction = interaction,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

private data class SettingsChipOption<T>(
    val value: T,
    val label: String,
    val preview: (@Composable (() -> Unit))? = null,
)

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    BlockWiseTheme {
        SettingsScreen(
            selectedLanguage = AppLanguage.English,
            selectedThemeMode = AppThemeMode.System,
            selectedThemeColorPalette = AppColorPalette.Classic,
            selectedBlockColorPalette = BlockColorPalette.Classic,
            selectedBlockVisualStyle = BlockVisualStyle.Flat,
            selectedNeonPulseSpeed = NeonPulseSpeed.Normal,
            selectedDragFingerOffsetLevel = DragFingerOffsetLevel.Medium,
            selectedInvalidPlacementFeedbackMode = InvalidPlacementFeedbackMode.OnDrop,
            onLanguageSelected = {},
            onThemeModeSelected = {},
            onThemeColorPaletteSelected = {},
            onBlockColorPaletteSelected = {},
            onBlockVisualStyleSelected = {},
            onNeonPulseSpeedSelected = {},
            onDragFingerOffsetLevelSelected = {},
            onInvalidPlacementFeedbackModeSelected = {},
            onBack = {},
        )
    }
}

