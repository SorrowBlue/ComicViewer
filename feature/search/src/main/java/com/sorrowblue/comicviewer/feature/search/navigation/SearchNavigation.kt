package com.sorrowblue.comicviewer.feature.search.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.utils.composable
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.search.SearchScreen
import com.sorrowblue.comicviewer.feature.search.destinations.SearchScreenDestination
import com.sorrowblue.comicviewer.folder.navigation.folderRoute
import com.sorrowblue.comicviewer.folder.navigation.folderScreen
import com.sorrowblue.comicviewer.folder.navigation.navigateToFolder
import com.sorrowblue.comicviewer.framework.ui.ComposeTransition
import com.sorrowblue.comicviewer.framework.ui.ComposeValue
import com.sorrowblue.comicviewer.framework.ui.animatedNavigation

class SearchArgs(val bookshelfId: BookshelfId, val path: String)

fun NavController.navigateToSearch(bookshelfId: BookshelfId, path: String) {
    navigate(SearchScreenDestination(bookshelfId, path))
}

context(ComposeValue)
private fun NavGraphBuilder.searchScreen(
    onBackClick: () -> Unit,
    onFileClick: (File) -> Unit,
    contentPadding: PaddingValues,
    onOpenFolderClick: (File) -> Unit,
    onFavoriteClick: (File) -> Unit,
) {
    composable(SearchScreenDestination) {
        SearchScreen(
            args = navArgs,
            savedStateHandle = navBackStackEntry.savedStateHandle,
            contentPadding = contentPadding,
            onBackClick = onBackClick,
            onFileClick = onFileClick,
            onFavoriteClick = onFavoriteClick,
            onOpenFolderClick = onOpenFolderClick,
        )
    }
}

private const val SearchGraph = "search_graph"

context(ComposeValue)
fun NavGraphBuilder.searchGraph(
    navigateToBook: (Book) -> Unit,
    navigateToSettings: () -> Unit,
    navigateToFavoriteAdd: (File) -> Unit,
) {
    animatedNavigation(
        startDestination = SearchScreenDestination.route,
        route = SearchGraph,
        transitions = listOf(
            ComposeTransition(
                SearchScreenDestination.route,
                folderRoute(SearchScreenDestination.baseRoute),
                ComposeTransition.Type.SharedAxisX
            ),
            ComposeTransition(
                SearchGraph,
                null,
                ComposeTransition.Type.ContainerTransform
            )
        )
    ) {
        searchScreen(
            onBackClick = navController::popBackStack,
            onFileClick = { file ->
                when (file) {
                    is Book -> navigateToBook(file)
                    is Folder ->
                        navController.navigateToFolder(
                            SearchScreenDestination.baseRoute,
                            file.bookshelfId,
                            file.path
                        )
                }
            },
            contentPadding = contentPadding,
            onFavoriteClick = navigateToFavoriteAdd,
            onOpenFolderClick = {
                navController.navigateToFolder(
                    SearchScreenDestination.baseRoute,
                    it.bookshelfId,
                    it.parent
                )
            }
        )

        folderScreen(
            prefix = SearchScreenDestination.baseRoute,
            onBackClick = navController::popBackStack,
            onSearchClick = navController::navigateToSearch,
            onSettingsClick = navigateToSettings,
            onClickFile = { file ->
                when (file) {
                    is Book -> navigateToBook(file)
                    is Folder -> navController.navigateToFolder(
                        SearchScreenDestination.baseRoute,
                        file.bookshelfId,
                        file.path
                    )
                }
            },
            onFavoriteClick = navigateToFavoriteAdd
        )
    }
}
