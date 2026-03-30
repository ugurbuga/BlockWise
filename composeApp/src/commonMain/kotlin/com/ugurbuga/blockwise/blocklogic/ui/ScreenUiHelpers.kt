package com.ugurbuga.blockwise.blocklogic.ui

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ugurbuga.blockwise.blocklogic.domain.Difficulty
import com.ugurbuga.blockwise.blocklogic.domain.GameModeKey
import com.ugurbuga.blockwise.blocklogic.domain.PlayMode
import com.ugurbuga.blockwise.localizedStringResource as stringResource
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import blockwise.composeapp.generated.resources.Res
import blockwise.composeapp.generated.resources.difficulty_easy
import blockwise.composeapp.generated.resources.difficulty_hard
import blockwise.composeapp.generated.resources.difficulty_normal
import blockwise.composeapp.generated.resources.difficulty_very_hard
import blockwise.composeapp.generated.resources.game_mode_custom
import blockwise.composeapp.generated.resources.game_mode_quick_play
import blockwise.composeapp.generated.resources.game_mode_quick_play_no_rules
import blockwise.composeapp.generated.resources.grid_size_option

internal val ScreenContentMaxWidth: Dp = 760.dp

@Composable
internal fun rememberPersistedScrollState(
    initialScroll: Int,
    onScrollChanged: (Int) -> Unit,
): ScrollState {
    val scrollState = rememberScrollState(initial = initialScroll)

    LaunchedEffect(scrollState, onScrollChanged) {
        snapshotFlow { scrollState.value }
            .distinctUntilChanged()
            .collectLatest(onScrollChanged)
    }

    return scrollState
}

@Composable
internal fun difficultyLabel(difficulty: Difficulty): String {
    return when (difficulty) {
        Difficulty.Easy -> stringResource(Res.string.difficulty_easy)
        Difficulty.Normal -> stringResource(Res.string.difficulty_normal)
        Difficulty.Hard -> stringResource(Res.string.difficulty_hard)
        Difficulty.VeryHard -> stringResource(Res.string.difficulty_very_hard)
    }
}

@Composable
internal fun playModeLabel(playMode: PlayMode): String {
    return when (playMode) {
        PlayMode.QuickPlay -> stringResource(Res.string.game_mode_quick_play)
        PlayMode.Custom -> stringResource(Res.string.game_mode_custom)
    }
}

@Composable
internal fun gameModeSecondaryLabel(mode: GameModeKey): String {
    return when (mode.playMode) {
        PlayMode.QuickPlay -> buildString {
            append(stringResource(Res.string.grid_size_option, mode.gridSize.value))
            append(" • ")
            append(stringResource(Res.string.game_mode_quick_play_no_rules))
        }

        PlayMode.Custom -> buildString {
            append(stringResource(Res.string.grid_size_option, mode.gridSize.value))
            append(" • ")
            append(difficultyLabel(mode.difficulty))
        }
    }
}

@Composable
internal fun gameModeTableLabel(mode: GameModeKey): String {
    return when (mode.playMode) {
        PlayMode.QuickPlay -> playModeLabel(mode.playMode)
        PlayMode.Custom -> difficultyLabel(mode.difficulty)
    }
}

