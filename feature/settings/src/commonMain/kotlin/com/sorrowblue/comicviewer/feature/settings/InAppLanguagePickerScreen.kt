package com.sorrowblue.comicviewer.feature.settings

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.sorrowblue.cmpdestinations.annotation.Destination
import com.sorrowblue.comicviewer.feature.settings.common.CheckedSetting
import com.sorrowblue.comicviewer.feature.settings.common.Setting
import com.sorrowblue.comicviewer.feature.settings.common.SettingsCategory
import com.sorrowblue.comicviewer.feature.settings.common.SettingsDetailNavigator
import com.sorrowblue.comicviewer.feature.settings.common.SettingsDetailPane
import com.sorrowblue.comicviewer.framework.designsystem.locale.LocalAppLocaleIso
import com.sorrowblue.comicviewer.framework.designsystem.locale.displayLanguageName
import comicviewer.feature.settings.generated.resources.Res
import comicviewer.feature.settings.generated.resources.settings_language_label_all_languages
import comicviewer.feature.settings.generated.resources.settings_language_label_system_default
import comicviewer.feature.settings.generated.resources.settings_language_title
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Serializable
data object InAppLanguagePicker

@Destination<InAppLanguagePicker>
@Composable
internal fun InAppLanguagePickerScreen(navigator: SettingsDetailNavigator = koinInject()) {
    InAppLanguagePickerScreen(onBackClick = navigator::navigateBack)
}

@Composable
private fun InAppLanguagePickerScreen(onBackClick: () -> Unit) {
    val locales = LocalAppLocaleIso.locales
    SettingsDetailPane(
        title = { Text(text = stringResource(Res.string.settings_language_title)) },
        onBackClick = onBackClick,
    ) {
        val currentLocale = LocalAppLocaleIso.current
        if (currentLocale == null) {
            CheckedSetting(
                title = stringResource(Res.string.settings_language_label_system_default),
                onClick = {}
            )
        } else {
            Setting(
                title = stringResource(Res.string.settings_language_label_system_default),
                onClick = { LocalAppLocaleIso.set(null) }
            )
        }

        SettingsCategory(title = Res.string.settings_language_label_all_languages) {
            locales.forEach { locale ->
                if (currentLocale?.toLanguageTag() == locale.toLanguageTag()) {
                    CheckedSetting(
                        title = locale.displayLanguageName,
                        onClick = {}
                    )
                } else {
                    Setting(
                        title = locale.displayLanguageName,
                        onClick = { LocalAppLocaleIso.set(locale) }
                    )
                }
            }
        }
    }
}
