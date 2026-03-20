package com.ugurbuga.blockwise

import java.util.prefs.Preferences

internal actual object PlatformScoreStorage {
    private val prefs: Preferences = Preferences.userRoot().node("com.ugurbuga.blockwise.scores")

    actual fun getInt(key: String): Int? {
        return if (prefs.get(key, null) != null) prefs.getInt(key, 0) else null
    }

    actual fun putInt(key: String, value: Int) {
        prefs.putInt(key, value)
        prefs.flush()
    }
}

