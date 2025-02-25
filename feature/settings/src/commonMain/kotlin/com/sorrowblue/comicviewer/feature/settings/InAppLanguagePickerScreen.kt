package com.sorrowblue.comicviewer.feature.settings

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.sorrowblue.comicviewer.feature.settings.common.CheckedSetting
import com.sorrowblue.comicviewer.feature.settings.common.Setting
import com.sorrowblue.comicviewer.feature.settings.common.SettingsCategory
import com.sorrowblue.comicviewer.feature.settings.common.SettingsDetailNavigator
import com.sorrowblue.comicviewer.feature.settings.common.SettingsDetailPane
import com.sorrowblue.cmpdestinations.annotation.Destination
import comicviewer.feature.settings.generated.resources.Res
import comicviewer.feature.settings.generated.resources.settings_language_label_all_languages
import comicviewer.feature.settings.generated.resources.settings_language_label_japanese
import comicviewer.feature.settings.generated.resources.settings_language_label_system_default
import comicviewer.feature.settings.generated.resources.settings_language_label_us
import comicviewer.feature.settings.generated.resources.settings_language_title
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource
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
    val languages = remember { Language.entries }
    val localeManager = remember { LocaleManager() }
    SettingsDetailPane(
        title = { Text(text = stringResource(Res.string.settings_language_title)) },
        onBackClick = onBackClick,
    ) {
        val current = remember {
            localeManager.currentLocale()
        }
        if (current.isEmpty()) {
            CheckedSetting(
                title = stringResource(Res.string.settings_language_label_system_default),
                onClick = {}
            )
        } else {
            Setting(
                title = stringResource(Res.string.settings_language_label_system_default),
                onClick = { localeManager.setSystemDefault() }
            )
        }

        SettingsCategory(title = Res.string.settings_language_label_all_languages) {
            languages.forEach {
                if (current == it.tag) {
                    CheckedSetting(
                        title = stringResource(it.label),
                        onClick = {
                        }
                    )
                } else {
                    Setting(
                        title = stringResource(it.label),
                        onClick = {
                            localeManager.setLocale(it.tag)
                        }
                    )
                }
            }
        }
    }
}

private enum class Language(val label: StringResource, val tag: String) {
    JAPANESE(Res.string.settings_language_label_japanese, "ja"),
    ENGLISH_US(Res.string.settings_language_label_us, "us"),
}
