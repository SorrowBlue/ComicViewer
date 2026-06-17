package com.sorrowblue.comicviewer.framework.designsystem.locale

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.intl.Locale
import com.sorrowblue.comicviewer.framework.common.LocalPlatformContext
import com.sorrowblue.comicviewer.framework.common.require
import io.github.takahirom.rin.rememberRetained

abstract class AppLocaleIso {
    /**
     * 現在の[Locale]。nullの場合はシステムデフォルト。
     */
    abstract val current: Locale? @Composable get

    /**
     * アプリがサポートしている[Locale]のリスト
     */
    abstract val locales: List<Locale>

    /**
     * アプリの[Locale]を設定する。
     *
     * @param locale [Locale]。nullの場合はシステムデフォルト。
     */
    abstract fun set(locale: Locale?)

    /**
     * [languageTag]を適用する。
     *
     * @param languageTag ETF BCP47準拠の言語タグ表現
     * @return
     */
    @Composable
    internal abstract infix fun provides(languageTag: String?): ProvidedValue<*>
}

/**
 * [Locale]の表示名
 */
expect val Locale.displayLanguageName: String

val ProvideLocalAppLocaleIso: ProvidedValue<*>
    @Composable
    get() {
        val context = LocalPlatformContext.current
        val graph = rememberRetained {
            context.require<AppLocaleIsoContext.Factory>().createAppLocaleIsoContext()
        }
        return graph.appLocaleIso provides appLanguageTag
    }

val fake = object : AppLocaleIso() {
    private var _current: Locale? = null
    override val current: Locale?
        @Composable get() = _current
    override val locales: List<Locale> = listOf(
        Locale("en"),
        Locale("ja"),
        Locale("fr"),
    )

    override fun set(locale: Locale?) {
        _current = locale
    }

    private val LocalAppLocaleIsoInternal =
        staticCompositionLocalOf { "" }

    @Composable
    override infix fun provides(languageTag: String?): ProvidedValue<*> =
        LocalAppLocaleIsoInternal.provides(languageTag.orEmpty())
}

/**
 * アプリの現在のETF BCP47準拠の言語タグ。nullの場合はシステムデフォルト。
 */
internal var appLanguageTag by mutableStateOf<String?>(null)
