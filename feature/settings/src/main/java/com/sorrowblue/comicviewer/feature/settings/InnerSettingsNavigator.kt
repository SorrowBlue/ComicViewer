package com.sorrowblue.comicviewer.feature.settings

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.navigation.NavController
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.scope.DestinationScopeWithNoDependencies
import com.sorrowblue.comicviewer.feature.settings.common.SettingsDetailNavigator
import com.sorrowblue.comicviewer.feature.settings.common.SettingsExtraNavigator
import com.sorrowblue.comicviewer.feature.settings.folder.FolderSettingsScreenNavigator
import com.sorrowblue.comicviewer.feature.settings.folder.destinations.SupportExtensionScreenDestination
import com.sorrowblue.comicviewer.feature.settings.security.SecuritySettingsScreenNavigator

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
internal fun DestinationScopeWithNoDependencies<*>.innerSettingsNavigator(
    scaffoldNavigator: ThreePaneScaffoldNavigator<Unit>,
    settingsScreenNavigator: SettingsScreenNavigator,
) = InnerSettingsNavigator(
    scaffoldNavigator = scaffoldNavigator,
    navController = navController,
    settingsScreenNavigator = settingsScreenNavigator
)

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
internal class InnerSettingsNavigator(
    private val scaffoldNavigator: ThreePaneScaffoldNavigator<Unit>,
    private val settingsScreenNavigator: SettingsScreenNavigator,
    private val navController: NavController,
) : SecuritySettingsScreenNavigator,
    FolderSettingsScreenNavigator,
    SettingsDetailNavigator,
    SettingsExtraNavigator {

    override fun navigateToChangeAuth(enabled: Boolean) {
        settingsScreenNavigator.navigateToChangeAuth(enabled)
    }

    override fun navigateToPasswordChange() {
        settingsScreenNavigator.onPasswordChange()
    }

    override fun navigateToExtension() {
        navController.navigate(SupportExtensionScreenDestination)
    }

    override fun navigateBack() {
        scaffoldNavigator.navigateBack()
    }

    override fun navigateUp() {
        navController.navigateUp()
    }
}
