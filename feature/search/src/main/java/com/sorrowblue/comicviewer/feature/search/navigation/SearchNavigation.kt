package com.sorrowblue.comicviewer.feature.search.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.sorrowblue.comicviewer.domain.model.Base64.decodeFromBase64
import com.sorrowblue.comicviewer.domain.model.Base64.encodeToBase64
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.search.SearchRoute
import com.sorrowblue.comicviewer.folder.navigation.folderScreen
import com.sorrowblue.comicviewer.folder.navigation.navigateToFolder

private const val BookshelfIdArg = "bookshelfId"
private const val PathArg = "path"

internal class SearchArgs(val bookshelfId: BookshelfId, val path: String) {

    constructor(savedStateHandle: SavedStateHandle) : this(
        BookshelfId(checkNotNull(savedStateHandle[BookshelfIdArg])),
        (checkNotNull<String>(savedStateHandle[PathArg])).decodeFromBase64(),
    )
}

private const val SearchRouteBase = "search"
private const val SearchRoute = "$SearchRouteBase?bookshelf_id={$BookshelfIdArg}&path={$PathArg}"

fun NavController.navigateToSearch(
    bookshelfId: BookshelfId,
    path: String,
    navOptions: NavOptions? = null,
) {
    this.navigate(
        "$SearchRouteBase?bookshelf_id=${bookshelfId.value}&path=${path.encodeToBase64()}",
        navOptions
    )
}

private fun NavGraphBuilder.searchScreen(
    onBackClick: () -> Unit,
    onFileClick: (File) -> Unit,
    onFileLongClick: (File) -> Unit,
) {
    composable(
        route = SearchRoute,
        arguments = listOf(
            navArgument(BookshelfIdArg) { type = NavType.IntType },
            navArgument(PathArg) { type = NavType.StringType },
        )
    ) {
        SearchRoute(
            onBackClick = onBackClick,
            onFileClick = onFileClick,
            onFileLongClick = onFileLongClick,
        )
    }
}

private const val SearchGraph = "search_graph"

fun NavGraphBuilder.searchGraph(
    contentPadding: PaddingValues,
    navController: NavController,
    onBookClick: (BookshelfId, String, Int) -> Unit,
    onClickLongFile: (File) -> Unit,
    onSettingsClick: () -> Unit,
) {
    navigation(route = SearchGraph, startDestination = SearchRoute) {
        searchScreen(
            onBackClick = navController::popBackStack,
            onFileClick = { file ->
                when (file) {
                    is Book -> onBookClick(file.bookshelfId, file.path, -1)
                    is Folder ->
                        navController.navigateToFolder(SearchRoute, file.bookshelfId, file.path)
                }
            },
            onFileLongClick = onClickLongFile,
        )

        folderScreen(
            prefix = SearchRoute,
            contentPadding = contentPadding,
            navigateToSearch = navController::navigateToSearch,
            onClickFile = { file, position ->
                when (file) {
                    is Book -> onBookClick(file.bookshelfId, file.path, position)
                    is Folder ->
                        navController.navigateToFolder(SearchRoute, file.bookshelfId, file.path)
                }
            },
            onSettingsClick = onSettingsClick,
            onBackClick = navController::popBackStack,
        )
    }
}
