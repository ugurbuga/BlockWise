package com.ugurbuga.blockwise

internal enum class AppLanguage(
    val storageValue: String,
    val languageTag: String,
    val endonym: String,
    val abbreviation: String,
    val isRtl: Boolean = false,
) {
    English(storageValue = "en", languageTag = "en", endonym = "English", abbreviation = "EN"),
    Turkish(storageValue = "tr", languageTag = "tr", endonym = "Türkçe", abbreviation = "TR"),
    Spanish(storageValue = "es", languageTag = "es", endonym = "Español", abbreviation = "ES"),
    French(storageValue = "fr", languageTag = "fr", endonym = "Français", abbreviation = "FR"),
    German(storageValue = "de", languageTag = "de", endonym = "Deutsch", abbreviation = "DE"),
    Russian(storageValue = "ru", languageTag = "ru", endonym = "Русский", abbreviation = "РУ"),
    Arabic(storageValue = "ar", languageTag = "ar", endonym = "العربية", abbreviation = "عر", isRtl = true),
    ;

    companion object {
        fun fromStorageValue(value: String?): AppLanguage? {
            return entries.firstOrNull { it.storageValue == value }
        }

        fun fromLanguageTag(languageTag: String?): AppLanguage {
            val normalized = languageTag
                ?.substringBefore('-')
                ?.substringBefore('_')
                ?.lowercase()

            return entries.firstOrNull { it.languageTag == normalized } ?: English
        }
    }
}

internal val SelectableAppLanguages = AppLanguage.entries

internal expect object PlatformAppSettings {
    fun getString(key: String): String?
    fun putString(key: String, value: String)
    fun getDeviceLanguageTag(): String?
    fun applyLanguage(languageTag: String, refreshUi: Boolean)
}

internal object AppLanguageStore {
    private const val LANGUAGE_KEY = "app_language"

    fun loadSelectedLanguage(): AppLanguage? {
        return AppLanguage.fromStorageValue(PlatformAppSettings.getString(LANGUAGE_KEY))
    }

    fun saveSelectedLanguage(language: AppLanguage) {
        PlatformAppSettings.putString(LANGUAGE_KEY, language.storageValue)
    }

    fun applyLanguage(language: AppLanguage, refreshUi: Boolean = false) {
        PlatformAppSettings.applyLanguage(language.languageTag, refreshUi)
    }
}


internal fun initializeAppLanguage(): AppLanguage {
    val selectedLanguage = AppLanguageStore.loadSelectedLanguage()
        ?: AppLanguage.fromLanguageTag(PlatformAppSettings.getDeviceLanguageTag())
            .also(AppLanguageStore::saveSelectedLanguage)

    AppLanguageStore.applyLanguage(selectedLanguage)
    return selectedLanguage
}

