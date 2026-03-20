package com.ugurbuga.blockwise.blocklogic.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ugurbuga.blockwise.localizedStringResource as stringResource
import com.ugurbuga.blockwise.blocklogic.domain.BlockColor
import com.ugurbuga.blockwise.blocklogic.domain.Difficulty
import com.ugurbuga.blockwise.blocklogic.domain.GridSize
import com.ugurbuga.blockwise.blocklogic.domain.Piece
import com.ugurbuga.blockwise.blocklogic.domain.resolveGameConfig
import com.ugurbuga.blockwise.blocklogic.domain.Shapes
import com.ugurbuga.blockwise.ui.theme.BlockWiseTheme
import com.ugurbuga.blockwise.ui.theme.toPaletteColor

import blockwise.composeapp.generated.resources.Res
import blockwise.composeapp.generated.resources.back
import blockwise.composeapp.generated.resources.grid_size_option
import blockwise.composeapp.generated.resources.rules_intro
import blockwise.composeapp.generated.resources.rules_rule_1_desc_disabled
import blockwise.composeapp.generated.resources.rules_rule_1_desc_enabled
import blockwise.composeapp.generated.resources.rules_rule_1_title
import blockwise.composeapp.generated.resources.rules_rule_2_desc_disabled
import blockwise.composeapp.generated.resources.rules_rule_2_desc_enabled
import blockwise.composeapp.generated.resources.rules_rule_2_title
import blockwise.composeapp.generated.resources.rules_current_mode
import blockwise.composeapp.generated.resources.rules_piece_pool_desc
import blockwise.composeapp.generated.resources.rules_piece_pool_title
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
import blockwise.composeapp.generated.resources.rules_tips_desc
import blockwise.composeapp.generated.resources.rules_tips_title
import blockwise.composeapp.generated.resources.rules_title
import blockwise.composeapp.generated.resources.difficulty_easy
import blockwise.composeapp.generated.resources.difficulty_normal
import blockwise.composeapp.generated.resources.difficulty_hard
import blockwise.composeapp.generated.resources.difficulty_very_hard
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun RulesScreen(
    gridSize: GridSize,
    difficulty: Difficulty,
    onBack: () -> Unit,
    initialScroll: Int = 0,
    onScrollChanged: (Int) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val config = resolveGameConfig(gridSize, difficulty)
    val scrollState = rememberScrollState(initial = initialScroll)

    LaunchedEffect(scrollState) {
        snapshotFlow { scrollState.value }
            .distinctUntilChanged()
            .collectLatest(onScrollChanged)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                stringResource(Res.string.rules_title),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            Button(onClick = onBack) {
                Text(stringResource(Res.string.back))
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(stringResource(Res.string.rules_intro), style = MaterialTheme.typography.bodyMedium)
            Text(
                text = stringResource(
                    Res.string.rules_current_mode,
                    stringResource(Res.string.grid_size_option, gridSize.value),
                    when (difficulty) {
                        Difficulty.Easy -> stringResource(Res.string.difficulty_easy)
                        Difficulty.Normal -> stringResource(Res.string.difficulty_normal)
                        Difficulty.Hard -> stringResource(Res.string.difficulty_hard)
                        Difficulty.VeryHard -> stringResource(Res.string.difficulty_very_hard)
                    }
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            RuleSection(
                title = stringResource(Res.string.rules_rule_1_title),
                description = config.rules.maxSameColorPerRow?.let {
                    stringResource(Res.string.rules_rule_1_desc_enabled, it)
                } ?: stringResource(Res.string.rules_rule_1_desc_disabled),
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    PieceSample(Piece(shape = Shapes.Square2, color = BlockColor.Red))
                    PieceSample(Piece(shape = Shapes.Line3H, color = BlockColor.Red))
                    PieceSample(Piece(shape = Shapes.L3, color = BlockColor.Blue))
                }
            }

            RuleSection(
                title = stringResource(Res.string.rules_rule_2_title),
                description = config.rules.maxSameColorPerCol?.let {
                    stringResource(Res.string.rules_rule_2_desc_enabled, it)
                } ?: stringResource(Res.string.rules_rule_2_desc_disabled),
                content = null,
            )

            RuleSection(
                title = stringResource(Res.string.rules_piece_pool_title),
                description = stringResource(Res.string.rules_piece_pool_desc, config.maxShapeDimension),
                content = null,
            )

            RuleSection(
                title = stringResource(Res.string.rules_adjacent_limit_title),
                description = config.rules.maxAdjacentSameColor?.let {
                    stringResource(Res.string.rules_adjacent_limit_desc_enabled, it)
                } ?: stringResource(Res.string.rules_adjacent_limit_desc_disabled),
                content = null,
            )

            RuleSection(
                title = stringResource(Res.string.rules_variety_title),
                description = config.rules.minDistinctColorsInFullLine?.let {
                    stringResource(Res.string.rules_variety_desc_enabled, it)
                } ?: stringResource(Res.string.rules_variety_desc_disabled),
                content = null,
            )

            RuleSection(
                title = stringResource(Res.string.rules_move_limit_title),
                description = config.rules.moveLimit?.let {
                    stringResource(Res.string.rules_move_limit_desc_enabled, it)
                } ?: stringResource(Res.string.rules_move_limit_desc_disabled),
                content = null,
            )

            RuleSection(
                title = stringResource(Res.string.rules_prefilled_title),
                description = stringResource(
                    Res.string.rules_prefilled_desc,
                    (config.difficultyConfig.preFilledRatio * 100).toInt(),
                    (config.difficultyConfig.lockedCellsRatio * 100).toInt(),
                ),
                content = null,
            )

            RuleSection(
                title = stringResource(Res.string.rules_tips_title),
                description = stringResource(Res.string.rules_tips_desc),
                content = null,
            )
        }
    }
}

@Composable
private fun RuleSection(
    title: String,
    description: String,
    content: (@Composable () -> Unit)?,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        tonalElevation = 3.dp,
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(
                description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (content != null) {
                content()
            }
        }
    }
}

@Composable
private fun PieceSample(piece: Piece) {
    Box(modifier = Modifier.padding(4.dp)) {
        PiecePreviewSmall(piece)
    }
}

@Composable
private fun PiecePreviewSmall(piece: Piece) {
    val maxDx = piece.shape.cells.maxOf { it.dx }
    val maxDy = piece.shape.cells.maxOf { it.dy }
    val width = maxDx + 1
    val height = maxDy + 1

    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        for (y in 0 until height) {
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                for (x in 0 until width) {
                    val filled = piece.shape.cells.any { it.dx == x && it.dy == y }
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .background(
                                if (filled) piece.color.toPaletteColor() else MaterialTheme.colorScheme.surface
                            )
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RulesScreenPreview() {
    BlockWiseTheme {
        RulesScreen(
            gridSize = GridSize(12),
            difficulty = Difficulty.Hard,
            onBack = {},
        )
    }
}

