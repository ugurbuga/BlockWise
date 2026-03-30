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
import com.ugurbuga.blockwise.blocklogic.ui.SettingsScreen
import com.ugurbuga.blockwise.blocklogic.ui.ShapesPreviewScreen
import com.ugurbuga.blockwise.blocklogic.domain.GameModeKey
import com.ugurbuga.blockwise.blocklogic.domain.PlayMode
import com.ugurbuga.blockwise.blocklogic.domain.customModeKey
import com.ugurbuga.blockwise.blocklogic.domain.quickPlayModeKey
import com.ugurbuga.blockwise.navigation.AppRootComponent
import com.ugurbuga.blockwise.navigation.subscribeAsState
import com.ugurbuga.blockwise.ui.theme.BlockWiseTheme

internal enum class AppScreen {
    LevelSelection,
    Game,
    Rules,
    Scores,
    Settings,
    ShapesPreview,
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
        AppScreen.Settings -> AppScreen.Settings.name
        AppScreen.ShapesPreview -> AppScreen.ShapesPreview.name
    }
}

internal fun scrollStateKey(
    screen: AppScreen,
    selectedMode: GameModeKey,
): String {
    return when (screen) {
        AppScreen.LevelSelection -> AppScreen.LevelSelection.name
        AppScreen.Game -> "${AppScreen.Game.name}:${selectedMode.playMode.name}:${selectedMode.gridSize.value}:${selectedMode.difficulty.name}"
        AppScreen.Rules -> "${AppScreen.Rules.name}:${selectedMode.playMode.name}:${selectedMode.gridSize.value}:${selectedMode.difficulty.name}"
        AppScreen.Scores -> AppScreen.Scores.name
        AppScreen.Settings -> AppScreen.Settings.name
        AppScreen.ShapesPreview -> AppScreen.ShapesPreview.name
    }
}

@Composable
@Preview
fun App() {
    var appThemeMode by rememberSaveable { mutableStateOf(initializeAppThemeMode()) }
    var appColorPalette by rememberSaveable { mutableStateOf(initializeAppColorPalette()) }
    var blockColorPalette by rememberSaveable { mutableStateOf(initializeBlockColorPalette()) }
    var blockVisualStyle by rememberSaveable { mutableStateOf(initializeBlockVisualStyle()) }
    var boardBlockStyleMode by rememberSaveable { mutableStateOf(initializeBoardBlockStyleMode()) }
    var blockGapSpacing by rememberSaveable { mutableStateOf(initializeBlockGapSpacing()) }
    var neonPulseSpeed by rememberSaveable { mutableStateOf(initializeNeonPulseSpeed()) }
    var dragFingerOffsetLevel by rememberSaveable { mutableStateOf(initializeDragFingerOffsetLevel()) }
    var invalidPlacementFeedbackMode by rememberSaveable {
        mutableStateOf(initializeInvalidPlacementFeedbackMode())
    }

    BlockWiseTheme(themeMode = appThemeMode, colorPalette = appColorPalette) {
        val navigation = remember { AppRootComponent() }
        val childStack by navigation.childStack.subscribeAsState()
        var selectedPlayMode by remember { mutableStateOf(initializeSelectedPlayMode()) }
        var selectedCustomSize by remember { mutableStateOf(initializeSelectedGridSize()) }
        var selectedCustomDifficulty by remember { mutableStateOf(initializeSelectedDifficulty()) }
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
        val currentChild = childStack.active.instance
        val selectedGameMode = remember(selectedPlayMode, selectedCustomSize, selectedCustomDifficulty) {
            when (selectedPlayMode) {
                PlayMode.QuickPlay -> quickPlayModeKey()
                PlayMode.Custom -> customModeKey(
                    gridSize = selectedCustomSize,
                    difficulty = selectedCustomDifficulty,
                )
            }
        }

        fun recordBestScore(mode: GameModeKey, score: Int) {
            val previousBest = bestScores[mode]
            if (BestScoreStore.shouldSaveBest(previousBest, score)) {
                bestScores[mode] = score
                BestScoreStore.saveBestScore(mode, score)
            }
        }

        PlatformBackHandler(enabled = navigation.canPop) {
            navigation.onBack()
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
                    LocalBlockColorPalette provides blockColorPalette,
                    LocalBlockVisualStyle provides blockVisualStyle,
                    LocalBoardBlockStyleMode provides boardBlockStyleMode,
                    LocalBlockGapSpacing provides blockGapSpacing,
                    LocalNeonPulseSpeed provides neonPulseSpeed,
                    LocalDragFingerOffsetLevel provides dragFingerOffsetLevel,
                    LocalInvalidPlacementFeedbackMode provides invalidPlacementFeedbackMode,
                    LocalLayoutDirection provides if (appLanguage.isRtl) {
                        LayoutDirection.Rtl
                    } else {
                        LayoutDirection.Ltr
                    },
                ) {
                    key(appLanguage) {
                        val screenStateKey = when (currentChild) {
                            AppRootComponent.Child.LevelSelection -> screenStateKey(
                                AppScreen.LevelSelection,
                                gameSessionKey = 0,
                            )

                            is AppRootComponent.Child.Game -> screenStateKey(
                                AppScreen.Game,
                                gameSessionKey = currentChild.sessionKey,
                            )

                            AppRootComponent.Child.Rules -> screenStateKey(
                                AppScreen.Rules,
                                gameSessionKey = 0,
                            )

                            AppRootComponent.Child.Scores -> screenStateKey(
                                AppScreen.Scores,
                                gameSessionKey = 0,
                            )

                            AppRootComponent.Child.Settings -> screenStateKey(
                                AppScreen.Settings,
                                gameSessionKey = 0,
                            )

                            AppRootComponent.Child.ShapesPreview -> screenStateKey(
                                AppScreen.ShapesPreview,
                                gameSessionKey = 0,
                            )
                        }

                        saveableStateHolder.SaveableStateProvider(
                            key = screenStateKey,
                        ) {
                            when (currentChild) {
                                AppRootComponent.Child.LevelSelection -> {
                                    LevelSelectionScreen(
                                        modifier = Modifier.fillMaxSize(),
                                        selectedPlayMode = selectedPlayMode,
                                        customGridSize = selectedCustomSize,
                                        customDifficulty = selectedCustomDifficulty,
                                        onPreviewModeSelected = { playMode ->
                                            selectedPlayMode = playMode
                                            LevelSelectionStore.saveSelectedPlayMode(playMode)
                                        },
                                        onCustomGridSizeSelected = {
                                            selectedPlayMode = PlayMode.Custom
                                            LevelSelectionStore.saveSelectedPlayMode(PlayMode.Custom)
                                            selectedCustomSize = it
                                            LevelSelectionStore.saveSelectedGridSize(it)
                                        },
                                        onCustomDifficultySelected = {
                                            selectedPlayMode = PlayMode.Custom
                                            LevelSelectionStore.saveSelectedPlayMode(PlayMode.Custom)
                                            selectedCustomDifficulty = it
                                            LevelSelectionStore.saveSelectedDifficulty(it)
                                        },
                                        onOpenRules = {
                                            navigation.openRules()
                                        },
                                        onOpenScores = {
                                            navigation.openScores()
                                        },
                                        onOpenShapesPreview = {
                                            navigation.openShapesPreview()
                                        },
                                        onOpenSettings = {
                                            navigation.openSettings()
                                        },
                                        onPlayQuickPlay = {
                                            selectedPlayMode = PlayMode.QuickPlay
                                            LevelSelectionStore.saveSelectedPlayMode(PlayMode.QuickPlay)
                                            navigation.openGame()
                                        },
                                        onPlayCustom = {
                                            selectedPlayMode = PlayMode.Custom
                                            LevelSelectionStore.saveSelectedPlayMode(PlayMode.Custom)
                                            navigation.openGame()
                                        },
                                        bestScoreForSelection = bestScores[selectedGameMode],
                                        initialScroll = pageScrollOffsets[AppScreen.LevelSelection.name]
                                            ?: 0,
                                        onScrollChanged = {
                                            pageScrollOffsets[AppScreen.LevelSelection.name] = it
                                        },
                                    )
                                }

                                is AppRootComponent.Child.Game -> {
                                    BlockLogicScreen(
                                        modifier = Modifier.fillMaxSize(),
                                        initialMode = selectedGameMode,
                                        sessionKey = currentChild.sessionKey.toString(),
                                        onMenu = { navigation.returnToRoot() },
                                        onRecordScore = { mode, score ->
                                            recordBestScore(mode, score)
                                        },
                                    )
                                }

                                AppRootComponent.Child.Rules -> {
                                    val rulesScrollKey = scrollStateKey(
                                        AppScreen.Rules,
                                        selectedGameMode,
                                    )
                                    RulesScreen(
                                        modifier = Modifier.fillMaxSize(),
                                        gameMode = selectedGameMode,
                                        onBack = { navigation.onBack() },
                                        initialScroll = pageScrollOffsets[rulesScrollKey] ?: 0,
                                        onScrollChanged = {
                                            pageScrollOffsets[rulesScrollKey] = it
                                        },
                                    )
                                }

                                AppRootComponent.Child.Scores -> {
                                    val scoresScrollKey = scrollStateKey(AppScreen.Scores, selectedGameMode)
                                    ScoresScreen(
                                        modifier = Modifier.fillMaxSize(),
                                        bestScores = bestScores,
                                        onBack = { navigation.onBack() },
                                        initialScroll = pageScrollOffsets[scoresScrollKey] ?: 0,
                                        onScrollChanged = {
                                            pageScrollOffsets[scoresScrollKey] = it
                                        },
                                    )
                                }

                                AppRootComponent.Child.Settings -> {
                                    val settingsScrollKey = scrollStateKey(AppScreen.Settings, selectedGameMode)
                                    SettingsScreen(
                                        modifier = Modifier.fillMaxSize(),
                                        selectedLanguage = appLanguage,
                                        selectedThemeMode = appThemeMode,
                                        selectedThemeColorPalette = appColorPalette,
                                        selectedBlockColorPalette = blockColorPalette,
                                        selectedBlockVisualStyle = blockVisualStyle,
                                        selectedBoardBlockStyleMode = boardBlockStyleMode,
                                        selectedBlockGapSpacing = blockGapSpacing,
                                        selectedNeonPulseSpeed = neonPulseSpeed,
                                        selectedDragFingerOffsetLevel = dragFingerOffsetLevel,
                                        selectedInvalidPlacementFeedbackMode = invalidPlacementFeedbackMode,
                                        onLanguageSelected = { language ->
                                            if (language != appLanguage) {
                                                appLanguage = language
                                                AppLanguageStore.saveSelectedLanguage(language)
                                                AppLanguageStore.applyLanguage(language)
                                            }
                                        },
                                        onThemeModeSelected = { themeMode ->
                                            if (themeMode != appThemeMode) {
                                                AppThemeModeStore.saveSelectedThemeMode(themeMode)
                                                appThemeMode = themeMode
                                            }
                                        },
                                        onThemeColorPaletteSelected = { palette ->
                                            if (palette != appColorPalette) {
                                                AppColorPaletteStore.saveSelectedColorPalette(palette)
                                                appColorPalette = palette
                                            }
                                        },
                                        onBlockColorPaletteSelected = { palette ->
                                            if (palette != blockColorPalette) {
                                                BlockColorPaletteStore.saveSelectedBlockColorPalette(palette)
                                                blockColorPalette = palette
                                            }
                                        },
                                        onBlockVisualStyleSelected = { style ->
                                            if (style != blockVisualStyle) {
                                                BlockVisualStyleStore.saveSelectedBlockVisualStyle(style)
                                                blockVisualStyle = style
                                            }
                                        },
                                        onBoardBlockStyleModeSelected = { mode ->
                                            if (mode != boardBlockStyleMode) {
                                                BoardBlockStyleModeStore.saveSelectedBoardBlockStyleMode(mode)
                                                boardBlockStyleMode = mode
                                            }
                                        },
                                        onBlockGapSpacingSelected = { spacing ->
                                            if (spacing != blockGapSpacing) {
                                                BlockGapSpacingStore.saveSelectedBlockGapSpacing(spacing)
                                                blockGapSpacing = spacing
                                            }
                                        },
                                        onNeonPulseSpeedSelected = { speed ->
                                            if (speed != neonPulseSpeed) {
                                                NeonPulseSpeedStore.saveSelectedNeonPulseSpeed(speed)
                                                neonPulseSpeed = speed
                                            }
                                        },
                                        onDragFingerOffsetLevelSelected = { level ->
                                            if (level != dragFingerOffsetLevel) {
                                                DragFingerOffsetLevelStore.saveSelectedDragFingerOffsetLevel(level)
                                                dragFingerOffsetLevel = level
                                            }
                                        },
                                        onInvalidPlacementFeedbackModeSelected = { mode ->
                                            if (mode != invalidPlacementFeedbackMode) {
                                                InvalidPlacementFeedbackModeStore
                                                    .saveSelectedInvalidPlacementFeedbackMode(mode)
                                                invalidPlacementFeedbackMode = mode
                                            }
                                        },
                                        onBack = { navigation.onBack() },
                                        initialScroll = pageScrollOffsets[settingsScrollKey] ?: 0,
                                        onScrollChanged = {
                                            pageScrollOffsets[settingsScrollKey] = it
                                        },
                                    )
                                }

                                AppRootComponent.Child.ShapesPreview -> {
                                    ShapesPreviewScreen(
                                        onBack = { navigation.onBack() }
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