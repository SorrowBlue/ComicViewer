package com.sorrowblue.comicviewer.feature.library.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.feature.history.navigation.historyScreen
import com.sorrowblue.comicviewer.feature.history.navigation.navigateToHistory
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
    navigateToBook: (Book) -> Unit,
    onFavoriteClick: (File) -> Unit,
    onSettingsClick: () -> Unit,
    onAddOnClick: (Feature.AddOn) -> Unit,
) {
    navigation(route = LibraryGraphRoute, startDestination = LibraryRoute) {
        libraryScreen(
            contentPadding = contentPadding,
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
            onFavoriteClick = onFavoriteClick,
            contentPadding = contentPadding
        )
    }
}
