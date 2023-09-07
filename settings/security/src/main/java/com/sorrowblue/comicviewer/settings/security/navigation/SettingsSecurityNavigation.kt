package com.sorrowblue.comicviewer.settings.security.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.sorrowblue.comicviewer.settings.security.SettingsSecurityRoute

private const val SettingsSecurityRoute = "settings/security"

fun NavGraphBuilder.settingsSecurityScreen(onBackClick: () -> Unit) {
    composable(SettingsSecurityRoute) {
        SettingsSecurityRoute(
            onBackClick = onBackClick
        )
    }
}

fun NavController.navigateToSettingsSecurity(navOptions: NavOptions? = null) {
    navigate(SettingsSecurityRoute, navOptions)
}
