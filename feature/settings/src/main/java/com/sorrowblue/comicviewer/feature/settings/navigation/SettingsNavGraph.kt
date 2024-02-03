package com.sorrowblue.comicviewer.feature.settings.navigation

import com.ramcosta.composedestinations.navigation.DependenciesContainerBuilder
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.spec.DestinationSpec
import com.sorrowblue.comicviewer.feature.authentication.AuthenticationScreenNavigator
import com.sorrowblue.comicviewer.feature.authentication.destinations.AuthenticationScreenDestination
import com.sorrowblue.comicviewer.feature.authentication.navigation.Mode
import com.sorrowblue.comicviewer.feature.settings.SettingsScreenNavigator
import com.sorrowblue.comicviewer.feature.settings.destinations.SettingsScreenDestination
import com.sorrowblue.comicviewer.framework.ui.AnimatedNavGraphSpec
import com.sorrowblue.comicviewer.framework.ui.TransitionsConfigure

object SettingsNavGraph : AnimatedNavGraphSpec {
    override val route = "settings_graph"
    override val startRoute = SettingsScreenDestination
    override val destinationsByRoute = listOf(
        SettingsScreenDestination,
        AuthenticationScreenDestination
    ).associateBy(DestinationSpec<*>::route)

    override val transitions = listOf(
        TransitionsConfigure(
            route,
            null,
            TransitionsConfigure.Type.ContainerTransform
        )
    )
}

fun DependenciesContainerBuilder<*>.dependencySettingsNavGraph(
    onStartTutorialClick: () -> Unit,
) {
    dependency(SettingsNavGraph) {
        object : SettingsScreenNavigator, AuthenticationScreenNavigator {
            override fun onStartTutorialClick() = onStartTutorialClick()

            override fun onPasswordChange() {
                navController.navigate(AuthenticationScreenDestination(Mode.Change))
            }

            override fun navigateToChangeAuth(enabled: Boolean) {
                if (enabled) {
                    navController.navigate(AuthenticationScreenDestination(Mode.Register))
                } else {
                    navController.navigate(AuthenticationScreenDestination(Mode.Erase))
                }
            }

            override fun navigateUp() {
                navController.navigateUp()
            }

            override fun onCompleted() {
                navController.popBackStack()
            }
        }
    }
}
