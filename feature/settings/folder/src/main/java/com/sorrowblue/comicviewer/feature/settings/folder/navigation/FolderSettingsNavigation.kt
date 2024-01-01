package com.sorrowblue.comicviewer.feature.settings.folder.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.utils.composable
import com.sorrowblue.comicviewer.feature.settings.folder.FolderSettingsScreen
import com.sorrowblue.comicviewer.feature.settings.folder.SupportExtensionScreen
import com.sorrowblue.comicviewer.feature.settings.folder.destinations.FolderSettingsScreenDestination
import com.sorrowblue.comicviewer.feature.settings.folder.destinations.SupportExtensionScreenDestination

fun NavGraphBuilder.settingsFolderScreen(
    onBackClick: () -> Unit,
    onExtensionClick: () -> Unit,
    contentPadding: PaddingValues,
) {
    composable(FolderSettingsScreenDestination) {
        FolderSettingsScreen(
            onBackClick = onBackClick,
            onExtensionClick = onExtensionClick,
            contentPadding = contentPadding
        )
    }
}

fun NavGraphBuilder.settingsSupportExtensionScreen(
    onBackClick: () -> Unit,
    contentPadding: PaddingValues,
) {
    composable(SupportExtensionScreenDestination) {
        SupportExtensionScreen(onBackClick = onBackClick, contentPadding = contentPadding)
    }
}

fun NavController.navigateToSettingsSupportExtension() {
    navigate(SupportExtensionScreenDestination)
}
