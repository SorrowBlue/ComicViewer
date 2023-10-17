package com.sorrowblue.comicviewer.feature.settings.display.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.sorrowblue.comicviewer.feature.settings.display.SettingsDisplayRoute

private const val SettingsDisplayRoute = "settings/display"

fun NavGraphBuilder.settingsDisplayScreen(onBackClick: () -> Unit) {
    composable(SettingsDisplayRoute) {
        SettingsDisplayRoute(
            onBackClick = onBackClick,
        )
    }
}

fun NavController.navigateToSettingsDisplay(navOptions: NavOptions? = null) {
    navigate(SettingsDisplayRoute, navOptions)
}
