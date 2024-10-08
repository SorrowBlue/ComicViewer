package com.sorrowblue.comicviewer.feature.settings.info

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.parameters.CodeGenVisibility
import com.sorrowblue.comicviewer.feature.settings.common.Setting
import com.sorrowblue.comicviewer.feature.settings.common.SettingsDetailNavigator
import com.sorrowblue.comicviewer.feature.settings.common.SettingsDetailPane
import com.sorrowblue.comicviewer.feature.settings.info.navigation.AppInfoSettingsGraph
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons

internal interface AppInfoSettingsScreenNavigator : SettingsDetailNavigator {

    fun navigateToLicense()
}

@Destination<AppInfoSettingsGraph>(start = true, visibility = CodeGenVisibility.INTERNAL)
@Composable
internal fun AppInfoSettingsScreen(navigator: AppInfoSettingsScreenNavigator) {
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
        title = { Text(text = stringResource(id = R.string.settings_info_title)) },
        onBackClick = onBackClick,
    ) {
        Setting(
            title = stringResource(id = R.string.settings_info_label_version),
            onClick = { },
            summary = uiState.versionName
        )
        Setting(
            title = stringResource(id = R.string.settings_info_label_build),
            onClick = { },
            summary = uiState.buildAt
        )
        Setting(
            title = R.string.settings_info_label_license,
            onClick = onLicenceClick,
        )
        Setting(
            title = R.string.settings_info_label_rate,
            summary = R.string.settings_info_rate_app_summary,
            onClick = onRateAppClick,
            icon = ComicIcons.Star
        )
    }
}
