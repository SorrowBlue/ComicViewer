package com.sorrowblue.comicviewer.feature.settings.navigation

import androidx.navigation.NavController
import com.ramcosta.composedestinations.navigation.DependenciesContainerBuilder
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.spec.DestinationSpec
import com.sorrowblue.comicviewer.feature.settings.SettingsScreenNavigator
import com.sorrowblue.comicviewer.feature.settings.destinations.SettingsScreenDestination
import com.sorrowblue.comicviewer.framework.ui.AnimatedNavGraphSpec
import com.sorrowblue.comicviewer.framework.ui.TransitionsConfigure

object SettingsNavGraph : AnimatedNavGraphSpec {
    override val route = "settings_graph"
    override val startRoute = SettingsScreenDestination
    override val destinationsByRoute = listOf(SettingsScreenDestination)
        .associateBy(DestinationSpec<*>::route)
    override val transitions = listOf(
        TransitionsConfigure(
            route,
            null,
            TransitionsConfigure.Type.ContainerTransform
        )
    )
}

interface SettingsNavGraphNavigator {
    fun onStartTutorialClick()
    fun onPasswordChange()
    fun navigateToChangeAuth(enabled: Boolean)
}

fun DependenciesContainerBuilder<*>.dependencySettingsNavGraph(navigator: SettingsNavGraphNavigator) {
    dependency(SettingsNavGraph) {
        SettingsNavGraphNavigatorImpl(navigator, navController)
    }
}

private class SettingsNavGraphNavigatorImpl(
    navigator: SettingsNavGraphNavigator,
    private val navController: NavController,
) : SettingsScreenNavigator,
    SettingsNavGraphNavigator by navigator {

    override fun navigateUp() {
        navController.navigateUp()
    }
}
