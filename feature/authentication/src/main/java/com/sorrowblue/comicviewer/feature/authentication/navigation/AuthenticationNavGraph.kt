package com.sorrowblue.comicviewer.feature.authentication.navigation

import androidx.navigation.NavController
import com.ramcosta.composedestinations.scope.DestinationScopeWithNoDependencies
import com.ramcosta.composedestinations.spec.DestinationSpec
import com.sorrowblue.comicviewer.feature.authentication.AuthenticationScreenNavigator
import com.sorrowblue.comicviewer.feature.authentication.destinations.AuthenticationScreenDestination
import com.sorrowblue.comicviewer.framework.ui.AnimatedNavGraphSpec
import com.sorrowblue.comicviewer.framework.ui.TransitionsConfigure

enum class Mode {
    Register,
    Change,
    Erase,
    Authentication,
}

object AuthenticationNavGraph : AnimatedNavGraphSpec {
    override val route = "authentication_graph"
    override val startRoute = AuthenticationScreenDestination
    override val destinationsByRoute = listOf(
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

fun DestinationScopeWithNoDependencies<*>.authenticationNavGraphNavigator(navigator: AuthenticationNavGraphNavigator) =
    ReadLaterNavGraphNavigatorImpl(navigator, navController)

interface AuthenticationNavGraphNavigator {
    fun onBack()
    fun onCompleted()
}

class ReadLaterNavGraphNavigatorImpl internal constructor(
    navigator: AuthenticationNavGraphNavigator,
    private val navController: NavController,
) : AuthenticationScreenNavigator,
    AuthenticationNavGraphNavigator by navigator {

    override fun navigateUp() {
        navController.navigateUp()
    }
}
