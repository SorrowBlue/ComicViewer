package com.sorrowblue.comicviewer.settings

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

private const val SETTINGS_ROUTE = "settings"

fun NavGraphBuilder.settingsScreen() {
    composable(SETTINGS_ROUTE) {
        SettingsScreen()
    }
}


fun NavController.navigateToSettings(navOptions: NavOptions? = null) {
    navigate(SETTINGS_ROUTE, navOptions)
}
