package com.sorrowblue.comicviewer.feature.settings

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.intl.Locale
import com.sorrowblue.comicviewer.feature.settings.common.CheckedSetting
import com.sorrowblue.comicviewer.feature.settings.common.Setting
import com.sorrowblue.comicviewer.feature.settings.common.SettingsCategory
import com.sorrowblue.comicviewer.feature.settings.common.SettingsDetailPane
import com.sorrowblue.comicviewer.framework.designsystem.locale.displayLanguageName
import comicviewer.feature.settings.generated.resources.Res
import comicviewer.feature.settings.generated.resources.settings_language_label_all_languages
import comicviewer.feature.settings.generated.resources.settings_language_label_system_default
import comicviewer.feature.settings.generated.resources.settings_language_title
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.stringResource

@Composable
context(context: InAppLanguagePickerScreenContext)
internal fun InAppLanguagePickerScreen(
    localeList: ImmutableList<Locale>,
    onBackClick: () -> Unit,
) {
    SettingsDetailPane(
        title = { Text(text = stringResource(Res.string.settings_language_title)) },
        onBackClick = onBackClick,
    ) {
        val currentLocale = context.appLocaleIso.current
        if (currentLocale == null) {
            CheckedSetting(
                title = stringResource(Res.string.settings_language_label_system_default),
                onClick = {}
            )
        } else {
            Setting(
                title = stringResource(Res.string.settings_language_label_system_default),
                onClick = { context.appLocaleIso.set(null) }
            )
        }

        SettingsCategory(title = Res.string.settings_language_label_all_languages) {
            localeList.forEach { locale ->
                if (currentLocale?.toLanguageTag() == locale.toLanguageTag()) {
                    CheckedSetting(
                        title = locale.displayLanguageName,
                        onClick = {}
                    )
                } else {
                    Setting(
                        title = locale.displayLanguageName,
                        onClick = { context.appLocaleIso.set(locale) }
                    )
                }
            }
        }
    }
}
