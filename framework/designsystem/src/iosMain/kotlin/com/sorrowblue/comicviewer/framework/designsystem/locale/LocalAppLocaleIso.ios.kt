package com.sorrowblue.comicviewer.framework.designsystem.locale

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.intl.Locale
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import platform.Foundation.NSLocale
import platform.Foundation.NSLocaleIdentifier
import platform.Foundation.NSUserDefaults
import platform.Foundation.languageIdentifier
import platform.Foundation.preferredLanguages

private const val LangKey = "AppleLanguages"

@SingleIn(AppScope::class)
@Inject
actual class AppLocaleIso {
    private val default = NSLocale.preferredLanguages.first() as String

    @Suppress("VariableNaming")
    private val LocalAppLocaleIsoInternal = staticCompositionLocalOf { default }

    /**
     * 現在の[Locale]。nullの場合はシステムデフォルト。
     */
    actual val current: Locale?
        @Composable get() = Locale(LocalAppLocaleIsoInternal.current)

    actual val locales: List<Locale>
        get() = NSLocale.preferredLanguages.filterIsInstance<String>().map(::Locale)

    @Composable
    actual infix fun provides(languageTag: String?): ProvidedValue<*> {
        val new = languageTag ?: default
        if (languageTag == null) {
            NSUserDefaults.standardUserDefaults.removeObjectForKey(LangKey)
        } else {
            NSUserDefaults.standardUserDefaults.setObject(arrayListOf(new), LangKey)
        }
        return LocalAppLocaleIsoInternal.provides(new)
    }

    /**
     * アプリの[Locale]を設定する。
     *
     * @param locale [Locale]。nullの場合はシステムデフォルト。
     */
    actual fun set(locale: Locale?) {
        val new = locale?.toLanguageTag()
        if (new == null) {
            NSUserDefaults.standardUserDefaults.removeObjectForKey(LangKey)
        } else {
            NSUserDefaults.standardUserDefaults.setObject(arrayListOf(new), LangKey)
        }
    }
}

/**
 * [Locale]の表示名
 */
actual val Locale.displayLanguageName: String
    get() = platformLocale
        .run { displayNameForKey(NSLocaleIdentifier, languageIdentifier) }
        .orEmpty()
