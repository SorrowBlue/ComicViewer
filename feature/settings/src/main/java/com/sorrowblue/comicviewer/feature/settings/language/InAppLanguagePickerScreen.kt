package com.sorrowblue.comicviewer.feature.settings.language

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.os.LocaleListCompat
import com.sorrowblue.comicviewer.feature.settings.R
import com.sorrowblue.comicviewer.feature.settings.common.CheckedSetting
import com.sorrowblue.comicviewer.feature.settings.common.Setting
import com.sorrowblue.comicviewer.feature.settings.common.SettingsColumn
import com.sorrowblue.comicviewer.framework.ui.material3.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.material3.Scaffold
import com.sorrowblue.comicviewer.framework.ui.material3.TopAppBar
import com.sorrowblue.comicviewer.framework.ui.material3.TopAppBarDefaults
import com.sorrowblue.comicviewer.framework.ui.material3.pinnedScrollBehavior
import java.util.Locale
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList

enum class Language(val label: Int, val tag: String) {
    Default(R.string.settings_language_label_system_default, ""),
    Japanese(R.string.settings_language_label_japanese, Locale.JAPAN.toLanguageTag()),
    EnglishUs(R.string.settings_language_label_us, Locale.US.toLanguageTag()),
}

@Composable
internal fun InAppLanguagePickerRoute(
    onBackClick: () -> Unit,
    state: InAppLanguagePickerScreenState = rememberInAppLanguagePickerScreenState(),
) {
    val uiState = state.uiState
    InAppLanguagePickerScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onLanguageClick = state::onLanguageClick
    )
}

@Stable
internal class InAppLanguagePickerScreenState {
    var uiState by mutableStateOf(InAppLanguagePickerScreenUiState())
        private set

    fun onLanguageClick(language: Language) {
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(language.tag))
        uiState = uiState.copy(
            currentLanguage = AppCompatDelegate.getApplicationLocales().toLanguageTags()
        )
    }
}

@Composable
internal fun rememberInAppLanguagePickerScreenState() = remember {
    InAppLanguagePickerScreenState()
}

internal data class InAppLanguagePickerScreenUiState(
    val currentLanguage: String = AppCompatDelegate.getApplicationLocales().toLanguageTags(),
    val languages: PersistentList<Language> = Language.entries.toPersistentList(),
)

@Composable
private fun InAppLanguagePickerScreen(
    uiState: InAppLanguagePickerScreenUiState,
    onBackClick: () -> Unit,
    onLanguageClick: (Language) -> Unit,
) {
    val languages = uiState.languages
    val currentLanguage = uiState.currentLanguage
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
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
        SettingsColumn(
            contentPadding = contentPadding
        ) {
            languages.forEach {
                if (currentLanguage == it.tag) {
                    CheckedSetting(
                        title = it.label,
                        onClick = {}
                    )
                } else {
                    Setting(
                        title = it.label,
                        onClick = {
                            onLanguageClick(it)
                        }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewInAppLanguagePickerScreen() {
    PreviewTheme {
        InAppLanguagePickerScreen(InAppLanguagePickerScreenUiState(), {}, {})
    }
}
