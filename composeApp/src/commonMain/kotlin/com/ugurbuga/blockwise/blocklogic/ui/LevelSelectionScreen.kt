package com.ugurbuga.blockwise.blocklogic.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.ArrowDropUp
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ugurbuga.blockwise.AppLanguage
import com.ugurbuga.blockwise.SelectableAppLanguages
import com.ugurbuga.blockwise.localizedStringResource as stringResource
import com.ugurbuga.blockwise.blocklogic.domain.Difficulty
import com.ugurbuga.blockwise.blocklogic.domain.GridSize
import com.ugurbuga.blockwise.blocklogic.domain.resolveGameConfig
import com.ugurbuga.blockwise.blocklogic.domain.supportedGridSizes
import com.ugurbuga.blockwise.ui.theme.BlockWiseTheme

import blockwise.composeapp.generated.resources.Res
import blockwise.composeapp.generated.resources.difficulty
import blockwise.composeapp.generated.resources.difficulty_easy
import blockwise.composeapp.generated.resources.difficulty_hard
import blockwise.composeapp.generated.resources.difficulty_normal
import blockwise.composeapp.generated.resources.difficulty_very_hard
import blockwise.composeapp.generated.resources.grid_size
import blockwise.composeapp.generated.resources.grid_size_option
import blockwise.composeapp.generated.resources.level_selection_title
import blockwise.composeapp.generated.resources.scores
import blockwise.composeapp.generated.resources.selected_mode_best_score
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
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LevelSelectionScreen(
    selectedSize: GridSize,
    selectedDifficulty: Difficulty,
    selectedLanguage: AppLanguage,
    onSizeSelected: (GridSize) -> Unit,
    onDifficultySelected: (Difficulty) -> Unit,
    onLanguageSelected: (AppLanguage) -> Unit,
    onOpenRules: () -> Unit,
    onOpenScores: () -> Unit,
    onPlay: () -> Unit,
    bestScoreForSelection: Int?,
    initialScroll: Int = 0,
    onScrollChanged: (Int) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val config = resolveGameConfig(selectedSize, selectedDifficulty)
    val scrollState = rememberScrollState(initial = initialScroll)
    var isLanguageMenuExpanded by remember { mutableStateOf(false) }
    var pendingLanguageSelection by remember { mutableStateOf<AppLanguage?>(null) }
    val gridSizeOptions = supportedGridSizes().map { size ->
        ChipOption(
            value = size,
            label = stringResource(Res.string.grid_size_option, size.value),
        )
    }
    val difficultyOptions = listOf(
        ChipOption(Difficulty.Easy, stringResource(Res.string.difficulty_easy)),
        ChipOption(Difficulty.Normal, stringResource(Res.string.difficulty_normal)),
        ChipOption(Difficulty.Hard, stringResource(Res.string.difficulty_hard)),
        ChipOption(Difficulty.VeryHard, stringResource(Res.string.difficulty_very_hard)),
    )
    LaunchedEffect(scrollState) {
        snapshotFlow { scrollState.value }
            .distinctUntilChanged()
            .collectLatest(onScrollChanged)
    }

    LaunchedEffect(isLanguageMenuExpanded, pendingLanguageSelection) {
        val language = pendingLanguageSelection ?: return@LaunchedEffect
        if (!isLanguageMenuExpanded) {
            withFrameNanos { }
            onLanguageSelected(language)
            pendingLanguageSelection = null
        }
    }

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
                .widthIn(max = 760.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(Res.string.level_selection_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                )

                ExposedDropdownMenuBox(
                    expanded = isLanguageMenuExpanded,
                    onExpandedChange = { isLanguageMenuExpanded = !isLanguageMenuExpanded },
                    modifier = Modifier.wrapContentWidth(Alignment.End),
                ) {
                    val languageSelectorShape = RoundedCornerShape(20.dp)
                    Row(
                        modifier = Modifier
                            .menuAnchor(
                                type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                                enabled = true,
                            )
                            .clip(languageSelectorShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outlineVariant,
                                shape = languageSelectorShape,
                            )
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Translate,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                        )
                        Text(
                            text = selectedLanguage.abbreviation,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Icon(
                            imageVector = if (isLanguageMenuExpanded) {
                                Icons.Rounded.ArrowDropUp
                            } else {
                                Icons.Rounded.ArrowDropDown
                            },
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }

                    ExposedDropdownMenu(
                        expanded = isLanguageMenuExpanded,
                        onDismissRequest = { isLanguageMenuExpanded = false },
                        modifier = Modifier.widthIn(min = 200.dp),
                        shape = RoundedCornerShape(20.dp),
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 4.dp,
                        shadowElevation = 8.dp,
                    ) {
                        SelectableAppLanguages.forEach { language ->
                            val isSelected = language == selectedLanguage
                            DropdownMenuItem(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(
                                        color = if (isSelected) {
                                            MaterialTheme.colorScheme.primaryContainer
                                        } else {
                                            Color.Transparent
                                        },
                                    ),
                                text = {
                                    Text(
                                        text = languageLabel(language),
                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                                        color = if (isSelected) {
                                            MaterialTheme.colorScheme.onPrimaryContainer
                                        } else {
                                            MaterialTheme.colorScheme.onSurface
                                        },
                                        maxLines = 1,
                                        softWrap = false,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                },
                                leadingIcon = {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Rounded.Check,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                        )
                                    } else {
                                        Spacer(modifier = Modifier.width(24.dp))
                                    }
                                },
                                trailingIcon = {
                                    Text(
                                        text = language.abbreviation,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isSelected) {
                                            MaterialTheme.colorScheme.onPrimaryContainer
                                        } else {
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                        },
                                    )
                                },
                                colors = MenuDefaults.itemColors(
                                    textColor = MaterialTheme.colorScheme.onSurface,
                                    leadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    trailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                                    disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f),
                                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f),
                                ),
                                onClick = {
                                    isLanguageMenuExpanded = false
                                    pendingLanguageSelection = language
                                },
                            )
                        }
                    }
                }
            }

            bestScoreForSelection?.let { bestScore ->
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    tonalElevation = 2.dp,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                ) {
                    Text(
                        text = stringResource(Res.string.selected_mode_best_score, bestScore),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                }
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

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(onClick = onOpenRules) {
                            Text(stringResource(Res.string.rules))
                        }
                        Button(onClick = onOpenScores) {
                            Text(stringResource(Res.string.scores))
                        }
                    }
                }
            }

        }
    }
}

@Composable
private fun languageLabel(language: AppLanguage): String {
    return language.endonym
}

@Composable
private fun <T> SelectionChipGroup(
    title: String,
    selectedValue: T,
    options: List<ChipOption<T>>,
    onSelected: (T) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
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

@Composable
private fun difficultyLabel(difficulty: Difficulty): String {
    return when (difficulty) {
        Difficulty.Easy -> stringResource(Res.string.difficulty_easy)
        Difficulty.Normal -> stringResource(Res.string.difficulty_normal)
        Difficulty.Hard -> stringResource(Res.string.difficulty_hard)
        Difficulty.VeryHard -> stringResource(Res.string.difficulty_very_hard)
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
            selectedLanguage = AppLanguage.English,
            onSizeSelected = {},
            onDifficultySelected = {},
            onLanguageSelected = {},
            onOpenRules = {},
            onOpenScores = {},
            onPlay = {},
            bestScoreForSelection = 124,
        )
    }
}

