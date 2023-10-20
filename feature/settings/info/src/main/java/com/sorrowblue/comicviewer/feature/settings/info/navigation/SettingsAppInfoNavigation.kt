package com.sorrowblue.comicviewer.feature.settings.info.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.sorrowblue.comicviewer.feature.settings.info.SettingsAppInfoRoute

private const val SettingsAppInfoRoute = "settings/app_info"

fun NavGraphBuilder.settingsAppInfoScreen(
    onBackClick: () -> Unit,
) {
    composable(SettingsAppInfoRoute) {
        SettingsAppInfoRoute(
            onBackClick = onBackClick,
        )
    }
}

fun NavController.navigateToSettingsAppInfo(navOptions: NavOptions? = null) {
    navigate(SettingsAppInfoRoute, navOptions)
}
