package com.sorrowblue.comicviewer.feature.settings

import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import platform.Foundation.localeIdentifier

actual class LocaleManager actual constructor() {
    actual fun currentLocale(): String {
        return NSLocale.currentLocale.localeIdentifier
    }

    actual fun setSystemDefault() {
        // TODO
    }

    actual fun setLocale(tag: String) {
        // TODO
    }
}
