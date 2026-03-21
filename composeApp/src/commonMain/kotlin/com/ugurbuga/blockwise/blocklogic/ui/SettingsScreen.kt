package com.ugurbuga.blockwise.blocklogic.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import blockwise.composeapp.generated.resources.Res
import blockwise.composeapp.generated.resources.back
import blockwise.composeapp.generated.resources.language
import blockwise.composeapp.generated.resources.settings
import blockwise.composeapp.generated.resources.theme
import blockwise.composeapp.generated.resources.theme_dark
import blockwise.composeapp.generated.resources.theme_light
import blockwise.composeapp.generated.resources.theme_system
import com.ugurbuga.blockwise.AppLanguage
import com.ugurbuga.blockwise.AppThemeMode
import com.ugurbuga.blockwise.SelectableAppLanguages
import com.ugurbuga.blockwise.SelectableThemeModes
import com.ugurbuga.blockwise.localizedStringResource as stringResource
import com.ugurbuga.blockwise.ui.theme.BlockWiseTheme
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
internal fun SettingsScreen(
    selectedLanguage: AppLanguage,
    selectedThemeMode: AppThemeMode,
    onLanguageSelected: (AppLanguage) -> Unit,
    onThemeModeSelected: (AppThemeMode) -> Unit,
    onBack: () -> Unit,
    initialScroll: Int = 0,
    onScrollChanged: (Int) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState(initial = initialScroll)
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

    LaunchedEffect(scrollState) {
        snapshotFlow { scrollState.value }
            .distinctUntilChanged()
            .collectLatest(onScrollChanged)
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
                .widthIn(max = 760.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(Res.string.settings),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            Button(onClick = onBack) {
                Text(stringResource(Res.string.back))
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 760.dp),
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
                SettingsChipGroup(
                    title = stringResource(Res.string.theme),
                    selectedValue = selectedThemeMode,
                    options = themeModeOptions,
                    onSelected = onThemeModeSelected,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                )
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
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            options.forEach { option ->
                val isSelected = option.value == selectedValue
                FilterChip(
                    selected = isSelected,
                    onClick = { onSelected(option.value) },
                    label = { Text(option.label) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = MaterialTheme.colorScheme.outlineVariant,
                        selectedBorderColor = MaterialTheme.colorScheme.primary,
                    ),
                )
            }
        }
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

private data class SettingsChipOption<T>(
    val value: T,
    val label: String,
)

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    BlockWiseTheme {
        SettingsScreen(
            selectedLanguage = AppLanguage.English,
            selectedThemeMode = AppThemeMode.System,
            onLanguageSelected = {},
            onThemeModeSelected = {},
            onBack = {},
        )
    }
}

