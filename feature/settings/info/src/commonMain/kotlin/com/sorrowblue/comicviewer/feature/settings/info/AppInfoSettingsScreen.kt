package com.sorrowblue.comicviewer.feature.settings.info

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sorrowblue.comicviewer.feature.settings.common.Setting
import com.sorrowblue.comicviewer.feature.settings.common.SettingsDetailPane
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import comicviewer.feature.settings.info.generated.resources.Res
import comicviewer.feature.settings.info.generated.resources.settings_info_label_build
import comicviewer.feature.settings.info.generated.resources.settings_info_label_license
import comicviewer.feature.settings.info.generated.resources.settings_info_label_rate
import comicviewer.feature.settings.info.generated.resources.settings_info_label_version
import comicviewer.feature.settings.info.generated.resources.settings_info_rate_app_summary
import comicviewer.feature.settings.info.generated.resources.settings_info_title
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun AppInfoSettingsScreen(
    uiState: SettingsAppInfoScreenUiState,
    onBackClick: () -> Unit,
    onLicenceClick: () -> Unit,
    onRateAppClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SettingsDetailPane(
        title = { Text(text = stringResource(Res.string.settings_info_title)) },
        onBackClick = onBackClick,
        modifier = modifier,
    ) {
        Setting(
            title = {
                Text(stringResource(Res.string.settings_info_label_version))
            },
            onClick = { },
            summary = {
                Text(uiState.versionName)
            },
        )
        Setting(
            title = {
                Text(stringResource(Res.string.settings_info_label_build))
            },
            onClick = { },
            summary = {
                Text(uiState.buildAt)
            },
        )
        Setting(
            title = {
                Text(stringResource(Res.string.settings_info_label_license))
            },
            onClick = onLicenceClick,
        )
        Setting(
            title = {
                Text(stringResource(Res.string.settings_info_label_rate))
            },
            summary = {
                Text(stringResource(Res.string.settings_info_rate_app_summary))
            },
            onClick = onRateAppClick,
            icon = {
                Icon(ComicIcons.Star, null)
            },
        )
    }
}
