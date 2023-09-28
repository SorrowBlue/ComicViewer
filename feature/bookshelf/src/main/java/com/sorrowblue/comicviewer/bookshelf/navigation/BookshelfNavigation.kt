package com.sorrowblue.comicviewer.bookshelf.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.sorrowblue.comicviewer.bookshelf.BookshelfRoute
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.bookshelf.edit.navigation.bookshelfEditScreen
import com.sorrowblue.comicviewer.feature.bookshelf.edit.navigation.navigateToBookshelfEdit
import com.sorrowblue.comicviewer.feature.bookshelf.selection.navigation.bookshelfSelectionRoute
import com.sorrowblue.comicviewer.feature.bookshelf.selection.navigation.bookshelfSelectionScreen
import com.sorrowblue.comicviewer.feature.bookshelf.selection.navigation.navigateToBookshelfSelection
import com.sorrowblue.comicviewer.folder.navigation.folderRoute
import com.sorrowblue.comicviewer.folder.navigation.folderScreen
import com.sorrowblue.comicviewer.folder.navigation.navigateToFolder

const val bookshelfRoute = "bookshelf"
const val bookshelfGraphRoute = "bookshelf_graph"
val routeInBookshelfGraph get() = listOf(bookshelfRoute, folderRoute(bookshelfRoute))

fun NavController.navigateToBookshelfFolder(id: BookshelfId, path: String, position: Int = -1) {
    navigateToFolder(prefix = bookshelfRoute, bookshelfId = id, path = path, position = position)
}

private fun NavGraphBuilder.bookshelfScreen(
    contentPadding: PaddingValues,
    onSettingsClick: () -> Unit,
    onBookshelfClick: (BookshelfId, String) -> Unit,
    onEditClick: (BookshelfId) -> Unit,
) {
    composable(bookshelfRoute) {
        BookshelfRoute(
            contentPadding = contentPadding,
            onSettingsClick = onSettingsClick,
            onBookshelfClick = onBookshelfClick,
            onEditClick = onEditClick,
        )
    }
}

fun NavController.navigateToBookshelfSelection(navOptions: NavOptions? = null) {
    navigateToBookshelfSelection(navOptions)
}

fun NavGraphBuilder.bookshelfGraph(
    contentPadding: PaddingValues,
    navController: NavController,
    onClickLongFile: (File) -> Unit,
    onSettingsClick: () -> Unit,
    navigateToBook: (BookshelfId, String, Int) -> Unit,
    navigateToSearch: (BookshelfId, String) -> Unit,
    onRestoreComplete: () -> Unit,
) {
    navigation(route = bookshelfGraphRoute, startDestination = bookshelfRoute) {
        bookshelfScreen(
            contentPadding = contentPadding,
            onSettingsClick = onSettingsClick,
            onBookshelfClick = navController::navigateToBookshelfFolder,
            onEditClick = navController::navigateToBookshelfEdit,
        )
        bookshelfSelectionScreen(
            onBackClick = navController::popBackStack,
            onSourceClick = navController::navigateToBookshelfEdit
        )
        bookshelfEditScreen(
            onBackClick = navController::popBackStack,
            onComplete = { navController.popBackStack(bookshelfSelectionRoute, true) }
        )
        folderScreen(
            prefix = bookshelfRoute,
            contentPadding = contentPadding,
            navigateToSearch = navigateToSearch,
            onClickLongFile = onClickLongFile,
            onClickFile = { file, position ->
                when (file) {
                    is Book -> navigateToBook(file.bookshelfId, file.path, position)
                    is Folder -> navController.navigateToFolder(
                        prefix = bookshelfRoute,
                        bookshelfId = file.bookshelfId,
                        path = file.path
                    )
                }
            },
            onSettingsClick = onSettingsClick,
            onBackClick = navController::popBackStack,
            onRestoreComplete = onRestoreComplete,
        )
    }
}
