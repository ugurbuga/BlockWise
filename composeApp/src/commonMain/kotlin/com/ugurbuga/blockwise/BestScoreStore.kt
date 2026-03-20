package com.ugurbuga.blockwise

import com.ugurbuga.blockwise.blocklogic.domain.Difficulty
import com.ugurbuga.blockwise.blocklogic.domain.GameModeKey
import com.ugurbuga.blockwise.blocklogic.domain.GridSize
import com.ugurbuga.blockwise.blocklogic.domain.allGameModes

internal expect object PlatformScoreStorage {
    fun getInt(key: String): Int?
    fun putInt(key: String, value: Int)
}

internal object BestScoreStore {
    private const val KEY_PREFIX = "best_score"

    fun loadScores(): Map<GameModeKey, Int> {
        return buildMap {
            allGameModes().forEach { mode ->
                PlatformScoreStorage.getInt(mode.storageKey())?.let { score ->
                    put(mode, score)
                }
            }
        }
    }

    fun saveBestScore(mode: GameModeKey, score: Int) {
        PlatformScoreStorage.putInt(mode.storageKey(), score)
    }

    fun shouldSaveBest(previousBest: Int?, score: Int): Boolean {
        return previousBest == null || score > previousBest
    }

    internal fun keyFor(size: GridSize, difficulty: Difficulty): String {
        return GameModeKey(gridSize = size, difficulty = difficulty).storageKey()
    }

    private fun GameModeKey.storageKey(): String {
        return "$KEY_PREFIX:${gridSize.value}:${difficulty.name}"
    }
}

