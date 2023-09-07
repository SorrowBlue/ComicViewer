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
import com.sorrowblue.comicviewer.settings.folder.navigation.navigateToSettingsFolder
import com.sorrowblue.comicviewer.settings.folder.navigation.navigateToSettingsSupportExtension
import com.sorrowblue.comicviewer.settings.folder.navigation.settingsFolderScreen
import com.sorrowblue.comicviewer.settings.folder.navigation.settingsSupportExtensionScreen
import com.sorrowblue.comicviewer.settings.language.InAppLanguagePickerScreen
import com.sorrowblue.comicviewer.settings.security.navigation.navigateToSettingsSecurity
import com.sorrowblue.comicviewer.settings.security.navigation.settingsSecurityScreen

private const val SettingsNavGraphRoute = "settings_group"
private const val SettingsRoute = "settings"
private const val InAppLanguagePickerRoute = "settings/inapplanguagepicker"

private fun NavGraphBuilder.settingsScreen(
    onBackClick: () -> Unit,
    onDisplayClick: () -> Unit,
    onFolderClick: () -> Unit,
    onSecurityClick: () -> Unit,
    onAppInfoClick: () -> Unit,
    onAppLanguageClick: () -> Unit
) {
    composable(SettingsRoute) {
        SettingsScreen(
            onBackClick = onBackClick,
            onDisplayClick = onDisplayClick,
            onFolderClick = onFolderClick,
            onSecurityClick = onSecurityClick,
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
            onFolderClick = navController::navigateToSettingsFolder,
            onSecurityClick = navController::navigateToSettingsSecurity,
            onAppInfoClick = navController::navigateToSettingsAppInfo,
            onAppLanguageClick = navController::navigateToInAppLanguagePickerScreen
        )
        settingsDisplayScreen(onBackClick = navController::popBackStack)
        settingsFolderScreen(
            onBackClick = navController::popBackStack,
            onExtensionClick = navController::navigateToSettingsSupportExtension
        )
        settingsSecurityScreen(onBackClick = navController::popBackStack)
        settingsSupportExtensionScreen(onBackClick = navController::popBackStack)
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
