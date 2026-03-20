package com.ugurbuga.blockwise.blocklogic.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
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
import com.ugurbuga.blockwise.ui.theme.BlockWiseTheme
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import blockwise.composeapp.generated.resources.Res
import blockwise.composeapp.generated.resources.back
import blockwise.composeapp.generated.resources.difficulty_easy
import blockwise.composeapp.generated.resources.difficulty_hard
import blockwise.composeapp.generated.resources.difficulty_normal
import blockwise.composeapp.generated.resources.difficulty_very_hard
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
    val scrollState = rememberScrollState(initial = initialScroll)

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
                text = stringResource(Res.string.scores_title),
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
        )
        ScoreTableCell(
            text = stringResource(Res.string.difficulty),
            weight = 1.2f,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
        )
        ScoreTableCell(
            text = stringResource(Res.string.scores_best_for_mode),
            weight = 1f,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.End,
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
                text = difficultyLabel(mode.difficulty),
                weight = 1.2f,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
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

@Preview(showBackground = true)
@Composable
private fun ScoresScreenPreview() {
    BlockWiseTheme {
        ScoresScreen(
            bestScores = mapOf(
                GameModeKey(GridSize(8), Difficulty.Easy) to 24,
                GameModeKey(GridSize(14), Difficulty.VeryHard) to 91,
            ),
            onBack = {},
        )
    }
}

