package com.sorrowblue.comicviewer.feature.library.navigation

import androidx.navigation.NavGraphBuilder
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.utils.composable
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.feature.history.destinations.HistoryScreenDestination
import com.sorrowblue.comicviewer.feature.history.navigation.historyScreen
import com.sorrowblue.comicviewer.feature.library.LibraryScreen
import com.sorrowblue.comicviewer.feature.library.destinations.LibraryScreenDestination
import com.sorrowblue.comicviewer.feature.library.section.Feature
import com.sorrowblue.comicviewer.framework.ui.ComposeTransition
import com.sorrowblue.comicviewer.framework.ui.ComposeValue
import com.sorrowblue.comicviewer.framework.ui.animatedNavigation

val LibraryNavigationRoute = "${LibraryScreenDestination.route}_graph"

val RouteInLibraryNavigation get() = listOf(LibraryScreenDestination.route)

context(ComposeValue)
private fun NavGraphBuilder.libraryScreen(onFeatureClick: (Feature) -> Unit) {
    composable(LibraryScreenDestination) {
        LibraryScreen(
            contentPadding = contentPadding,
            onFeatureClick = onFeatureClick
        )
    }
}

context(ComposeValue)
fun NavGraphBuilder.libraryNavigation(
    navigateToBook: (Book) -> Unit,
    onFavoriteClick: (File) -> Unit,
    onSettingsClick: () -> Unit,
    onAddOnClick: (Feature.AddOn) -> Unit,
) {
    animatedNavigation(
        startDestination = LibraryScreenDestination.route,
        route = LibraryNavigationRoute,
        transitions = listOf(
            ComposeTransition(
                LibraryScreenDestination.route,
                HistoryScreenDestination.route,
                ComposeTransition.Type.SharedAxisX
            ),
            ComposeTransition(
                LibraryNavigationRoute,
                null,
                ComposeTransition.Type.ContainerTransform
            ),

            )
    ) {
        libraryScreen(
            onFeatureClick = {
                when (it) {
                    is Feature.AddOn -> onAddOnClick(it)
                    Feature.Basic.History -> navController.navigate(HistoryScreenDestination)
                    Feature.Basic.Download -> TODO()
                }
            }
        )

        historyScreen(
            onBackClick = navController::popBackStack,
            onSettingsClick = onSettingsClick,
            onFileClick = navigateToBook,
            onFavoriteClick = onFavoriteClick
        )
    }
}
