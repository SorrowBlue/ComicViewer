package com.sorrowblue.comicviewer.feature.settings.info

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

@Composable
internal fun AppInfoSettingsScreenRoot(onBackClick: () -> Unit, onLicenceClick: () -> Unit) {
    val state = rememberAppInfoSettingsScreenState()
    AppInfoSettingsScreen(
        uiState = state.uiState,
        onBackClick = onBackClick,
        onRateAppClick = state::launchReview,
        onLicenceClick = onLicenceClick,
        modifier = Modifier.testTag("InfoSettingsRoot"),
    )
}
