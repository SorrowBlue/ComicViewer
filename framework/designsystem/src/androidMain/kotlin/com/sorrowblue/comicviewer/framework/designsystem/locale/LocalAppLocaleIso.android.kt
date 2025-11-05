package com.sorrowblue.comicviewer.framework.designsystem.locale

import android.app.LocaleConfig
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.intl.Locale
import androidx.core.os.LocaleListCompat
import com.sorrowblue.comicviewer.framework.common.PlatformContext
import comicviewer.framework.designsystem.generated.resources.Res
import comicviewer.framework.designsystem.generated.resources.locales
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.getString
import java.util.Locale as JavaLocale

@SingleIn(AppScope::class)
@Inject
actual class AppLocaleIso(private val context: PlatformContext) {

    private var currentLocale by mutableStateOf(resolveLocale())

    /**
     * アプリがサポートしている[Locale]のリスト
     */
    actual val locales: List<Locale>
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            LocaleConfig(context).supportedLocales?.run {
                List(size()) {
                    Locale(get(it).toLanguageTag())
                }
            }.orEmpty()
        } else {
            runBlocking { getString(Res.string.locales) }.split(",").map(::Locale)
        }

    /**
     * 現在の[JavaLocale]。nullの場合はシステムデフォルト。
     */
    actual val current: Locale?
        @Composable
        get() = currentLocale

    /**
     * アプリの[JavaLocale]を設定する。
     *
     * @param locale [JavaLocale]。nullの場合はシステムデフォルト。
     */
    actual fun set(locale: Locale?) {
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(locale?.toLanguageTag()))
        currentLocale = resolveLocale()
    }

    /**
     * [languageTag]を適用する。
     *
     * @param languageTag ETF BCP47準拠の言語タグ表現
     * @return
     */
    @Composable
    actual infix fun provides(languageTag: String?): ProvidedValue<*> =
        LocalConfiguration provides LocalConfiguration.current

    private fun resolveLocale() = AppCompatDelegate.getApplicationLocales()
        .let { if (it == LocaleListCompat.getEmptyLocaleList()) null else Locale(it.toLanguageTags()) }
}

/**
 * [Locale]の表示名
 */
actual val Locale.displayLanguageName: String
    get() = LocaleListCompat.forLanguageTags(toLanguageTag())[0]?.displayName.orEmpty()
