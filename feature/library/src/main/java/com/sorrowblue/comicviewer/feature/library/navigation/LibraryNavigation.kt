package com.sorrowblue.comicviewer.feature.library.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.feature.history.navigation.historyScreen
import com.sorrowblue.comicviewer.feature.history.navigation.navigateToHistory
import com.sorrowblue.comicviewer.feature.library.LibraryRoute
import com.sorrowblue.comicviewer.feature.library.section.Feature
import com.sorrowblue.comicviewer.framework.ui.ComposeValue

private const val LibraryRoute = "library"
const val LibraryGraphRoute = "${LibraryRoute}_graph"

val RouteInLibraryGraph get() = listOf(LibraryRoute)

context(ComposeValue)
private fun NavGraphBuilder.libraryScreen(
    onFeatureClick: (Feature) -> Unit,
) {
    composable(LibraryRoute) {
        LibraryRoute(
            contentPadding = contentPadding,
            onFeatureClick = onFeatureClick
        )
    }
}

context(ComposeValue)
fun NavGraphBuilder.libraryGroup(
    navigateToBook: (Book) -> Unit,
    onFavoriteClick: (File) -> Unit,
    onSettingsClick: () -> Unit,
    onAddOnClick: (Feature.AddOn) -> Unit,
) {
    navigation(route = LibraryGraphRoute, startDestination = LibraryRoute) {
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
