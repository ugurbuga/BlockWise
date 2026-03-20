package com.ugurbuga.blockwise

import android.content.Context
import androidx.activity.ComponentActivity

internal object AndroidAppContextHolder {
    var appContext: Context? = null
    var currentActivity: ComponentActivity? = null
}

internal actual object PlatformScoreStorage {
    private const val PREFS_NAME = "blockwise_scores"

    actual fun getInt(key: String): Int? {
        val prefs = AndroidAppContextHolder.appContext?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            ?: return null
        return if (prefs.contains(key)) prefs.getInt(key, 0) else null
    }

    actual fun putInt(key: String, value: Int) {
        val prefs = AndroidAppContextHolder.appContext?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            ?: return
        prefs.edit().putInt(key, value).apply()
    }
}

