package com.sorrowblue.comicviewer.feature.settings.security.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.sorrowblue.comicviewer.feature.settings.security.SettingsSecurityRoute

private const val SettingsSecurityRoute = "settings/security"

fun NavGraphBuilder.settingsSecurityScreen(
    onBackClick: () -> Unit,
    onChangeAuthEnabled: (Boolean) -> Unit,
    onPasswordChangeClick: () -> Unit,
) {
    composable(SettingsSecurityRoute) {
        SettingsSecurityRoute(
            onBackClick = onBackClick,
            onChangeAuthEnabled = onChangeAuthEnabled,
            onPasswordChangeClick = onPasswordChangeClick
        )
    }
}

fun NavController.navigateToSettingsSecurity(navOptions: NavOptions? = null) {
    navigate(SettingsSecurityRoute, navOptions)
}
