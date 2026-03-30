package com.ugurbuga.blockwise

import com.ugurbuga.blockwise.blocklogic.domain.Difficulty
import com.ugurbuga.blockwise.blocklogic.domain.GridSize
import com.ugurbuga.blockwise.blocklogic.domain.PlayMode
import com.ugurbuga.blockwise.blocklogic.domain.supportedGridSizes

internal object LevelSelectionStore {
    private const val SELECTED_PLAY_MODE_KEY = "selected_play_mode"
    private const val SELECTED_GRID_SIZE_KEY = "selected_grid_size"
    private const val SELECTED_DIFFICULTY_KEY = "selected_difficulty"

    fun loadSelectedPlayMode(): PlayMode? {
        val storedValue = PlatformAppSettings.getString(SELECTED_PLAY_MODE_KEY) ?: return null
        return PlayMode.entries.firstOrNull { it.name == storedValue }
    }

    fun saveSelectedPlayMode(playMode: PlayMode) {
        PlatformAppSettings.putString(SELECTED_PLAY_MODE_KEY, playMode.name)
    }

    fun loadSelectedGridSize(): GridSize? {
        val storedValue = PlatformAppSettings.getString(SELECTED_GRID_SIZE_KEY)?.toIntOrNull() ?: return null
        return supportedGridSizes().firstOrNull { it.value == storedValue }
    }

    fun saveSelectedGridSize(size: GridSize) {
        PlatformAppSettings.putString(SELECTED_GRID_SIZE_KEY, size.value.toString())
    }

    fun loadSelectedDifficulty(): Difficulty? {
        val storedValue = PlatformAppSettings.getString(SELECTED_DIFFICULTY_KEY) ?: return null
        return Difficulty.entries.firstOrNull { it.name == storedValue }
    }

    fun saveSelectedDifficulty(difficulty: Difficulty) {
        PlatformAppSettings.putString(SELECTED_DIFFICULTY_KEY, difficulty.name)
    }
}

internal fun initializeSelectedPlayMode(): PlayMode {
    return LevelSelectionStore.loadSelectedPlayMode()
        ?: PlayMode.QuickPlay.also(LevelSelectionStore::saveSelectedPlayMode)
}

internal fun initializeSelectedGridSize(): GridSize {
    return LevelSelectionStore.loadSelectedGridSize()
        ?: GridSize(10).also(LevelSelectionStore::saveSelectedGridSize)
}

internal fun initializeSelectedDifficulty(): Difficulty {
    return LevelSelectionStore.loadSelectedDifficulty()
        ?: Difficulty.Easy.also(LevelSelectionStore::saveSelectedDifficulty)
}

