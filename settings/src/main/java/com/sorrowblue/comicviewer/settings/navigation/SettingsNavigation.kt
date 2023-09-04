package com.sorrowblue.comicviewer.settings.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.sorrowblue.comicviewer.settings.SettingsScreen
import com.sorrowblue.comicviewer.settings.app.navigation.navigateToSettingsAppInfo
import com.sorrowblue.comicviewer.settings.app.navigation.settingsAppInfoScreen
import com.sorrowblue.comicviewer.settings.display.navigation.navigateToSettingsDisplay
import com.sorrowblue.comicviewer.settings.display.navigation.settingsDisplayScreen
import com.sorrowblue.comicviewer.settings.language.InAppLanguagePickerScreen

private const val SettingsNavGraphRoute = "settings_group"
private const val SettingsRoute = "settings"
private const val InAppLanguagePickerRoute = "settings/inapplanguagepicker"

private fun NavGraphBuilder.settingsScreen(
    onBackClick: () -> Unit,
    onDisplayClick: () -> Unit,
    onAppInfoClick: () -> Unit,
    onAppLanguageClick: () -> Unit
) {
    composable(SettingsRoute) {
        SettingsScreen(
            onBackClick = onBackClick,
            onDisplayClick = onDisplayClick,
            onAppInfoClick = onAppInfoClick,
            onAppLanguageClick = onAppLanguageClick
        )
    }
}

private fun NavGraphBuilder.inAppLanguagePickerScreen(onBackClick: () -> Unit) {
    composable(InAppLanguagePickerRoute) {
        InAppLanguagePickerScreen(
            onBackClick = onBackClick
        )
    }
}

fun NavGraphBuilder.settingsNavGraph(
    navController: NavController,
    onLicenceClick: () -> Unit,
    onRateAppClick: () -> Unit,
) {
    navigation(route = SettingsNavGraphRoute, startDestination = SettingsRoute) {
        settingsScreen(
            onBackClick = navController::popBackStack,
            onDisplayClick = navController::navigateToSettingsDisplay,
            onAppInfoClick = navController::navigateToSettingsAppInfo,
            onAppLanguageClick = navController::navigateToInAppLanguagePickerScreen
        )
        settingsDisplayScreen(onBackClick = navController::popBackStack)
        settingsAppInfoScreen(
            onBackClick = navController::popBackStack,
            onLicenceClick = onLicenceClick,
            onRateAppClick = onRateAppClick,
        )
        inAppLanguagePickerScreen(onBackClick = navController::popBackStack)
    }
}

fun NavController.navigateToSettings(navOptions: NavOptions? = null) {
    navigate(SettingsNavGraphRoute, navOptions)
}

fun NavController.navigateToInAppLanguagePickerScreen(navOptions: NavOptions? = null) {
    navigate(InAppLanguagePickerRoute, navOptions)
}
