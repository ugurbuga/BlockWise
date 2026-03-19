package com.ugurbuga.blockwise.blocklogic.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ugurbuga.blockwise.blocklogic.domain.Difficulty
import com.ugurbuga.blockwise.blocklogic.domain.GridSize
import com.ugurbuga.blockwise.blocklogic.domain.toRules
import org.jetbrains.compose.resources.stringResource

import blockwise.composeapp.generated.resources.Res
import blockwise.composeapp.generated.resources.difficulty
import blockwise.composeapp.generated.resources.difficulty_easy
import blockwise.composeapp.generated.resources.difficulty_hard
import blockwise.composeapp.generated.resources.difficulty_normal
import blockwise.composeapp.generated.resources.grid_size
import blockwise.composeapp.generated.resources.grid_size_option
import blockwise.composeapp.generated.resources.level_selection_title
import blockwise.composeapp.generated.resources.rules_rule_1_title
import blockwise.composeapp.generated.resources.rules_rule_1_desc_disabled
import blockwise.composeapp.generated.resources.rules_rule_1_desc_enabled
import blockwise.composeapp.generated.resources.rules_rule_2_title
import blockwise.composeapp.generated.resources.rules_rule_2_desc_disabled
import blockwise.composeapp.generated.resources.rules_rule_2_desc_enabled
import blockwise.composeapp.generated.resources.play
import blockwise.composeapp.generated.resources.rules

@Composable
fun LevelSelectionScreen(
    selectedSize: GridSize,
    selectedDifficulty: Difficulty,
    onSizeSelected: (GridSize) -> Unit,
    onDifficultySelected: (Difficulty) -> Unit,
    onOpenRules: () -> Unit,
    onPlay: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val rules = selectedDifficulty.toRules()
    val gridSizeOptions = listOf(8, 10, 12).map { size ->
        ChipOption(
            value = GridSize(size),
            label = stringResource(Res.string.grid_size_option, size),
        )
    }
    val difficultyOptions = listOf(
        ChipOption(Difficulty.Easy, stringResource(Res.string.difficulty_easy)),
        ChipOption(Difficulty.Normal, stringResource(Res.string.difficulty_normal)),
        ChipOption(Difficulty.Hard, stringResource(Res.string.difficulty_hard)),
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            stringResource(Res.string.level_selection_title),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
        )

        SelectionChipGroup(
            title = stringResource(Res.string.grid_size),
            selectedValue = selectedSize,
            options = gridSizeOptions,
            onSelected = onSizeSelected,
        )

        SelectionChipGroup(
            title = stringResource(Res.string.difficulty),
            selectedValue = selectedDifficulty,
            options = difficultyOptions,
            onSelected = onDifficultySelected,
        )

        Surface(
            shape = RoundedCornerShape(24.dp),
            tonalElevation = 2.dp,
            color = MaterialTheme.colorScheme.surface,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(stringResource(Res.string.rules_rule_1_title), style = MaterialTheme.typography.titleMedium)
                Text(
                    text = if (rules.maxSameColorPerRow == null) {
                        stringResource(Res.string.rules_rule_1_desc_disabled)
                    } else {
                        stringResource(Res.string.rules_rule_1_desc_enabled, rules.maxSameColorPerRow)
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Text(stringResource(Res.string.rules_rule_2_title), style = MaterialTheme.typography.titleMedium)
                Text(
                    text = if (rules.maxSameColorPerCol == null) {
                        stringResource(Res.string.rules_rule_2_desc_disabled)
                    } else {
                        stringResource(Res.string.rules_rule_2_desc_enabled, rules.maxSameColorPerCol)
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Button(onClick = onOpenRules) {
                    Text(stringResource(Res.string.rules))
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onPlay,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(Res.string.play))
        }
    }
}

@Composable
private fun <T> SelectionChipGroup(
    title: String,
    selectedValue: T,
    options: List<ChipOption<T>>,
    onSelected: (T) -> Unit,
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
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
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
}

private data class ChipOption<T>(
    val value: T,
    val label: String,
)

