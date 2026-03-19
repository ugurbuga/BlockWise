package com.ugurbuga.blockwise

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.MaterialTheme

import com.ugurbuga.blockwise.blocklogic.ui.BlockLogicScreen
import com.ugurbuga.blockwise.blocklogic.ui.LevelSelectionScreen
import com.ugurbuga.blockwise.blocklogic.ui.RulesScreen
import com.ugurbuga.blockwise.blocklogic.domain.Difficulty
import com.ugurbuga.blockwise.blocklogic.domain.GridSize
import com.ugurbuga.blockwise.ui.theme.BlockWiseTheme

private enum class MainScreen {
    LevelSelection,
    Game,
    Rules,
}

@Composable
@Preview
fun App() {
    BlockWiseTheme {
        var screen by remember { mutableStateOf(MainScreen.LevelSelection) }
        var selectedSize by remember { mutableStateOf(GridSize(10)) }
        var selectedDifficulty by remember { mutableStateOf(Difficulty.Normal) }
        var gameSessionKey by remember { mutableStateOf(0) }
        Box(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.surfaceVariant,
                        )
                    )
                )
                .safeContentPadding()
                .fillMaxSize(),
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                when (screen) {
                    MainScreen.LevelSelection -> {
                        LevelSelectionScreen(
                            modifier = Modifier.fillMaxSize(),
                            selectedSize = selectedSize,
                            selectedDifficulty = selectedDifficulty,
                            onSizeSelected = { selectedSize = it },
                            onDifficultySelected = { selectedDifficulty = it },
                            onOpenRules = { screen = MainScreen.Rules },
                            onPlay = {
                                gameSessionKey += 1
                                screen = MainScreen.Game
                            },
                        )
                    }

                    MainScreen.Game -> {
                        BlockLogicScreen(
                            modifier = Modifier.fillMaxSize(),
                            initialSize = selectedSize,
                            initialDifficulty = selectedDifficulty,
                            sessionKey = gameSessionKey.toString(),
                            onMenu = { screen = MainScreen.LevelSelection },
                        )
                    }

                    MainScreen.Rules -> {
                        RulesScreen(
                            modifier = Modifier.fillMaxSize(),
                            difficulty = selectedDifficulty,
                            onBack = { screen = MainScreen.LevelSelection },
                        )
                    }
                }
            }
        }
    }
}