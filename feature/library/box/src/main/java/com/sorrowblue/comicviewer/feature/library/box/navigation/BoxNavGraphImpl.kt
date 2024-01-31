package com.sorrowblue.comicviewer.feature.library.box.navigation

import androidx.navigation.NavController
import com.ramcosta.composedestinations.navigation.DependenciesContainerBuilder
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.navigation.popUpTo
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.library.box.BoxOauth2RouteNavigator
import com.sorrowblue.comicviewer.feature.library.box.BoxScreenNavigator
import com.sorrowblue.comicviewer.feature.library.box.destinations.BoxLoginScreenDestination
import com.sorrowblue.comicviewer.feature.library.box.destinations.BoxOauth2ScreenDestination
import com.sorrowblue.comicviewer.feature.library.box.destinations.BoxScreenDestination
import com.sorrowblue.comicviewer.feature.library.box.destinations.TypedDestination
import com.sorrowblue.comicviewer.feature.library.serviceloader.BoxNavGraph
import com.sorrowblue.comicviewer.framework.ui.TransitionsConfigure

internal object BoxNavGraphImpl : BoxNavGraph {

    override val route = "box_graph"

    override val startRoute = BoxScreenDestination

    override val destinationsByRoute = listOf(
        BoxScreenDestination,
        BoxLoginScreenDestination,
        BoxOauth2ScreenDestination
    ).associateBy(TypedDestination<*>::route)

    override val transitions = listOf(
        TransitionsConfigure(
            BoxScreenDestination.route,
            BoxScreenDestination.route,
            TransitionsConfigure.Type.SharedAxisX
        ),
        TransitionsConfigure(
            BoxScreenDestination.route,
            BoxLoginScreenDestination.route,
            TransitionsConfigure.Type.SharedAxisY
        ),
        TransitionsConfigure(
            BoxScreenDestination.route,
            BoxOauth2ScreenDestination.route,
            TransitionsConfigure.Type.FadeThrough
        ),
        TransitionsConfigure(
            route,
            null,
            TransitionsConfigure.Type.SharedAxisX
        ),
    )

    context(DependenciesContainerBuilder<*>)
    override fun dependency() {
        dependency(BoxNavGraphImpl) {
            BoxNavGraphNavigator(navController)
        }
    }

    internal class ProviderImpl : BoxNavGraph.Provider {
        override fun get() = BoxNavGraphImpl
    }
}

private class BoxNavGraphNavigator(private val navController: NavController) :
    BoxScreenNavigator,
    BoxOauth2RouteNavigator {

    override fun onComplete() {
        navController.navigate(BoxScreenDestination()) {
            popUpTo(BoxNavGraphImpl) {
                inclusive = true
            }
        }
    }

    override fun requireLogin() {
        navController.navigate(BoxLoginScreenDestination) {
            popUpTo(BoxNavGraphImpl) {
                inclusive = true
            }
        }
    }

    override fun onFolderClick(folder: Folder) {
        navController.navigate(BoxScreenDestination(folder.path))
    }

    override fun navigateUp() {
        navController.navigateUp()
    }
}
