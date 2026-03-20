package com.ugurbuga.blockwise

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.material3.MaterialTheme

import com.ugurbuga.blockwise.blocklogic.ui.BlockLogicScreen
import com.ugurbuga.blockwise.blocklogic.ui.LevelSelectionScreen
import com.ugurbuga.blockwise.blocklogic.ui.RulesScreen
import com.ugurbuga.blockwise.blocklogic.ui.ScoresScreen
import com.ugurbuga.blockwise.blocklogic.domain.Difficulty
import com.ugurbuga.blockwise.blocklogic.domain.GameModeKey
import com.ugurbuga.blockwise.blocklogic.domain.GridSize
import com.ugurbuga.blockwise.ui.theme.BlockWiseTheme

internal enum class AppScreen {
    LevelSelection,
    Game,
    Rules,
    Scores,
}

internal fun pushScreen(backStack: List<AppScreen>, destination: AppScreen): List<AppScreen> {
    return if (backStack.lastOrNull() == destination) backStack else backStack + destination
}

internal fun popScreen(backStack: List<AppScreen>): List<AppScreen> {
    return if (backStack.size > 1) backStack.dropLast(1) else backStack
}

internal fun resetToRoot(root: AppScreen = AppScreen.LevelSelection): List<AppScreen> {
    return listOf(root)
}

internal fun screenStateKey(screen: AppScreen, gameSessionKey: Int): String {
    return when (screen) {
        AppScreen.LevelSelection -> AppScreen.LevelSelection.name
        AppScreen.Game -> "${AppScreen.Game.name}:$gameSessionKey"
        AppScreen.Rules -> AppScreen.Rules.name
        AppScreen.Scores -> AppScreen.Scores.name
    }
}

internal fun scrollStateKey(
    screen: AppScreen,
    selectedSize: GridSize,
    selectedDifficulty: Difficulty,
): String {
    return when (screen) {
        AppScreen.LevelSelection -> AppScreen.LevelSelection.name
        AppScreen.Game -> "${AppScreen.Game.name}:${selectedSize.value}:${selectedDifficulty.name}"
        AppScreen.Rules -> "${AppScreen.Rules.name}:${selectedSize.value}:${selectedDifficulty.name}"
        AppScreen.Scores -> AppScreen.Scores.name
    }
}

@Composable
@Preview
fun App() {
    BlockWiseTheme {
        var backStack by remember { mutableStateOf(resetToRoot()) }
        var selectedSize by remember { mutableStateOf(GridSize(10)) }
        var selectedDifficulty by remember { mutableStateOf(Difficulty.Normal) }
        var gameSessionKey by remember { mutableStateOf(0) }
        var appLanguage by rememberSaveable { mutableStateOf(initializeAppLanguage()) }
        val pageScrollOffsets = rememberSaveable(
            saver = mapSaver(
                save = { it.toMap() },
                restore = { restored ->
                    mutableStateMapOf<String, Int>().apply {
                        restored.forEach { (key, value) ->
                            put(key, value as Int)
                        }
                    }
                },
            )
        ) {
            mutableStateMapOf<String, Int>()
        }
        val saveableStateHolder = rememberSaveableStateHolder()
        val bestScores = remember {
            mutableStateMapOf<GameModeKey, Int>().apply {
                putAll(BestScoreStore.loadScores())
            }
        }
        val currentScreen = backStack.last()

        fun recordBestScore(size: GridSize, difficulty: Difficulty, score: Int) {
            val key = GameModeKey(size, difficulty)
            val previousBest = bestScores[key]
            if (BestScoreStore.shouldSaveBest(previousBest, score)) {
                bestScores[key] = score
                BestScoreStore.saveBestScore(key, score)
            }
        }

        PlatformBackHandler(enabled = backStack.size > 1) {
            backStack = popScreen(backStack)
        }

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
                CompositionLocalProvider(
                    LocalAppLanguage provides appLanguage,
                    LocalLayoutDirection provides if (appLanguage.isRtl) {
                        LayoutDirection.Rtl
                    } else {
                        LayoutDirection.Ltr
                    },
                ) {
                    key(appLanguage) {
                        saveableStateHolder.SaveableStateProvider(
                            key = screenStateKey(currentScreen, gameSessionKey),
                        ) {
                            when (currentScreen) {
                                AppScreen.LevelSelection -> {
                                    val levelSelectionScrollKey = scrollStateKey(
                                        AppScreen.LevelSelection,
                                        selectedSize,
                                        selectedDifficulty,
                                    )
                                    LevelSelectionScreen(
                                        modifier = Modifier.fillMaxSize(),
                                        selectedSize = selectedSize,
                                        selectedDifficulty = selectedDifficulty,
                                        selectedLanguage = appLanguage,
                                        onSizeSelected = { selectedSize = it },
                                        onDifficultySelected = { selectedDifficulty = it },
                                        onLanguageSelected = { language ->
                                            if (language != appLanguage) {
                                                AppLanguageStore.saveSelectedLanguage(language)
                                                AppLanguageStore.applyLanguage(language, refreshUi = true)
                                                appLanguage = language
                                            }
                                        },
                                        onOpenRules = {
                                            backStack = pushScreen(backStack, AppScreen.Rules)
                                        },
                                        onOpenScores = {
                                            backStack = pushScreen(backStack, AppScreen.Scores)
                                        },
                                        onPlay = {
                                            gameSessionKey += 1
                                            backStack = pushScreen(backStack, AppScreen.Game)
                                        },
                                        bestScoreForSelection = bestScores[GameModeKey(
                                            selectedSize,
                                            selectedDifficulty
                                        )],
                                        initialScroll = pageScrollOffsets[levelSelectionScrollKey]
                                            ?: 0,
                                        onScrollChanged = {
                                            pageScrollOffsets[levelSelectionScrollKey] = it
                                        },
                                    )
                                }

                                AppScreen.Game -> {
                                    BlockLogicScreen(
                                        modifier = Modifier.fillMaxSize(),
                                        initialSize = selectedSize,
                                        initialDifficulty = selectedDifficulty,
                                        sessionKey = gameSessionKey.toString(),
                                        onMenu = { backStack = resetToRoot() },
                                        onRecordScore = ::recordBestScore,
                                    )
                                }

                                AppScreen.Rules -> {
                                    val rulesScrollKey = scrollStateKey(
                                        AppScreen.Rules,
                                        selectedSize,
                                        selectedDifficulty,
                                    )
                                    RulesScreen(
                                        modifier = Modifier.fillMaxSize(),
                                        gridSize = selectedSize,
                                        difficulty = selectedDifficulty,
                                        onBack = { backStack = popScreen(backStack) },
                                        initialScroll = pageScrollOffsets[rulesScrollKey] ?: 0,
                                        onScrollChanged = {
                                            pageScrollOffsets[rulesScrollKey] = it
                                        },
                                    )
                                }

                                AppScreen.Scores -> {
                                    val scoresScrollKey = scrollStateKey(
                                        AppScreen.Scores,
                                        selectedSize,
                                        selectedDifficulty,
                                    )
                                    ScoresScreen(
                                        modifier = Modifier.fillMaxSize(),
                                        bestScores = bestScores,
                                        onBack = { backStack = popScreen(backStack) },
                                        initialScroll = pageScrollOffsets[scoresScrollKey] ?: 0,
                                        onScrollChanged = {
                                            pageScrollOffsets[scoresScrollKey] = it
                                        },
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}