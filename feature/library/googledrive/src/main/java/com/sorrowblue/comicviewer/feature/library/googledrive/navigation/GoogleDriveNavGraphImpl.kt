package com.sorrowblue.comicviewer.feature.library.googledrive.navigation

import androidx.navigation.NavController
import com.ramcosta.composedestinations.navigation.DependenciesContainerBuilder
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.navigation.popUpTo
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.feature.library.googledrive.GoogleDriveLoginScreenNavigator
import com.sorrowblue.comicviewer.feature.library.googledrive.GoogleDriveScreenNavigator
import com.sorrowblue.comicviewer.feature.library.googledrive.destinations.GoogleDriveLoginScreenDestination
import com.sorrowblue.comicviewer.feature.library.googledrive.destinations.GoogleDriveScreenDestination
import com.sorrowblue.comicviewer.feature.library.googledrive.destinations.TypedDestination
import com.sorrowblue.comicviewer.feature.library.serviceloader.GoogleDriveNavGraph

internal object GoogleDriveNavGraphImpl : GoogleDriveNavGraph {
    override val route = "googledrive_graph"
    override val startRoute = GoogleDriveScreenDestination
    override val destinationsByRoute = listOf(
        GoogleDriveScreenDestination,
        GoogleDriveLoginScreenDestination
    ).associateBy(TypedDestination<*>::route)

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

    override fun onFileClick(file: File) {
        navController.navigateUp()
    }

    override fun requireAuthentication() {
        navController.navigate(GoogleDriveLoginScreenDestination) {
            popUpTo(GoogleDriveNavGraphImpl) {
                inclusive = true
            }
        }
    }
}
