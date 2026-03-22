package com.ugurbuga.blockwise.blocklogic.ui

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ugurbuga.blockwise.blocklogic.domain.Difficulty
import com.ugurbuga.blockwise.localizedStringResource as stringResource
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import blockwise.composeapp.generated.resources.Res
import blockwise.composeapp.generated.resources.difficulty_easy
import blockwise.composeapp.generated.resources.difficulty_hard
import blockwise.composeapp.generated.resources.difficulty_normal
import blockwise.composeapp.generated.resources.difficulty_very_hard

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

