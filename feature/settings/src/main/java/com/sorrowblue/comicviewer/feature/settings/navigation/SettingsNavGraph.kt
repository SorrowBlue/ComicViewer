package com.sorrowblue.comicviewer.feature.settings.navigation

import androidx.navigation.NavController
import com.ramcosta.composedestinations.scope.DestinationScopeWithNoDependencies
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

fun DestinationScopeWithNoDependencies<*>.settingsNavGraphNavigator(navigator: SettingsNavGraphNavigator) =
    SettingsNavGraphNavigatorImpl(navigator, navController)

interface SettingsNavGraphNavigator {
    fun onStartTutorialClick()
    fun onPasswordChange()
    fun navigateToChangeAuth(enabled: Boolean)
}

class SettingsNavGraphNavigatorImpl internal constructor(
    navigator: SettingsNavGraphNavigator,
    private val navController: NavController,
) : SettingsScreenNavigator,
    SettingsNavGraphNavigator by navigator {

    override fun navigateUp() {
        navController.navigateUp()
    }
}
