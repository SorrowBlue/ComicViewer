package com.sorrowblue.comicviewer.feature.settings.info

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
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
internal fun AppInfoSettingsScreen(
    contentPadding: PaddingValues,
    navigator: AppInfoSettingsScreenNavigator,
) {
    AppInfoSettingsScreen(
        contentPadding = contentPadding,
        onBackClick = navigator::navigateBack,
        onLicenceClick = navigator::navigateToLicense
    )
}

@Composable
private fun AppInfoSettingsScreen(
    onBackClick: () -> Unit,
    onLicenceClick: () -> Unit,
    contentPadding: PaddingValues,
    state: AppInfoSettingsScreenState = rememberAppInfoSettingsScreenState(),
) {
    AppInfoSettingsScreen(
        state.uiState,
        onBackClick = onBackClick,
        onLicenceClick = onLicenceClick,
        onRateAppClick = state::launchReview,
        contentPadding = contentPadding
    )
}

internal data class SettingsAppInfoScreenUiState(
    val versionName: String = "",
    val buildAt: String,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppInfoSettingsScreen(
    uiState: SettingsAppInfoScreenUiState,
    onBackClick: () -> Unit,
    onLicenceClick: () -> Unit,
    onRateAppClick: () -> Unit,
    contentPadding: PaddingValues,
) {
    SettingsDetailPane(
        title = { Text(text = stringResource(id = R.string.settings_info_title)) },
        onBackClick = onBackClick,
        contentPadding = contentPadding
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
