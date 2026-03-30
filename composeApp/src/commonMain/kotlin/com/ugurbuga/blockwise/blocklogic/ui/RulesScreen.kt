package com.ugurbuga.blockwise.blocklogic.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ugurbuga.blockwise.LocalBlockGapSpacing
import com.ugurbuga.blockwise.localizedStringResource as stringResource
import com.ugurbuga.blockwise.blocklogic.domain.BlockColor
import com.ugurbuga.blockwise.blocklogic.domain.GameModeKey
import com.ugurbuga.blockwise.blocklogic.domain.Piece
import com.ugurbuga.blockwise.blocklogic.domain.customModeKey
import com.ugurbuga.blockwise.blocklogic.domain.Difficulty
import com.ugurbuga.blockwise.blocklogic.domain.GridSize
import com.ugurbuga.blockwise.blocklogic.domain.resolveGameConfig
import com.ugurbuga.blockwise.blocklogic.domain.Shapes
import com.ugurbuga.blockwise.ui.theme.BlockWiseTheme
import com.ugurbuga.blockwise.ui.theme.toPaletteColor

import blockwise.composeapp.generated.resources.Res
import blockwise.composeapp.generated.resources.back
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

@Composable
fun RulesScreen(
    gameMode: GameModeKey,
    onBack: () -> Unit,
    initialScroll: Int = 0,
    onScrollChanged: (Int) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val config = resolveGameConfig(gameMode)
    val scrollState = rememberPersistedScrollState(
        initialScroll = initialScroll,
        onScrollChanged = onScrollChanged,
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
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
                stringResource(Res.string.rules_title),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = ScreenContentMaxWidth)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = stringResource(Res.string.rules_intro),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = stringResource(
                    Res.string.rules_current_mode,
                    playModeLabel(gameMode.playMode),
                    gameModeSecondaryLabel(gameMode),
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
    val gap = LocalBlockGapSpacing.current.gapDp
    val maxDx = piece.shape.cells.maxOf { it.dx }
    val maxDy = piece.shape.cells.maxOf { it.dy }
    val width = maxDx + 1
    val height = maxDy + 1
    val cellSize = 14.dp

    Column(verticalArrangement = Arrangement.spacedBy(gap)) {
        for (y in 0 until height) {
            Row(horizontalArrangement = Arrangement.spacedBy(gap)) {
                for (x in 0 until width) {
                    val filled = piece.shape.cells.any { it.dx == x && it.dy == y }
                    BlockTile3D(
                        fillColor = if (filled) piece.color.toPaletteColor() else MaterialTheme.colorScheme.surfaceVariant,
                        borderColor = if (filled) {
                            piece.color.toPaletteColor().darken(0.35f)
                        } else {
                            MaterialTheme.colorScheme.outlineVariant
                        },
                        borderWidth = 1.dp,
                        cornerRadius = 4.dp,
                        recessed = !filled,
                        elevation = if (filled) 2.dp else 0.dp,
                        modifier = Modifier
                            .size(cellSize)
                    ) {}
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
            gameMode = customModeKey(GridSize(12), Difficulty.Hard),
            onBack = {},
        )
    }
}

