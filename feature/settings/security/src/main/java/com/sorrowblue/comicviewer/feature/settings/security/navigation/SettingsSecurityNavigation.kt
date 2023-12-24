package com.sorrowblue.comicviewer.feature.settings.security.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sorrowblue.comicviewer.feature.settings.security.SettingsSecurityRoute

const val SettingsSecurityRoute = "settings/security"

fun NavGraphBuilder.settingsSecurityScreen(
    onBackClick: () -> Unit,
    onChangeAuthEnabled: (Boolean) -> Unit,
    onPasswordChangeClick: () -> Unit,
    contentPadding: PaddingValues,
) {
    composable(SettingsSecurityRoute) {
        SettingsSecurityRoute(
            onBackClick = onBackClick,
            onChangeAuthEnabled = onChangeAuthEnabled,
            onPasswordChangeClick = onPasswordChangeClick,
            contentPadding = contentPadding
        )
    }
}
