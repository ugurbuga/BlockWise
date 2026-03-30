package com.ugurbuga.blockwise.blocklogic.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ugurbuga.blockwise.localizedStringResource as stringResource
import com.ugurbuga.blockwise.blocklogic.domain.Difficulty
import com.ugurbuga.blockwise.blocklogic.domain.GameModeKey
import com.ugurbuga.blockwise.blocklogic.domain.GridSize
import com.ugurbuga.blockwise.blocklogic.domain.allGameModes
import com.ugurbuga.blockwise.blocklogic.domain.customModeKey
import com.ugurbuga.blockwise.blocklogic.domain.quickPlayModeKey
import com.ugurbuga.blockwise.ui.theme.BlockWiseTheme
import blockwise.composeapp.generated.resources.Res
import blockwise.composeapp.generated.resources.back
import blockwise.composeapp.generated.resources.grid_size
import blockwise.composeapp.generated.resources.grid_size_option
import blockwise.composeapp.generated.resources.difficulty
import blockwise.composeapp.generated.resources.scores_empty
import blockwise.composeapp.generated.resources.scores_best_for_mode
import blockwise.composeapp.generated.resources.scores_title

internal fun formatBestScore(bestScore: Int?, emptyPlaceholder: String): String {
    return bestScore?.toString() ?: emptyPlaceholder
}

@Composable
fun ScoresScreen(
    bestScores: Map<GameModeKey, Int>,
    onBack: () -> Unit,
    initialScroll: Int = 0,
    onScrollChanged: (Int) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberPersistedScrollState(
        initialScroll = initialScroll,
        onScrollChanged = onScrollChanged,
    )

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
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(Res.string.scores_title),
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
                tonalElevation = 2.dp,
                color = MaterialTheme.colorScheme.surface,
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    ScoreTableHeader()

                    allGameModes().forEachIndexed { index, mode ->
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        ScoreTableRow(
                            mode = mode,
                            bestScore = bestScores[mode],
                            striped = index % 2 == 1,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ScoreTableHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ScoreTableCell(
            text = stringResource(Res.string.grid_size),
            weight = 1f,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
            maxLines = 2,
        )
        ScoreTableCell(
            text = stringResource(Res.string.difficulty),
            weight = 1f,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            maxLines = 2,
        )
        ScoreTableCell(
            text = stringResource(Res.string.scores_best_for_mode),
            weight = 1f,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.End,
            maxLines = 2,
        )
    }
}

@Composable
private fun ScoreTableRow(
    mode: GameModeKey,
    bestScore: Int?,
    striped: Boolean,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (striped) {
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.28f)
                } else {
                    Color.Transparent
                }
            ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ScoreTableCell(
                text = stringResource(Res.string.grid_size_option, mode.gridSize.value),
                weight = 1f,
                style = MaterialTheme.typography.bodyLarge,
            )
            ScoreTableCell(
                text = gameModeTableLabel(mode),
                weight = 1f,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            ScoreTableCell(
                text = formatBestScore(bestScore, stringResource(Res.string.scores_empty)),
                weight = 1f,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (bestScore != null) FontWeight.SemiBold else FontWeight.Normal,
                color = if (bestScore != null) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                textAlign = TextAlign.End,
            )
        }
    }
}

@Composable
private fun RowScope.ScoreTableCell(
    text: String,
    weight: Float,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.labelLarge,
    fontWeight: FontWeight? = null,
    color: Color = MaterialTheme.colorScheme.onSurface,
    textAlign: TextAlign = TextAlign.Start,
    maxLines: Int = 1,
) {
    Text(
        text = text,
        modifier = modifier
            .weight(weight)
            .width(0.dp),
        style = style,
        fontWeight = fontWeight,
        color = color,
        textAlign = textAlign,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
    )
}


@Preview(showBackground = true)
@Composable
private fun ScoresScreenPreview() {
    BlockWiseTheme {
        ScoresScreen(
            bestScores = mapOf(
                quickPlayModeKey() to 24,
                customModeKey(GridSize(14), Difficulty.VeryHard) to 91,
            ),
            onBack = {},
        )
    }
}

