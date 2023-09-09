package com.sorrowblue.comicviewer.library.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.sorrowblue.comicviewer.domain.entity.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.feature.history.navigation.historyGroup
import com.sorrowblue.comicviewer.feature.history.navigation.navigateToHistoryGroup
import com.sorrowblue.comicviewer.library.LibraryRoute
import com.sorrowblue.comicviewer.library.LocalFeature

const val LibraryRoute = "library"

private fun NavGraphBuilder.libraryScreen(
    contentPadding: PaddingValues,
    onFeatureClick: (LocalFeature) -> Unit
) {
    composable(LibraryRoute) {
        LibraryRoute(
            contentPadding = contentPadding,
            onFeatureClick = onFeatureClick
        )
    }
}

const val LibraryGroupRoute = "${LibraryRoute}_group"

fun NavGraphBuilder.libraryGroup(
    contentPadding: PaddingValues,
    navController: NavController,
    onBookClick: (BookshelfId, String) -> Unit,
    onSettingsClick: () -> Unit,
    onAddFavoriteClick: (BookshelfId, String) -> Unit,
    navigateToSearch: (BookshelfId, String) -> Unit
) {
    navigation(route = LibraryGroupRoute, startDestination = LibraryRoute) {
        libraryScreen(
            contentPadding = contentPadding,
            onFeatureClick = {
                when (it) {
                    LocalFeature.HISTORY -> navController.navigateToHistoryGroup()
                    LocalFeature.DOWNLOADED -> Unit /*TODO()*/
                }
            }
        )

        historyGroup(
            contentPadding = contentPadding,
            navController = navController,
            onBookClick = onBookClick,
            onSettingsClick = onSettingsClick,
            onAddFavoriteClick = onAddFavoriteClick,
            navigateToSearch = navigateToSearch
        )
    }
}
