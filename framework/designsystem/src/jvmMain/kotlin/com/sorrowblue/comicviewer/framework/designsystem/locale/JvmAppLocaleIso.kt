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
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import java.util.Locale as JavaLocale
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.getString

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
internal class JvmAppLocaleIso(private val localeHelper: LocaleHelper) : AppLocaleIso() {
    @Suppress("VariableNaming", "PrivatePropertyName")
    private val LocalAppLocaleIsoInternal =
        staticCompositionLocalOf { JavaLocale.getDefault().toString() }

    @Suppress("VarCouldBeVal")
    private var currentLanguageTag: String? by mutableStateOf(null)

    @Suppress("ConstantLocale")
    private val systemDefault = JavaLocale.getDefault()

    override val current: Locale?
        @Composable get() = if (currentLanguageTag.isNullOrEmpty()) {
            null
        } else {
            Locale(
                languageTag = requireNotNull(currentLanguageTag),
            )
        }

    override val locales: List<Locale>
        get() = runBlocking { getString(Res.string.locales) }.split(",").map {
            Locale(it)
        }

    override fun set(locale: Locale?) {
        localeHelper.save(locale?.platformLocale)
        currentLanguageTag = locale?.toLanguageTag()
        // Uiに反映させる
        appLanguageTag = locale?.toLanguageTag()
    }

    @Composable
    override infix fun provides(languageTag: String?): ProvidedValue<*> {
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
                Locale(languageTag = requireNotNull(currentLanguageTag))
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
