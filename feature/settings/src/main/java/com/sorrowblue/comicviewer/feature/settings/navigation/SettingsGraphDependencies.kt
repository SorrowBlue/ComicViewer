package com.sorrowblue.comicviewer.feature.settings.navigation

import androidx.compose.runtime.Composable
import com.ramcosta.composedestinations.navigation.DependenciesContainerBuilder
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.navigation.navGraph
import com.sorrowblue.comicviewer.feature.authentication.AuthenticationScreenNavigator
import com.sorrowblue.comicviewer.feature.settings.NavGraphs
import com.sorrowblue.comicviewer.feature.settings.SettingsScreenNavigator

@Composable
fun DependenciesContainerBuilder<*>.SettingsGraphDependencies(
    onStartTutorialClick: () -> Unit,
) {
    navGraph(NavGraphs.settings) {
        dependency(object : SettingsScreenNavigator, AuthenticationScreenNavigator {
            override fun onStartTutorialClick() = onStartTutorialClick()

            override fun onPasswordChange() {
//                destinationsNavigator.navigate(AuthenticationScreenDestination(Mode.Change))
            }

            override fun navigateToChangeAuth(enabled: Boolean) {
                if (enabled) {
//                    destinationsNavigator.navigate(AuthenticationScreenDestination(Mode.Register))
                } else {
//                    destinationsNavigator.navigate(AuthenticationScreenDestination(Mode.Erase))
                }
            }

            override fun navigateUp() {
                navController.navigateUp()
            }

            override fun onCompleted() {
                navController.popBackStack()
            }
        })
    }
}
