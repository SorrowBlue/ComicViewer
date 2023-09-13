package com.sorrowblue.comicviewer.bookshelf.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.sorrowblue.comicviewer.bookshelf.BookshelfRoute
import com.sorrowblue.comicviewer.domain.entity.BookshelfFolder
import com.sorrowblue.comicviewer.domain.entity.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.entity.file.Book
import com.sorrowblue.comicviewer.domain.entity.file.Folder
import com.sorrowblue.comicviewer.folder.navigation.folderRoute
import com.sorrowblue.comicviewer.folder.navigation.folderScreen
import com.sorrowblue.comicviewer.folder.navigation.navigateToFolder

private const val bookshelfRoute = "bookshelf"
const val bookshelfGraphRoute = "bookshelf_graph"
val routeInBookshelfGraph get() = listOf(bookshelfRoute, folderRoute(bookshelfRoute))

private fun NavGraphBuilder.bookshelfScreen(
    contentPadding: PaddingValues,
    onSettingsClick: () -> Unit,
    onBookshelfClick: (BookshelfFolder) -> Unit,
    onEditClick: (BookshelfId) -> Unit,
    onClickFab: () -> Unit,
) {
    composable(bookshelfRoute) {
        BookshelfRoute(
            contentPadding = contentPadding,
            onClickFab = onClickFab,
            onSettingsClick = onSettingsClick,
            onBookshelfClick = onBookshelfClick,
            onEditClick = onEditClick
        )
    }
}

fun NavGraphBuilder.bookshelfGroup(
    contentPadding: PaddingValues,
    navController: NavController,
    onSettingsClick: () -> Unit,
    navigateToBook: (BookshelfId, String) -> Unit,
    navigateToSearch: (BookshelfId, String) -> Unit,
    onAddFavoriteClick: (BookshelfId, String) -> Unit,
    onEditClick: (BookshelfId) -> Unit,
    onAddClick: () -> Unit
) {
    navigation(route = bookshelfGraphRoute, startDestination = bookshelfRoute) {
        bookshelfScreen(
            contentPadding = contentPadding,
            onSettingsClick = onSettingsClick,
            onBookshelfClick = {
                navController.navigateToFolder(
                    bookshelfRoute,
                    it.bookshelf.id, it.folder.path
                )
            },
            onEditClick = onEditClick,
            onClickFab = onAddClick,
        )
        folderScreen(
            prefix = bookshelfRoute,
            contentPadding = contentPadding,
            navigateToSearch = navigateToSearch,
            onClickFile = {
                when (it) {
                    is Book -> navigateToBook(it.bookshelfId, it.path)
                    is Folder -> navController.navigateToFolder(
                        prefix = bookshelfRoute,
                        bookshelfId = it.bookshelfId,
                        path = it.path
                    )
                }
            },
            onSettingsClick = onSettingsClick,
            onBackClick = navController::popBackStack,
            onAddFavoriteClick = { onAddFavoriteClick(it.bookshelfId, it.path) }
        )
    }
}
