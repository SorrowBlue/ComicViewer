package com.sorrowblue.comicviewer.feature.library.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.feature.history.navigation.historyGroup
import com.sorrowblue.comicviewer.feature.history.navigation.navigateToHistoryGroup
import com.sorrowblue.comicviewer.feature.library.LibraryRoute
import com.sorrowblue.comicviewer.feature.library.section.Feature

private const val LibraryRoute = "library"
const val LibraryGraphRoute = "${LibraryRoute}_graph"

val RouteInLibraryGraph get() = listOf(LibraryRoute)

private fun NavGraphBuilder.libraryScreen(
    contentPadding: PaddingValues,
    onFeatureClick: (Feature) -> Unit,
) {
    composable(LibraryRoute) {
        LibraryRoute(
            contentPadding = contentPadding,
            onFeatureClick = onFeatureClick,
        )
    }
}

fun NavGraphBuilder.libraryGroup(
    contentPadding: PaddingValues,
    navController: NavController,
    onBookClick: (BookshelfId, String, Int) -> Unit,
    onSettingsClick: () -> Unit,
    navigateToSearch: (BookshelfId, String) -> Unit,
    onAddOnClick: (Feature.AddOn) -> Unit,
) {
    navigation(route = LibraryGraphRoute, startDestination = LibraryRoute) {
        libraryScreen(
            contentPadding = contentPadding,
            onFeatureClick = {
                when (it) {
                    is Feature.AddOn -> onAddOnClick(it)
                    Feature.Basic.History -> navController.navigateToHistoryGroup()
                    Feature.Basic.Download -> TODO()
                }
            }
        )

        historyGroup(
            contentPadding = contentPadding,
            navController = navController,
            onBookClick = onBookClick,
            onSettingsClick = onSettingsClick,
            navigateToSearch = navigateToSearch
        )
    }
}
