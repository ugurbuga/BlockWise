package com.ugurbuga.blockwise

internal enum class AppThemeMode(val storageValue: String) {
    System("system"),
    Light("light"),
    Dark("dark"),
    ;

    companion object {
        fun fromStorageValue(value: String?): AppThemeMode? {
            return entries.firstOrNull { it.storageValue == value }
        }
    }
}

internal val SelectableThemeModes = AppThemeMode.entries

internal object AppThemeModeStore {
    private const val THEME_MODE_KEY = "app_theme_mode"

    fun loadSelectedThemeMode(): AppThemeMode? {
        return AppThemeMode.fromStorageValue(PlatformAppSettings.getString(THEME_MODE_KEY))
    }

    fun saveSelectedThemeMode(themeMode: AppThemeMode) {
        PlatformAppSettings.putString(THEME_MODE_KEY, themeMode.storageValue)
    }
}

internal fun initializeAppThemeMode(): AppThemeMode {
    return AppThemeModeStore.loadSelectedThemeMode()
        ?: AppThemeMode.System.also(AppThemeModeStore::saveSelectedThemeMode)
}

