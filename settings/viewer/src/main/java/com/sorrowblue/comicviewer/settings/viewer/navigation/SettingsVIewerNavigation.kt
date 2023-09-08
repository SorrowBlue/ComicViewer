package com.sorrowblue.comicviewer.settings.viewer.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.sorrowblue.comicviewer.settings.viewer.SettingsViewerRoute

private const val SettingsViewerRoute = "settings/viewer"

fun NavGraphBuilder.settingsViewerScreen(onBackClick: () -> Unit) {
    composable(SettingsViewerRoute) {
        SettingsViewerRoute(
            onBackClick = onBackClick
        )
    }
}

fun NavController.navigateToSettingsViewer(navOptions: NavOptions? = null) {
    navigate(SettingsViewerRoute, navOptions)
}
