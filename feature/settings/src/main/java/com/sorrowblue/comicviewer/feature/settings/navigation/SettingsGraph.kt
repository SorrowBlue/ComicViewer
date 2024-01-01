package com.sorrowblue.comicviewer.feature.settings.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.sorrowblue.comicviewer.feature.settings.display.navigation.settingsDisplayScreen
import com.sorrowblue.comicviewer.feature.settings.folder.navigation.navigateToSettingsSupportExtension
import com.sorrowblue.comicviewer.feature.settings.folder.navigation.settingsFolderScreen
import com.sorrowblue.comicviewer.feature.settings.folder.navigation.settingsSupportExtensionScreen
import com.sorrowblue.comicviewer.feature.settings.info.navigation.appInfoSettingsScreen
import com.sorrowblue.comicviewer.feature.settings.security.navigation.settingsSecurityScreen
import com.sorrowblue.comicviewer.feature.settings.viewer.navigation.viewerSettingsScreen

internal fun NavGraphBuilder.settingsGraph(
    navController: NavHostController,
    onBackClick: () -> Unit,
    contentPadding: PaddingValues,
    onChangeAuthEnabled: (Boolean) -> Unit,
    onPasswordChangeClick: () -> Unit,
) {
    settingsDisplayScreen(
        onBackClick = onBackClick,
        contentPadding = contentPadding
    )
    settingsFolderScreen(
        onBackClick = onBackClick,
        onExtensionClick = navController::navigateToSettingsSupportExtension,
        contentPadding = contentPadding
    )
    viewerSettingsScreen(
        onBackClick = onBackClick,
        contentPadding = contentPadding
    )
    inAppLanguagePickerScreen(
        onBackClick = onBackClick,
        contentPadding = contentPadding
    )
    settingsSecurityScreen(
        onBackClick = onBackClick,
        onChangeAuthEnabled = onChangeAuthEnabled,
        onPasswordChangeClick = onPasswordChangeClick,
        contentPadding = contentPadding
    )

    appInfoSettingsScreen(
        onBackClick = onBackClick,
        contentPadding = contentPadding
    )

    settingsSupportExtensionScreen(
        onBackClick = navController::navigateUp,
        contentPadding = contentPadding
    )
}
