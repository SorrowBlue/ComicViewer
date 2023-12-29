package com.sorrowblue.comicviewer.feature.library.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.feature.history.navigation.HistoryScreenRoute
import com.sorrowblue.comicviewer.feature.history.navigation.historyScreen
import com.sorrowblue.comicviewer.feature.history.navigation.navigateToHistory
import com.sorrowblue.comicviewer.feature.library.LibraryScreen
import com.sorrowblue.comicviewer.feature.library.section.Feature
import com.sorrowblue.comicviewer.framework.ui.ComposeTransition
import com.sorrowblue.comicviewer.framework.ui.ComposeValue
import com.sorrowblue.comicviewer.framework.ui.animatedNavigation

private const val LibraryScreenRoute = "library"
const val LibraryNavigationRoute = "${LibraryScreenRoute}_graph"

val RouteInLibraryNavigation get() = listOf(LibraryScreenRoute)

context(ComposeValue)
private fun NavGraphBuilder.libraryScreen(onFeatureClick: (Feature) -> Unit) {
    composable(LibraryScreenRoute) {
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
        startDestination = LibraryScreenRoute,
        route = LibraryNavigationRoute,
        transitions = listOf(
            ComposeTransition(
                LibraryScreenRoute,
                HistoryScreenRoute,
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
                    Feature.Basic.History -> navController.navigateToHistory()
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
