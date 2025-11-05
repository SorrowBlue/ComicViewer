package com.sorrowblue.comicviewer.framework.designsystem.locale

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.intl.Locale
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
actual class AppLocaleIso(private val localeHelper: LocaleHelper) {

    private val LocalAppLocaleIsoInternal =
        staticCompositionLocalOf { JavaLocale.getDefault().toString() }
    private var currentLanguageTag: String? by mutableStateOf(null)

    @Suppress("ConstantLocale")
    private val systemDefault = JavaLocale.getDefault()

    actual val current: Locale?
        @Composable get() = if (currentLanguageTag.isNullOrEmpty()) null else Locale(languageTag = currentLanguageTag!!)

    actual val locales: List<Locale>
        get() = runBlocking { getString(Res.string.locales) }.split(",").map {
            Locale(it)
        }

    actual fun set(locale: Locale?) {
        localeHelper.save(locale?.platformLocale)
        currentLanguageTag = locale?.toLanguageTag()
        // Uiに反映させる
        appLanguageTag = locale?.toLanguageTag()
    }

    @Composable
    actual infix fun provides(languageTag: String?): ProvidedValue<*> {
        val locale = if (languageTag == null) {
            // 初回またはシステムデフォルト
            if (currentLanguageTag == null) {
                // ファイルからロードしていないのでロード
                currentLanguageTag = localeHelper.load()?.toLanguageTag().orEmpty()
            }
            if (currentLanguageTag.isNullOrEmpty()) {
                // 保存したLocaleなし、デフォルト値を設定
                Locale(languageTag = systemDefault.toLanguageTag())
            } else {
                // 保存したLocaleあり
                Locale(languageTag = currentLanguageTag!!)
            }
        } else {
            // 引数のlanguageTagを使う
            currentLanguageTag = languageTag
            Locale(languageTag = languageTag)
        }

        JavaLocale.setDefault(locale.platformLocale)
        return LocalAppLocaleIsoInternal.provides(locale.toLanguageTag())
    }
}

actual val Locale.displayLanguageName: String
    get() = platformLocale.displayName
