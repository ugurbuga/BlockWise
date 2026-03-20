package com.ugurbuga.blockwise

import android.content.Context
import android.content.res.Resources
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

internal actual object PlatformAppSettings {
    private const val PREFS_NAME = "blockwise_settings"

    private fun prefs() = AndroidAppContextHolder.appContext
        ?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    actual fun getString(key: String): String? {
        return prefs()?.getString(key, null)
    }

    actual fun putString(key: String, value: String) {
        prefs()?.edit()?.putString(key, value)?.apply()
    }

    actual fun getDeviceLanguageTag(): String? {
        return Resources.getSystem().configuration.locales[0]?.toLanguageTag()
    }

    actual fun applyLanguage(languageTag: String, refreshUi: Boolean) {
        val locales = LocaleListCompat.forLanguageTags(languageTag)
        AppCompatDelegate.setApplicationLocales(locales)

        if (refreshUi) {
            AndroidAppContextHolder.currentActivity?.recreate()
        }
    }
}

