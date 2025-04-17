package com.sorrowblue.comicviewer.framework.designsystem.locale

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.intl.Locale
import platform.Foundation.NSLocale
import platform.Foundation.NSLocaleIdentifier
import platform.Foundation.NSUserDefaults
import platform.Foundation.languageIdentifier
import platform.Foundation.preferredLanguages

actual object LocalAppLocaleIso {

    private const val LANG_KEY = "AppleLanguages"

    private val default = NSLocale.preferredLanguages.first() as String

    private val LocalDefaultLanguage = staticCompositionLocalOf { default }

    /**
     * 現在の[Locale]。nullの場合はシステムデフォルト。
     */
    actual val current: Locale?
        @Composable get() = Locale(LocalDefaultLanguage.current)

    actual val locales: List<Locale>
        get() = NSLocale.preferredLanguages.filterIsInstance<String>().map(::Locale)

    @Composable
    actual infix fun provides(languageTag: String?): ProvidedValue<*> {
        val new = languageTag ?: default
        if (languageTag == null) {
            NSUserDefaults.standardUserDefaults.removeObjectForKey(LANG_KEY)
        } else {
            NSUserDefaults.standardUserDefaults.setObject(arrayListOf(new), LANG_KEY)
        }
        return LocalDefaultLanguage.provides(new)
    }

    /**
     * アプリの[Locale]を設定する。
     *
     * @param locale [Locale]。nullの場合はシステムデフォルト。
     */
    actual fun set(locale: Locale?) {
        val new = locale?.toLanguageTag()
        if (new == null) {
            NSUserDefaults.standardUserDefaults.removeObjectForKey(LANG_KEY)
        } else {
            NSUserDefaults.standardUserDefaults.setObject(arrayListOf(new), LANG_KEY)
        }
    }
}

/**
 * [Locale]の表示名
 */
actual val Locale.displayLanguageName: String
    get() = platformLocale.run { displayNameForKey(NSLocaleIdentifier, languageIdentifier) }
        .orEmpty()
