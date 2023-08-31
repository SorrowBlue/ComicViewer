package com.sorrowblue.comicviewer.bookshelf.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.sorrowblue.comicviewer.bookshelf.BookshelfRoute
import com.sorrowblue.comicviewer.domain.entity.BookshelfFolder
import com.sorrowblue.comicviewer.domain.entity.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.entity.file.Book
import com.sorrowblue.comicviewer.domain.entity.file.Folder
import com.sorrowblue.comicviewer.feature.bookshelf.edit.navigation.bookshelfEditScreen
import com.sorrowblue.comicviewer.feature.bookshelf.edit.navigation.navigateToBookshelfEdit
import com.sorrowblue.comicviewer.feature.bookshelf.selection.navigation.BookshelfSelectionRoute
import com.sorrowblue.comicviewer.feature.bookshelf.selection.navigation.bookshelfSelectionScreen
import com.sorrowblue.comicviewer.folder.navigation.folderRoute
import com.sorrowblue.comicviewer.folder.navigation.folderScreen
import com.sorrowblue.comicviewer.folder.navigation.navigateToFolder

const val BookshelfRoute = "bookshelf"
val BookshelfFolderRoute = folderRoute(BookshelfRoute)

private fun NavGraphBuilder.bookshelfScreen(
    contentPadding: PaddingValues,
    onSettingsClick: () -> Unit,
    onBookshelfClick: (BookshelfFolder) -> Unit,
    onEditClick: (BookshelfId) -> Unit,
    onClickFab: () -> Unit,
) {
    composable(BookshelfRoute) {
        BookshelfRoute(
            contentPadding = contentPadding,
            onClickFab = onClickFab,
            onSettingsClick = onSettingsClick,
            onBookshelfClick = onBookshelfClick,
            onEditClick = onEditClick
        )
    }
}

const val BookshelfGroupRoute = "bookshelf_group"

fun NavGraphBuilder.bookshelfGroup(
    contentPadding: PaddingValues,
    navController: NavController,
    onSettingsClick: () -> Unit,
    navigateToBook: (BookshelfId, String) -> Unit,
    navigateToSearch: (BookshelfId, String) -> Unit,
    onAddFavoriteClick: (BookshelfId, String) -> Unit,
) {
    navigation(route = BookshelfGroupRoute, startDestination = BookshelfRoute) {
        bookshelfScreen(
            contentPadding = contentPadding,
            onSettingsClick = onSettingsClick,
            onBookshelfClick = {
                navController.navigateToFolder(
                    it.bookshelf.id,
                    it.folder.path, BookshelfRoute
                )
            },
            onEditClick = navController::navigateToBookshelfEdit,
            onClickFab = navController::navigateToBookshelfSelection,
        )
        bookshelfSelectionScreen(
            onBackClick = navController::popBackStack,
            onSourceClick = navController::navigateToBookshelfEdit
        )
        bookshelfEditScreen(
            onBackClick = navController::popBackStack,
            onComplete = { navController.popBackStack(BookshelfSelectionRoute, true) }
        )
        folderScreen(
            contentPadding = contentPadding,
            prefix = BookshelfRoute,
            navigateToSearch = navigateToSearch,
            onClickFile = {
                when (it) {
                    is Book -> navigateToBook(it.bookshelfId, it.path)
                    is Folder -> navController.navigateToFolder(
                        it.bookshelfId,
                        it.path,
                        BookshelfRoute
                    )
                }
            },
            onSettingsClick = onSettingsClick,
            onBackClick = navController::popBackStack,
            onAddFavoriteClick = { onAddFavoriteClick(it.bookshelfId, it.path)}
        )
    }
}

fun NavController.navigateToBookshelfSelection(navOptions: NavOptions? = null) {
    navigate(BookshelfSelectionRoute, navOptions)
}
