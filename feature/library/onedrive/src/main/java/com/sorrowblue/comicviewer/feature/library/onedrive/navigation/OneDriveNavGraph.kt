package com.sorrowblue.comicviewer.feature.library.onedrive.navigation

import androidx.navigation.NavController
import com.ramcosta.composedestinations.navigation.DependenciesContainerBuilder
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.navigation.popUpTo
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.feature.library.onedrive.OneDriveLoginScreenNavigator
import com.sorrowblue.comicviewer.feature.library.onedrive.OneDriveScreenNavigator
import com.sorrowblue.comicviewer.feature.library.onedrive.destinations.OneDriveLoginScreenDestination
import com.sorrowblue.comicviewer.feature.library.onedrive.destinations.OneDriveScreenDestination
import com.sorrowblue.comicviewer.feature.library.onedrive.destinations.TypedDestination
import com.sorrowblue.comicviewer.feature.library.serviceloader.OneDriveNavGraph
import com.sorrowblue.comicviewer.framework.ui.TransitionsConfigure

class OneDriveArgs(
    val name: String? = null,
    val itemId: String? = null,
)

internal object OneDriveNavGraphImpl : OneDriveNavGraph {
    override val route = "onedrive_graph"
    override val startRoute = OneDriveScreenDestination
    override val destinationsByRoute = listOf(
        OneDriveScreenDestination,
        OneDriveLoginScreenDestination
    ).associateBy(TypedDestination<*>::route)

    override val transitions = listOf(
        TransitionsConfigure(
            OneDriveScreenDestination.route,
            OneDriveScreenDestination.route,
            TransitionsConfigure.Type.SharedAxisX
        ),
        TransitionsConfigure(
            OneDriveScreenDestination.route,
            OneDriveLoginScreenDestination.route,
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
        dependency(OneDriveNavGraphImpl) {
            OneDriveNavGraphNavigator(navController)
        }
    }

    internal class ProviderImpl : OneDriveNavGraph.Provider {
        override fun get() = OneDriveNavGraphImpl
    }
}

private class OneDriveNavGraphNavigator(private val navController: NavController) :
    OneDriveScreenNavigator,
    OneDriveLoginScreenNavigator {

    override fun onCompleted() {
        navController.navigate(OneDriveScreenDestination()) {
            popUpTo(OneDriveNavGraphImpl) {
                inclusive = true
            }
        }
    }

    override fun navigateUp() {
        navController.navigateUp()
    }

    override fun onFileClick(file: File) {
        navController.navigate(
            OneDriveScreenDestination(
                OneDriveArgs(
                    name = file.name,
                    itemId = file.path
                )
            )
        )
    }

    override fun requireAuthentication() {
        navController.navigate(OneDriveLoginScreenDestination) {
            popUpTo(OneDriveNavGraphImpl) {
                inclusive = true
            }
        }
    }
}
