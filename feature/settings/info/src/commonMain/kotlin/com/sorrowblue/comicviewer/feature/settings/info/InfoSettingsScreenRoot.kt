package com.sorrowblue.comicviewer.feature.settings.info

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

@Composable
internal fun InfoSettingsScreenRoot(
    onBackClick: () -> Unit,
    onTutorialClick: () -> Unit,
    onLicenceClick: () -> Unit,
) {
    val state = rememberInfoSettingsScreenState()
    InfoSettingsScreen(
        uiState = state.uiState,
        onBackClick = onBackClick,
        onTutorialClick = onTutorialClick,
        onLicenceClick = onLicenceClick,
        onRateAppClick = state::launchReview,
        modifier = Modifier.testTag("InfoSettingsRoot"),
    )
}
