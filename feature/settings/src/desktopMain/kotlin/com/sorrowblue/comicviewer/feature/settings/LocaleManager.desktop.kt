package com.sorrowblue.comicviewer.feature.settings

import java.util.Locale

actual class LocaleManager actual constructor() {
    actual fun currentLocale(): String {
        return Locale.getDefault().toLanguageTag()
    }

    actual fun setSystemDefault() {
        Locale.setDefault(Locale.getDefault())
    }

    actual fun setLocale(tag: String) {
        Locale.setDefault(Locale.forLanguageTag(tag))
    }
}
