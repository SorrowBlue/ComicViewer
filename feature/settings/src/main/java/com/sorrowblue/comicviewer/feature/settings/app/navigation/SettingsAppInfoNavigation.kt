package com.sorrowblue.comicviewer.feature.settings.app.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.sorrowblue.comicviewer.feature.settings.app.SettingsAppInfoRoute

private const val SettingsAppInfoRoute = "settings/app_info"

internal fun NavGraphBuilder.settingsAppInfoScreen(
    onBackClick: () -> Unit,
    onLicenceClick: () -> Unit,
    onRateAppClick: () -> Unit,
) {
    composable(SettingsAppInfoRoute) {
        SettingsAppInfoRoute(
            onBackClick = onBackClick,
            onLicenceClick = onLicenceClick,
            onRateAppClick = onRateAppClick,
        )
    }
}

internal fun NavController.navigateToSettingsAppInfo(navOptions: NavOptions? = null) {
    navigate(SettingsAppInfoRoute, navOptions)
}
