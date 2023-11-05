package com.sorrowblue.comicviewer.feature.settings.language

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.os.LocaleListCompat
import com.sorrowblue.comicviewer.feature.settings.R
import com.sorrowblue.comicviewer.feature.settings.common.CheckedSetting
import com.sorrowblue.comicviewer.feature.settings.common.Setting
import com.sorrowblue.comicviewer.feature.settings.common.SettingsCategory
import com.sorrowblue.comicviewer.feature.settings.common.SettingsColumn
import com.sorrowblue.comicviewer.framework.ui.material3.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.material3.Scaffold
import com.sorrowblue.comicviewer.framework.ui.material3.TopAppBar
import com.sorrowblue.comicviewer.framework.ui.material3.TopAppBarDefaults
import com.sorrowblue.comicviewer.framework.ui.material3.pinnedScrollBehavior
import java.util.Locale
import kotlinx.collections.immutable.toPersistentList

@Composable
fun InAppLanguagePickerScreen(onBackClick: () -> Unit = {}) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val languages = remember { Language.entries.toPersistentList() }
    Scaffold(
        topBar = {
            TopAppBar(
                title = R.string.settings_language_title,
                onBackClick = onBackClick,
                scrollBehavior = scrollBehavior
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { contentPadding ->
        val current = remember {
            AppCompatDelegate.getApplicationLocales().toLanguageTags()
        }
        SettingsColumn(
            contentPadding = contentPadding
        ) {
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
}

enum class Language(val label: Int, val tag: String) {
    JAPANESE(R.string.settings_language_label_japanese, Locale.JAPAN.toLanguageTag()),
    ENGLISH_US(R.string.settings_language_label_us, Locale.US.toLanguageTag()),
}

@Preview
@Composable
private fun PreviewInAppLanguagePickerScreen() {
    PreviewTheme {
        InAppLanguagePickerScreen({})
    }
}
