package com.sorrowblue.comicviewer.framework.designsystem.locale

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.intl.Locale
import platform.Foundation.NSLocale
import platform.Foundation.NSLocaleIdentifier
import platform.Foundation.NSUserDefaults
import platform.Foundation.availableLocaleIdentifiers
import platform.Foundation.preferredLanguages

actual object LocalAppLocaleIso {
    private const val LANG_KEY = "AppleLanguages"
    private val default = NSLocale.preferredLanguages.first() as String
    private val LocalAppLocaleIso = staticCompositionLocalOf { default }
    actual val current: String
        @Composable get() = LocalAppLocaleIso.current

    @Composable
    actual infix fun provides(value: String?): ProvidedValue<*> {
        val new = value ?: default
        if (value == null) {
            NSUserDefaults.standardUserDefaults.removeObjectForKey(LANG_KEY)
        } else {
            NSUserDefaults.standardUserDefaults.setObject(arrayListOf(new), LANG_KEY)
        }
        return LocalAppLocaleIso.provides(new)
    }

    actual val locales: List<String>
        get() =
//            listOf(
            NSLocale.preferredLanguages.filterIsInstance<String>().map {
                NSLocale(localeIdentifier = it).displayNameForKey(NSLocaleIdentifier, it) as String
            }
//            NSLocale.preferredLanguages.first() as String
//        )
}

actual fun updateAppLocaleIso(value: String?) {
}