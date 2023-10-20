package com.sorrowblue.comicviewer.feature.settings.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.sorrowblue.comicviewer.feature.settings.SettingsScreen
import com.sorrowblue.comicviewer.feature.settings.display.navigation.navigateToSettingsDisplay
import com.sorrowblue.comicviewer.feature.settings.display.navigation.settingsDisplayScreen
import com.sorrowblue.comicviewer.feature.settings.folder.navigation.navigateToSettingsFolder
import com.sorrowblue.comicviewer.feature.settings.folder.navigation.navigateToSettingsSupportExtension
import com.sorrowblue.comicviewer.feature.settings.folder.navigation.settingsFolderScreen
import com.sorrowblue.comicviewer.feature.settings.folder.navigation.settingsSupportExtensionScreen
import com.sorrowblue.comicviewer.feature.settings.info.navigation.navigateToSettingsAppInfo
import com.sorrowblue.comicviewer.feature.settings.info.navigation.settingsAppInfoScreen
import com.sorrowblue.comicviewer.feature.settings.language.InAppLanguagePickerScreen
import com.sorrowblue.comicviewer.feature.settings.security.navigation.navigateToSettingsSecurity
import com.sorrowblue.comicviewer.feature.settings.security.navigation.settingsSecurityScreen
import com.sorrowblue.comicviewer.feature.settings.viewer.navigation.navigateToSettingsViewer
import com.sorrowblue.comicviewer.feature.settings.viewer.navigation.settingsViewerScreen

private const val SettingsNavGraphRoute = "settings_group"
const val SettingsRoute = "settings"
private const val InAppLanguagePickerRoute = "settings/inapplanguagepicker"

private fun NavGraphBuilder.settingsScreen(
    onBackClick: () -> Unit,
    onDisplayClick: () -> Unit,
    onFolderClick: () -> Unit,
    onViewerClick: () -> Unit,
    onSecurityClick: () -> Unit,
    onAppInfoClick: () -> Unit,
    onAppLanguageClick: () -> Unit,
    onStartTutorialClick: () -> Unit
) {
    composable(SettingsRoute) {
        SettingsScreen(
            onBackClick = onBackClick,
            onDisplayClick = onDisplayClick,
            onFolderClick = onFolderClick,
            onViewerClick = onViewerClick,
            onSecurityClick = onSecurityClick,
            onAppInfoClick = onAppInfoClick,
            onAppLanguageClick = onAppLanguageClick,
            onStartTutorialClick = onStartTutorialClick
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
    onChangeAuthEnabled: (Boolean) -> Unit,
    onPasswordChangeClick: () -> Unit,
    onStartTutorialClick: () -> Unit,
) {
    navigation(route = SettingsNavGraphRoute, startDestination = SettingsRoute) {
        settingsScreen(
            onBackClick = navController::popBackStack,
            onDisplayClick = navController::navigateToSettingsDisplay,
            onFolderClick = navController::navigateToSettingsFolder,
            onViewerClick = navController::navigateToSettingsViewer,
            onSecurityClick = navController::navigateToSettingsSecurity,
            onAppInfoClick = navController::navigateToSettingsAppInfo,
            onAppLanguageClick = navController::navigateToInAppLanguagePickerScreen,
            onStartTutorialClick = onStartTutorialClick
        )
        settingsDisplayScreen(onBackClick = navController::popBackStack)
        settingsFolderScreen(
            onBackClick = navController::popBackStack,
            onExtensionClick = navController::navigateToSettingsSupportExtension
        )
        settingsSecurityScreen(
            onBackClick = navController::popBackStack,
            onChangeAuthEnabled = onChangeAuthEnabled,
            onPasswordChangeClick = onPasswordChangeClick
        )
        settingsViewerScreen(onBackClick = navController::popBackStack)
        settingsSupportExtensionScreen(onBackClick = navController::popBackStack)
        settingsAppInfoScreen(
            onBackClick = navController::popBackStack,
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
