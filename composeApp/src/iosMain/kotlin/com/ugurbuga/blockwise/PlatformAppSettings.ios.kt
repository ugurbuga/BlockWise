package com.ugurbuga.blockwise

import platform.Foundation.NSUserDefaults

internal actual object PlatformAppSettings {
    private const val APPLE_LANGUAGES_KEY = "AppleLanguages"
    private val defaults: NSUserDefaults
        get() = NSUserDefaults.standardUserDefaults

    actual fun getString(key: String): String? {
        return defaults.stringForKey(key)
    }

    actual fun putString(key: String, value: String) {
        defaults.setObject(value, forKey = key)
    }

    actual fun getDeviceLanguageTag(): String? {
        return defaults.stringForKey("AppleLocale")
    }

    actual fun applyLanguage(languageTag: String, refreshUi: Boolean) {
        defaults.setObject(listOf(languageTag), forKey = APPLE_LANGUAGES_KEY)
        defaults.synchronize()
    }
}

