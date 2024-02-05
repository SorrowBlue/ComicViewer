package com.sorrowblue.comicviewer.feature.library.navigation

import com.ramcosta.composedestinations.navigation.DependenciesContainerBuilder
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.spec.DestinationSpec
import com.ramcosta.composedestinations.spec.NavGraphSpec
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.feature.history.HistoryScreenNavigator
import com.sorrowblue.comicviewer.feature.history.destinations.HistoryScreenDestination
import com.sorrowblue.comicviewer.feature.library.LibraryScreenNavigator
import com.sorrowblue.comicviewer.feature.library.destinations.LibraryScreenDestination
import com.sorrowblue.comicviewer.feature.library.section.Feature
import com.sorrowblue.comicviewer.feature.library.serviceloader.BoxNavGraph
import com.sorrowblue.comicviewer.feature.library.serviceloader.DropBoxNavGraph
import com.sorrowblue.comicviewer.feature.library.serviceloader.GoogleDriveNavGraph
import com.sorrowblue.comicviewer.feature.library.serviceloader.OneDriveNavGraph
import com.sorrowblue.comicviewer.framework.ui.AnimatedNavGraphSpec
import com.sorrowblue.comicviewer.framework.ui.TransitionsConfigure

object LibraryNavGraph : AnimatedNavGraphSpec {

    override val route = "library_graph"

    override val startRoute = LibraryScreenDestination

    override val destinationsByRoute = listOf(
        LibraryScreenDestination,
        HistoryScreenDestination
    ).associateBy(DestinationSpec<*>::route)

    override val transitions = listOf(
        TransitionsConfigure(
            LibraryScreenDestination.route,
            HistoryScreenDestination.route,
            TransitionsConfigure.Type.SharedAxisX
        ),
        TransitionsConfigure(
            LibraryScreenDestination.route,
            BoxNavGraph()?.startRoute?.route,
            TransitionsConfigure.Type.SharedAxisX
        ),
        TransitionsConfigure(
            route,
            null,
            TransitionsConfigure.Type.ContainerTransform
        )
    )

    override val nestedNavGraphs: List<NavGraphSpec>
        get() = listOfNotNull(
            BoxNavGraph(),
            DropBoxNavGraph(),
            GoogleDriveNavGraph(),
            OneDriveNavGraph()
        )
}

fun DependenciesContainerBuilder<*>.dependencyLibraryNavGraph(
    onSettingsClick: () -> Unit,
    navigateToBook: (Book) -> Unit,
    onFavoriteClick: (File) -> Unit,
) {
    dependency(LibraryNavGraph) {
        object :
            LibraryScreenNavigator,
            HistoryScreenNavigator {
            override fun onSettingsClick() = onSettingsClick()

            override fun navigateToBook(book: Book) = navigateToBook(book)

            override fun onFavoriteClick(file: File) = onFavoriteClick(file)

            override fun navigateUp() {
                navController.navigateUp()
            }

            override fun onFeatureClick(feature: Feature) {
                when (feature) {
                    is Feature.AddOn -> {
                        when (feature) {
                            is Feature.AddOn.Box -> BoxNavGraph()
                            is Feature.AddOn.Dropbox -> DropBoxNavGraph()
                            is Feature.AddOn.GoogleDrive -> GoogleDriveNavGraph()
                            is Feature.AddOn.OneDrive -> OneDriveNavGraph()
                        }?.let {
                            navController.navigate(it)
                        }
                    }

                    Feature.Basic.History -> navController.navigate(HistoryScreenDestination)
                    Feature.Basic.Download -> TODO()
                }
            }
        }
    }
}
