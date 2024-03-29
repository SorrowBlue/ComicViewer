package com.sorrowblue.comicviewer.feature.library.googledrive.navigation

import androidx.navigation.NavController
import com.ramcosta.composedestinations.navigation.DependenciesContainerBuilder
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.navigation.popUpTo
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.library.googledrive.GoogleDriveLoginScreenNavigator
import com.sorrowblue.comicviewer.feature.library.googledrive.GoogleDriveScreenNavigator
import com.sorrowblue.comicviewer.feature.library.googledrive.destinations.GoogleDriveLoginScreenDestination
import com.sorrowblue.comicviewer.feature.library.googledrive.destinations.GoogleDriveScreenDestination
import com.sorrowblue.comicviewer.feature.library.googledrive.destinations.TypedDestination
import com.sorrowblue.comicviewer.feature.library.serviceloader.GoogleDriveNavGraph
import com.sorrowblue.comicviewer.framework.ui.TransitionsConfigure

internal object GoogleDriveNavGraphImpl : GoogleDriveNavGraph {
    override val route = "googledrive_graph"
    override val startRoute = GoogleDriveScreenDestination
    override val destinationsByRoute = listOf(
        GoogleDriveScreenDestination,
        GoogleDriveLoginScreenDestination
    ).associateBy(TypedDestination<*>::route)

    override val transitions = listOf(
        TransitionsConfigure(
            GoogleDriveScreenDestination.route,
            GoogleDriveScreenDestination.route,
            TransitionsConfigure.Type.SharedAxisX
        ),
        TransitionsConfigure(
            GoogleDriveScreenDestination.route,
            GoogleDriveLoginScreenDestination.route,
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
        dependency(GoogleDriveNavGraphImpl) {
            GoogleDriveNavGraphNavigator(navController)
        }
    }

    internal class ProviderImpl : GoogleDriveNavGraph.Provider {
        override fun get() = GoogleDriveNavGraphImpl
    }
}

private class GoogleDriveNavGraphNavigator(private val navController: NavController) :
    GoogleDriveScreenNavigator,
    GoogleDriveLoginScreenNavigator {

    override fun onComplete() {
        navController.navigate(GoogleDriveScreenDestination()) {
            popUpTo(GoogleDriveNavGraphImpl) {
                inclusive = true
            }
        }
    }

    override fun navigateUp() {
        navController.navigateUp()
    }

    override fun onFolderClick(folder: Folder) {
        navController.navigate(GoogleDriveScreenDestination(folder.path))
    }

    override fun requireAuthentication() {
        navController.navigate(GoogleDriveLoginScreenDestination) {
            popUpTo(GoogleDriveNavGraphImpl) {
                inclusive = true
            }
        }
    }
}
