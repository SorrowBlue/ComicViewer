package com.sorrowblue.comicviewer.feature.settings.security.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import com.ramcosta.composedestinations.utils.composable
import com.sorrowblue.comicviewer.feature.settings.security.SecuritySettingsScreen
import com.sorrowblue.comicviewer.feature.settings.security.destinations.SecuritySettingsScreenDestination

fun NavGraphBuilder.settingsSecurityScreen(
    onBackClick: () -> Unit,
    onChangeAuthEnabled: (Boolean) -> Unit,
    onPasswordChangeClick: () -> Unit,
    contentPadding: PaddingValues,
) {
    composable(SecuritySettingsScreenDestination) {
        SecuritySettingsScreen(
            onBackClick = onBackClick,
            onChangeAuthEnabled = onChangeAuthEnabled,
            onPasswordChangeClick = onPasswordChangeClick,
            contentPadding = contentPadding
        )
    }
}
