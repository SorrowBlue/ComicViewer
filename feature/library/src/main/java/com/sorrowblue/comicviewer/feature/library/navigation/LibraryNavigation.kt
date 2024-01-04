package com.sorrowblue.comicviewer.feature.library.navigation

import androidx.navigation.NavController
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.scope.DestinationScopeWithNoDependencies
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

val RouteInLibraryNavigation get() = listOf(LibraryScreenDestination.route)

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

fun DestinationScopeWithNoDependencies<*>.libraryNavGraphNavigator(navigator: LibraryNavGraphNavigator) =
    LibraryNavGraphNavigatorImpl(navigator, navController)

interface LibraryNavGraphNavigator {
    fun onSettingsClick()
    fun navigateToBook(book: Book)
    fun onFavoriteClick(file: File)
}

class LibraryNavGraphNavigatorImpl internal constructor(
    navigator: LibraryNavGraphNavigator,
    private val navController: NavController,
) : LibraryScreenNavigator,
    HistoryScreenNavigator,
    LibraryNavGraphNavigator by navigator {

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
