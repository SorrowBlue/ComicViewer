package com.sorrowblue.comicviewer.feature.settings.folder.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.sorrowblue.comicviewer.feature.settings.folder.SettingsFolderRoute
import com.sorrowblue.comicviewer.feature.settings.folder.SupportExtensionRoute

private const val SettingsFolderRoute = "settings/folder"

fun NavGraphBuilder.settingsFolderScreen(onBackClick: () -> Unit, onExtensionClick: () -> Unit) {
    composable(SettingsFolderRoute) {
        SettingsFolderRoute(
            onBackClick = onBackClick,
            onExtensionClick = onExtensionClick,
        )
    }
}

fun NavController.navigateToSettingsFolder(navOptions: NavOptions? = null) {
    navigate(SettingsFolderRoute, navOptions)
}

private const val SettingsSupportExtensionRoute = "settings/folder/supportextension"

fun NavGraphBuilder.settingsSupportExtensionScreen(onBackClick: () -> Unit) {
    composable(SettingsSupportExtensionRoute) {
        SupportExtensionRoute(onBackClick = onBackClick)
    }
}

fun NavController.navigateToSettingsSupportExtension(navOptions: NavOptions? = null) {
    navigate(SettingsSupportExtensionRoute, navOptions)
}
