package com.ugurbuga.blockwise.blocklogic.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ugurbuga.blockwise.localizedStringResource as stringResource
import com.ugurbuga.blockwise.blocklogic.domain.Difficulty
import com.ugurbuga.blockwise.blocklogic.domain.GridSize
import com.ugurbuga.blockwise.blocklogic.domain.resolveGameConfig
import com.ugurbuga.blockwise.blocklogic.domain.supportedGridSizes
import com.ugurbuga.blockwise.ui.theme.BlockWiseTheme

import blockwise.composeapp.generated.resources.Res
import blockwise.composeapp.generated.resources.difficulty
import blockwise.composeapp.generated.resources.grid_size
import blockwise.composeapp.generated.resources.grid_size_option
import blockwise.composeapp.generated.resources.level_selection_title
import blockwise.composeapp.generated.resources.scores
import blockwise.composeapp.generated.resources.selected_mode_best_score
import blockwise.composeapp.generated.resources.settings
import blockwise.composeapp.generated.resources.rules_rule_1_title
import blockwise.composeapp.generated.resources.rules_rule_1_desc_disabled
import blockwise.composeapp.generated.resources.rules_rule_1_desc_enabled
import blockwise.composeapp.generated.resources.rules_rule_2_title
import blockwise.composeapp.generated.resources.rules_rule_2_desc_disabled
import blockwise.composeapp.generated.resources.rules_rule_2_desc_enabled
import blockwise.composeapp.generated.resources.rules_piece_pool_title
import blockwise.composeapp.generated.resources.rules_piece_pool_desc
import blockwise.composeapp.generated.resources.rules_adjacent_limit_title
import blockwise.composeapp.generated.resources.rules_adjacent_limit_desc_disabled
import blockwise.composeapp.generated.resources.rules_adjacent_limit_desc_enabled
import blockwise.composeapp.generated.resources.rules_variety_title
import blockwise.composeapp.generated.resources.rules_variety_desc_disabled
import blockwise.composeapp.generated.resources.rules_variety_desc_enabled
import blockwise.composeapp.generated.resources.rules_move_limit_title
import blockwise.composeapp.generated.resources.rules_move_limit_desc_disabled
import blockwise.composeapp.generated.resources.rules_move_limit_desc_enabled
import blockwise.composeapp.generated.resources.rules_prefilled_title
import blockwise.composeapp.generated.resources.rules_prefilled_desc
import blockwise.composeapp.generated.resources.play
import blockwise.composeapp.generated.resources.rules
import blockwise.composeapp.generated.resources.rules_title
import blockwise.composeapp.generated.resources.rules_current_mode
import blockwise.composeapp.generated.resources.scores_empty

@Composable
internal fun LevelSelectionScreen(
    selectedSize: GridSize,
    selectedDifficulty: Difficulty,
    onSizeSelected: (GridSize) -> Unit,
    onDifficultySelected: (Difficulty) -> Unit,
    onOpenRules: () -> Unit,
    onOpenScores: () -> Unit,
    onOpenSettings: () -> Unit,
    onPlay: () -> Unit,
    bestScoreForSelection: Int?,
    initialScroll: Int = 0,
    onScrollChanged: (Int) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val config = resolveGameConfig(selectedSize, selectedDifficulty)
    val scrollState = rememberPersistedScrollState(
        initialScroll = initialScroll,
        onScrollChanged = onScrollChanged,
    )
    val gridSizeOptions = supportedGridSizes().map { size ->
        ChipOption(
            value = size,
            label = stringResource(Res.string.grid_size_option, size.value),
        )
    }
    val difficultyOptions = listOf(
        ChipOption(Difficulty.Easy, difficultyLabel(Difficulty.Easy)),
        ChipOption(Difficulty.Normal, difficultyLabel(Difficulty.Normal)),
        ChipOption(Difficulty.Hard, difficultyLabel(Difficulty.Hard)),
        ChipOption(Difficulty.VeryHard, difficultyLabel(Difficulty.VeryHard)),
    )
    val bestScoreLabel = formatBestScore(
        bestScore = bestScoreForSelection,
        emptyPlaceholder = stringResource(Res.string.scores_empty),
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = ScreenContentMaxWidth),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(Res.string.level_selection_title),
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Button(onClick = onOpenSettings) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = null,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(Res.string.settings))
                }
            }

            Surface(
                shape = RoundedCornerShape(20.dp),
                tonalElevation = 2.dp,
                color = MaterialTheme.colorScheme.primaryContainer,
            ) {
                Text(
                    text = stringResource(Res.string.selected_mode_best_score, bestScoreLabel),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }

            Surface(
                shape = RoundedCornerShape(24.dp),
                tonalElevation = 3.dp,
                color = MaterialTheme.colorScheme.surface,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
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


                    Button(
                        onClick = onPlay,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = null,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(Res.string.play))
                    }
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
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = stringResource(Res.string.rules_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = stringResource(
                            Res.string.rules_current_mode,
                            stringResource(Res.string.grid_size_option, selectedSize.value),
                            difficultyLabel(selectedDifficulty),
                        ),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    RuleSummaryItem(
                        title = stringResource(Res.string.rules_rule_1_title),
                        description = config.rules.maxSameColorPerRow?.let {
                            emphasizedRuleText(
                                text = stringResource(Res.string.rules_rule_1_desc_enabled, it),
                                highlights = listOf(it.toString()),
                            )
                        } ?: AnnotatedString(stringResource(Res.string.rules_rule_1_desc_disabled)),
                    )

                    RuleSummaryItem(
                        title = stringResource(Res.string.rules_rule_2_title),
                        description = config.rules.maxSameColorPerCol?.let {
                            emphasizedRuleText(
                                text = stringResource(Res.string.rules_rule_2_desc_enabled, it),
                                highlights = listOf(it.toString()),
                            )
                        } ?: AnnotatedString(stringResource(Res.string.rules_rule_2_desc_disabled)),
                    )

                    RuleSummaryItem(
                        title = stringResource(Res.string.rules_piece_pool_title),
                        description = emphasizedRuleText(
                            text = stringResource(Res.string.rules_piece_pool_desc, config.maxShapeDimension),
                            highlights = listOf("${config.maxShapeDimension}x${config.maxShapeDimension}"),
                        ),
                    )

                    RuleSummaryItem(
                        title = stringResource(Res.string.rules_adjacent_limit_title),
                        description = config.rules.maxAdjacentSameColor?.let {
                            emphasizedRuleText(
                                text = stringResource(Res.string.rules_adjacent_limit_desc_enabled, it),
                                highlights = listOf(it.toString()),
                            )
                        } ?: AnnotatedString(stringResource(Res.string.rules_adjacent_limit_desc_disabled)),
                    )

                    RuleSummaryItem(
                        title = stringResource(Res.string.rules_variety_title),
                        description = config.rules.minDistinctColorsInFullLine?.let {
                            emphasizedRuleText(
                                text = stringResource(Res.string.rules_variety_desc_enabled, it),
                                highlights = listOf(it.toString()),
                            )
                        } ?: AnnotatedString(stringResource(Res.string.rules_variety_desc_disabled)),
                    )

                    RuleSummaryItem(
                        title = stringResource(Res.string.rules_move_limit_title),
                        description = config.rules.moveLimit?.let {
                            emphasizedRuleText(
                                text = stringResource(Res.string.rules_move_limit_desc_enabled, it),
                                highlights = listOf(it.toString()),
                            )
                        } ?: AnnotatedString(stringResource(Res.string.rules_move_limit_desc_disabled)),
                    )

                    val prefilledPercent = (config.difficultyConfig.preFilledRatio * 100).toInt()
                    val lockedPercent = (config.difficultyConfig.lockedCellsRatio * 100).toInt()
                    RuleSummaryItem(
                        title = stringResource(Res.string.rules_prefilled_title),
                        description = emphasizedRuleText(
                            text = stringResource(
                                Res.string.rules_prefilled_desc,
                                prefilledPercent,
                                lockedPercent,
                            ),
                            highlights = listOf("$prefilledPercent%", "$lockedPercent%"),
                        ),
                    )

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Button(onClick = onOpenRules) {
                            Icon(
                                imageVector = Icons.Filled.Gavel,
                                contentDescription = null,
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(Res.string.rules))
                        }
                        Button(onClick = onOpenScores) {
                            Icon(
                                imageVector = Icons.Filled.EmojiEvents,
                                contentDescription = null,
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(Res.string.scores))
                        }
                    }
                }
            }

        }
    }
}

@Composable
private fun <T> SelectionChipGroup(
    title: String,
    selectedValue: T,
    options: List<ChipOption<T>>,
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
                        borderColor = MaterialTheme.colorScheme.outlineVariant,
                        selectedBorderColor = MaterialTheme.colorScheme.primary,
                    ),
                )
            }
        }
    }
}

@Composable
private fun RuleSummaryItem(
    title: String,
    description: AnnotatedString,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
        )
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun emphasizedRuleText(
    text: String,
    highlights: List<String>,
): AnnotatedString {
    val highlightStyle = SpanStyle(
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.SemiBold,
    )
    return buildAnnotatedString {
        append(text)
        highlights
            .filter { it.isNotBlank() }
            .distinct()
            .sortedByDescending { it.length }
            .forEach { highlight ->
                var startIndex = text.indexOf(highlight)
                while (startIndex >= 0) {
                    addStyle(highlightStyle, startIndex, startIndex + highlight.length)
                    startIndex = text.indexOf(highlight, startIndex + highlight.length)
                }
            }
    }
}


private data class ChipOption<T>(
    val value: T,
    val label: String,
)

@Preview(showBackground = true)
@Composable
private fun LevelSelectionScreenPreview() {
    BlockWiseTheme {
        LevelSelectionScreen(
            selectedSize = GridSize(14),
            selectedDifficulty = Difficulty.VeryHard,
            onSizeSelected = {},
            onDifficultySelected = {},
            onOpenRules = {},
            onOpenScores = {},
            onOpenSettings = {},
            onPlay = {},
            bestScoreForSelection = 124,
        )
    }
}

