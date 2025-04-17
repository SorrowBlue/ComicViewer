package com.sorrowblue.comicviewer.feature.settings.info

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.sorrowblue.cmpdestinations.annotation.Destination
import com.sorrowblue.comicviewer.feature.settings.common.Setting
import com.sorrowblue.comicviewer.feature.settings.common.SettingsDetailNavigator
import com.sorrowblue.comicviewer.feature.settings.common.SettingsDetailPane
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import comicviewer.feature.settings.info.generated.resources.Res
import comicviewer.feature.settings.info.generated.resources.settings_info_label_build
import comicviewer.feature.settings.info.generated.resources.settings_info_label_license
import comicviewer.feature.settings.info.generated.resources.settings_info_label_rate
import comicviewer.feature.settings.info.generated.resources.settings_info_label_version
import comicviewer.feature.settings.info.generated.resources.settings_info_rate_app_summary
import comicviewer.feature.settings.info.generated.resources.settings_info_title
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Serializable
data object AppInfoSettings

internal interface AppInfoSettingsScreenNavigator : SettingsDetailNavigator {
    fun navigateToLicense()
}

@Destination<AppInfoSettings>
@Composable
internal fun AppInfoSettingsScreen(navigator: AppInfoSettingsScreenNavigator = koinInject()) {
    AppInfoSettingsScreen(
        onBackClick = navigator::navigateBack,
        onLicenceClick = navigator::navigateToLicense
    )
}

@Composable
private fun AppInfoSettingsScreen(
    onBackClick: () -> Unit,
    onLicenceClick: () -> Unit,
    state: AppInfoSettingsScreenState = rememberAppInfoSettingsScreenState(),
) {
    AppInfoSettingsScreen(
        state.uiState,
        onBackClick = onBackClick,
        onLicenceClick = onLicenceClick,
        onRateAppClick = state::launchReview,
    )
}

internal data class SettingsAppInfoScreenUiState(
    val versionName: String = "",
    val buildAt: String,
)

@Composable
private fun AppInfoSettingsScreen(
    uiState: SettingsAppInfoScreenUiState,
    onBackClick: () -> Unit,
    onLicenceClick: () -> Unit,
    onRateAppClick: () -> Unit,
) {
    SettingsDetailPane(
        title = { Text(text = stringResource(Res.string.settings_info_title)) },
        onBackClick = onBackClick,
    ) {
        Setting(
            title = stringResource(Res.string.settings_info_label_version),
            onClick = { },
            summary = uiState.versionName
        )
        Setting(
            title = stringResource(Res.string.settings_info_label_build),
            onClick = { },
            summary = uiState.buildAt
        )
        Setting(
            title = Res.string.settings_info_label_license,
            onClick = onLicenceClick,
        )
        Setting(
            title = Res.string.settings_info_label_rate,
            summary = Res.string.settings_info_rate_app_summary,
            onClick = onRateAppClick,
            icon = ComicIcons.Star
        )
    }
}
