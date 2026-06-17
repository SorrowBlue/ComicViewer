package com.sorrowblue.comicviewer.feature.settings.inapp

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.tooling.preview.Preview
import com.github.skydoves.navgraph.annotations.NavDestination
import com.github.skydoves.navgraph.annotations.NavPreview
import com.sorrowblue.comicviewer.feature.settings.common.Setting
import com.sorrowblue.comicviewer.feature.settings.common.SettingsCategory
import com.sorrowblue.comicviewer.feature.settings.common.SettingsDetailPane
import com.sorrowblue.comicviewer.feature.settings.navigation.InAppLanguagePickerNavKey
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.locale.AppLocaleIso
import com.sorrowblue.comicviewer.framework.designsystem.locale.displayLanguageName
import com.sorrowblue.comicviewer.framework.designsystem.locale.fake
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import comicviewer.feature.settings.generated.resources.Res
import comicviewer.feature.settings.generated.resources.settings_language_label_all_languages
import comicviewer.feature.settings.generated.resources.settings_language_label_system_default
import comicviewer.feature.settings.generated.resources.settings_language_title
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.compose.resources.stringResource

@NavDestination(InAppLanguagePickerNavKey::class)
@Composable
internal fun InAppLanguagePickerScreen(
    appLocaleIso: AppLocaleIso,
    localeList: ImmutableList<Locale>,
    onBackClick: () -> Unit,
) {
    SettingsDetailPane(
        title = { Text(text = stringResource(Res.string.settings_language_title)) },
        onBackClick = onBackClick,
    ) {
        val currentLocale = appLocaleIso.current
        Setting(
            title = {
                Text(stringResource(Res.string.settings_language_label_system_default))
            },
            onClick = {
                appLocaleIso.set(null)
            },
            widget = {
                if (currentLocale == null) {
                    Icon(imageVector = ComicIcons.Check, contentDescription = null)
                }
            },
        )

        SettingsCategory(title = {
            Text(stringResource(Res.string.settings_language_label_all_languages))
        }) {
            localeList.forEach { locale ->
                Setting(
                    title = {
                        Text(locale.displayLanguageName)
                    },
                    onClick = {
                        appLocaleIso.set(locale)
                    },
                    widget = {
                        if (currentLocale?.toLanguageTag() == locale.toLanguageTag()) {
                            Icon(
                                imageVector = ComicIcons.Check,
                                contentDescription = null,
                            )
                        }
                    },
                )
            }
        }
    }
}

@NavPreview(InAppLanguagePickerNavKey::class)
@Preview
@Composable
internal fun InAppLanguagePickerScreenPreview() {
    PreviewTheme {
        InAppLanguagePickerScreen(
            appLocaleIso = fake,
            localeList = listOf(
                Locale("en"),
                Locale("ja"),
                Locale("fr"),
            ).toImmutableList(),
            onBackClick = {},
        )
    }
}
