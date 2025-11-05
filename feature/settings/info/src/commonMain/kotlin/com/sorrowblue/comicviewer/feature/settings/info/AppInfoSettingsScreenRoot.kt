package com.sorrowblue.comicviewer.feature.settings.info

import androidx.compose.runtime.Composable

@Composable
fun AppInfoSettingsScreenRoot(
    onBackClick: () -> Unit,
    onLicenceClick: () -> Unit,
) {
    val state = rememberAppInfoSettingsScreenState()
    AppInfoSettingsScreen(
        uiState = state.uiState,
        onBackClick = onBackClick,
        onRateAppClick = state::launchReview,
        onLicenceClick = onLicenceClick
    )
}
