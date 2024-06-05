package com.sorrowblue.comicviewer.feature.settings.navigation

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.runtime.Composable
import com.ramcosta.composedestinations.navigation.DependenciesContainerBuilder
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.navigation.navGraph
import com.sorrowblue.comicviewer.feature.settings.NavGraphs
import com.sorrowblue.comicviewer.feature.settings.Settings2
import com.sorrowblue.comicviewer.feature.settings.SettingsScreenNavigator
import com.sorrowblue.comicviewer.feature.settings.common.SettingsDetailNavigator
import com.sorrowblue.comicviewer.feature.settings.common.SettingsExtraNavigator
import com.sorrowblue.comicviewer.feature.settings.folder.navigation.FolderSettingsGraphDependencies
import com.sorrowblue.comicviewer.feature.settings.info.navigation.AppInfoSettingsGraphDependencies
import com.sorrowblue.comicviewer.feature.settings.security.SecuritySettingsScreenNavigator

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
internal fun DependenciesContainerBuilder<*>.SettingsDetailGraphDependencies(
    scaffoldNavigator: ThreePaneScaffoldNavigator<Settings2>,
    settingsScreenNavigator: SettingsScreenNavigator,
) {
    navGraph(NavGraphs.settingsDetail) {
        dependency(object :
            SecuritySettingsScreenNavigator,
            SettingsDetailNavigator,
            SettingsExtraNavigator {

            override fun navigateToChangeAuth(enabled: Boolean) {
                settingsScreenNavigator.navigateToChangeAuth(enabled)
            }

            override fun navigateToPasswordChange() {
                settingsScreenNavigator.onPasswordChange()
            }

            override fun navigateBack() {
                scaffoldNavigator.navigateBack()
            }

            override fun navigateUp() {
                destinationsNavigator.navigateUp()
            }
        })
    }

    FolderSettingsGraphDependencies(
        navigateBack = scaffoldNavigator::navigateBack
    )
    AppInfoSettingsGraphDependencies(
        navigateBack = scaffoldNavigator::navigateBack
    )
}
