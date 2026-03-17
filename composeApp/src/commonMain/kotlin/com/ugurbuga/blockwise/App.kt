package com.ugurbuga.blockwise

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

import com.ugurbuga.blockwise.blocklogic.ui.BlockLogicScreen
import com.ugurbuga.blockwise.blocklogic.ui.LevelSelectionScreen
import com.ugurbuga.blockwise.blocklogic.ui.RulesScreen
import com.ugurbuga.blockwise.blocklogic.domain.Difficulty
import com.ugurbuga.blockwise.blocklogic.domain.GridSize

private enum class MainScreen {
    LevelSelection,
    Game,
    Rules,
}

@Composable
@Preview
fun App() {
    MaterialTheme {
        var screen by remember { mutableStateOf(MainScreen.LevelSelection) }
        var selectedSize by remember { mutableStateOf(GridSize(10)) }
        var selectedDifficulty by remember { mutableStateOf(Difficulty.Normal) }
        var gameSessionKey by remember { mutableStateOf(0) }
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .safeContentPadding()
                .fillMaxSize(),
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