package com.sorrowblue.comicviewer.bookshelf.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.navigation
import androidx.navigation.navOptions
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.bookshelf.edit.navigation.bookshelfEditScreen
import com.sorrowblue.comicviewer.feature.bookshelf.edit.navigation.navigateToBookshelfEdit
import com.sorrowblue.comicviewer.feature.bookshelf.selection.navigation.BookshelfSelectionRoute
import com.sorrowblue.comicviewer.feature.bookshelf.selection.navigation.bookshelfSelectionScreen
import com.sorrowblue.comicviewer.feature.bookshelf.selection.navigation.navigateToBookshelfSelection
import com.sorrowblue.comicviewer.folder.navigation.folderScreen
import com.sorrowblue.comicviewer.folder.navigation.navigateToFolder
import com.sorrowblue.comicviewer.framework.ui.ComposeValue

context(ComposeValue)
fun NavGraphBuilder.bookshelfGraph(
    onSettingsClick: () -> Unit,
    navigateToBook: (Book) -> Unit,
    navigateToSearch: (BookshelfId, String) -> Unit,
    onRestoreComplete: () -> Unit,
    onFavoriteClick: (File) -> Unit,
) {
    navigation(route = BookshelfGraphRoute, startDestination = BookshelfRoute) {
        bookshelfScreen(
            onSettingsClick = onSettingsClick,
            onFabClick = navController::navigateToBookshelfSelection,
            onBookshelfClick = navController::navigateToBookshelfFolder,
            onEditClick = navController::navigateToBookshelfEdit
        )
        bookshelfSelectionScreen(
            onBackClick = navController::popBackStack,
            onSourceClick = {
                navController.navigateToBookshelfEdit(it, navOptions {
                    popUpTo(BookshelfSelectionRoute) { inclusive = true }
                })
            }
        )
        bookshelfEditScreen(
            onBackClick = navController::popBackStack,
            onComplete = {
                if (!navController.popBackStack(BookshelfSelectionRoute, true)) {
                    navController.popBackStack()
                }
            }
        )
        folderScreen(
            prefix = BookshelfRoute,
            onBackClick = navController::popBackStack,
            onSearchClick = navigateToSearch,
            onSettingsClick = onSettingsClick,
            onClickFile = { file ->
                when (file) {
                    is Book -> navigateToBook(file)
                    is Folder -> navController.navigateToFolder(
                        prefix = BookshelfRoute,
                        bookshelfId = file.bookshelfId,
                        path = file.path
                    )
                }
            },
            onRestoreComplete = onRestoreComplete,
            onFavoriteClick = onFavoriteClick
        )
    }
}
