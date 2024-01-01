package com.sorrowblue.comicviewer.feature.settings.language

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.core.os.LocaleListCompat
import com.ramcosta.composedestinations.annotation.Destination
import com.sorrowblue.comicviewer.feature.settings.R
import com.sorrowblue.comicviewer.feature.settings.common.CheckedSetting
import com.sorrowblue.comicviewer.feature.settings.common.Setting
import com.sorrowblue.comicviewer.feature.settings.common.SettingsCategory
import com.sorrowblue.comicviewer.feature.settings.common.SettingsDetailPane
import java.util.Locale
import kotlinx.collections.immutable.toPersistentList

@OptIn(ExperimentalMaterial3Api::class)
@Destination
@Composable
internal fun InAppLanguagePickerScreen(
    onBackClick: () -> Unit,
    contentPadding: PaddingValues,
) {
    val languages = remember { Language.entries.toPersistentList() }
    SettingsDetailPane(
        title = { Text(text = stringResource(id = R.string.settings_language_title)) },
        onBackClick = onBackClick,
        contentPadding = contentPadding
    ) {
        val current = remember {
            AppCompatDelegate.getApplicationLocales().toLanguageTags()
        }
        if (current.isEmpty()) {
            CheckedSetting(
                title = stringResource(id = R.string.settings_language_label_system_default),
                onClick = {}
            )
        } else {
            Setting(
                title = stringResource(id = R.string.settings_language_label_system_default),
                onClick = { AppCompatDelegate.setApplicationLocales(LocaleListCompat.getEmptyLocaleList()) }
            )
        }

        SettingsCategory(title = R.string.settings_language_label_all_languages) {
            languages.forEach {
                if (current == it.tag) {
                    CheckedSetting(
                        title = stringResource(id = it.label),
                        onClick = {
                        }
                    )
                } else {
                    Setting(
                        title = stringResource(id = it.label),
                        onClick = {
                            AppCompatDelegate.setApplicationLocales(
                                LocaleListCompat.forLanguageTags(it.tag)
                            )
                        }
                    )
                }
            }
        }
    }
}

private enum class Language(val label: Int, val tag: String) {
    JAPANESE(R.string.settings_language_label_japanese, Locale.JAPAN.toLanguageTag()),
    ENGLISH_US(R.string.settings_language_label_us, Locale.US.toLanguageTag()),
}
