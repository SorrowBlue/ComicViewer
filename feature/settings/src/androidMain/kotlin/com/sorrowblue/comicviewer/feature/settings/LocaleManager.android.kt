package com.sorrowblue.comicviewer.feature.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

actual class LocaleManager {
    actual fun currentLocale(): String = AppCompatDelegate.getApplicationLocales().toLanguageTags()

    actual fun setSystemDefault() {
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.getEmptyLocaleList())
    }

    actual fun setLocale(tag: String) {
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(tag))
    }
}
