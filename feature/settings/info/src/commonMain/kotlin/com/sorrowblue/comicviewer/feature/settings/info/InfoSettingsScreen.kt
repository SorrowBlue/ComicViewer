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
import comicviewer.feature.settings.info.generated.resources.settings_info_label_tutorial
import comicviewer.feature.settings.info.generated.resources.settings_info_label_version
import comicviewer.feature.settings.info.generated.resources.settings_info_rate_app_summary
import comicviewer.feature.settings.info.generated.resources.settings_info_title
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun InfoSettingsScreen(
    uiState: InfoSettingsScreenUiState,
    onBackClick: () -> Unit,
    onTutorialClick: () -> Unit,
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
            title = { Text(stringResource(Res.string.settings_info_label_tutorial)) },
            onClick = onTutorialClick,
        )
        Setting(
            title = { Text(stringResource(Res.string.settings_info_label_license)) },
            onClick = onLicenceClick,
        )
        Setting(
            title = { Text(stringResource(Res.string.settings_info_label_rate)) },
            summary = { Text(stringResource(Res.string.settings_info_rate_app_summary)) },
            icon = { Icon(ComicIcons.Star, null) },
            onClick = onRateAppClick,
        )
        Setting(
            title = { Text(stringResource(Res.string.settings_info_label_version)) },
            summary = { Text(uiState.versionName) },
            onClick = {},
        )
        Setting(
            title = { Text(stringResource(Res.string.settings_info_label_build)) },
            summary = { Text(uiState.buildAt) },
            onClick = {},
        )
    }
}
