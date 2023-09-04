package com.sorrowblue.comicviewer.settings.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.sorrowblue.comicviewer.settings.SettingsScreen
import com.sorrowblue.comicviewer.settings.app.navigation.navigateToSettingsAppInfo
import com.sorrowblue.comicviewer.settings.app.navigation.settingsAppInfoScreen
import com.sorrowblue.comicviewer.settings.language.InAppLanguagePickerScreen

private const val SettingsNavGraphRoute = "settings_group"
private const val SettingsRoute = "settings"
private const val InAppLanguagePickerRoute = "settings/inapplanguagepicker"

private fun NavGraphBuilder.settingsScreen(
    onAppInfoClick: () -> Unit,
    onBackClick: () -> Unit,
    onAppLanguageClick: () -> Unit
) {
    composable(SettingsRoute) {
        SettingsScreen(
            onBackClick = onBackClick,
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
            onAppInfoClick = navController::navigateToSettingsAppInfo,
            onBackClick = navController::popBackStack,
            onAppLanguageClick = navController::navigateToInAppLanguagePickerScreen
        )
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
