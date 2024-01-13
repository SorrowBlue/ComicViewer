package com.sorrowblue.comicviewer.feature.library.dropbox.navigation

import androidx.navigation.NavController
import com.ramcosta.composedestinations.navigation.DependenciesContainerBuilder
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.navigation.popUpTo
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.library.dropbox.DropBoxLoginScreenNavigator
import com.sorrowblue.comicviewer.feature.library.dropbox.DropBoxScreenNavigator
import com.sorrowblue.comicviewer.feature.library.dropbox.destinations.DropBoxLoginScreenDestination
import com.sorrowblue.comicviewer.feature.library.dropbox.destinations.DropBoxScreenDestination
import com.sorrowblue.comicviewer.feature.library.dropbox.destinations.TypedDestination
import com.sorrowblue.comicviewer.feature.library.serviceloader.DropBoxNavGraph
import com.sorrowblue.comicviewer.framework.ui.TransitionsConfigure

internal object DropBoxNavGraphImpl : DropBoxNavGraph {

    override val route = "dropbox_graph"

    override val startRoute = DropBoxScreenDestination

    override val destinationsByRoute = listOf(
        DropBoxScreenDestination,
        DropBoxLoginScreenDestination
    ).associateBy(TypedDestination<*>::route)

    override val transitions = listOf(
        TransitionsConfigure(
            DropBoxScreenDestination.route,
            DropBoxScreenDestination.route,
            TransitionsConfigure.Type.SharedAxisX
        ),
        TransitionsConfigure(
            DropBoxScreenDestination.route,
            DropBoxLoginScreenDestination.route,
            TransitionsConfigure.Type.SharedAxisY
        ),
        TransitionsConfigure(
            route,
            null,
            TransitionsConfigure.Type.SharedAxisX
        )
    )

    context(DependenciesContainerBuilder<*>)
    override fun dependency() {
        dependency(DropBoxNavGraphImpl) {
            DropBoxNavGraphNavigator(navController)
        }
    }

    class ProviderImpl : DropBoxNavGraph.Provider {
        override fun get() = DropBoxNavGraphImpl
    }
}

private class DropBoxNavGraphNavigator(private val navController: NavController) :
    DropBoxScreenNavigator,
    DropBoxLoginScreenNavigator {
    override fun onLoginCompleted() {
        navController.navigate(DropBoxScreenDestination()) {
            popUpTo(DropBoxNavGraphImpl) {
                inclusive = true
            }
        }
    }

    override fun navigateUp() {
        navController.navigateUp()
    }

    override fun onFolderClick(folder: Folder) {
        navController.navigate(DropBoxScreenDestination(folder.path))
    }

    override fun requireAuthentication() {
        navController.navigate(DropBoxLoginScreenDestination) {
            popUpTo(DropBoxNavGraphImpl) {
                inclusive = true
            }
        }
    }
}
