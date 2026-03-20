package com.ugurbuga.blockwise

import java.util.Locale
import java.util.prefs.Preferences

internal actual object PlatformAppSettings {
    private val prefs: Preferences = Preferences.userRoot().node("com.ugurbuga.blockwise.settings")

    actual fun getString(key: String): String? {
        return prefs.get(key, null)
    }

    actual fun putString(key: String, value: String) {
        prefs.put(key, value)
        prefs.flush()
    }

    actual fun getDeviceLanguageTag(): String? {
        return Locale.getDefault().toLanguageTag()
    }

    actual fun applyLanguage(languageTag: String, refreshUi: Boolean) {
        val locale = Locale.forLanguageTag(languageTag)
        Locale.setDefault(locale)
        Locale.setDefault(Locale.Category.DISPLAY, locale)
        Locale.setDefault(Locale.Category.FORMAT, locale)
    }
}

