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
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.bookshelf.edit.navigation.bookshelfEditScreen
import com.sorrowblue.comicviewer.feature.bookshelf.edit.navigation.navigateToBookshelfEdit
import com.sorrowblue.comicviewer.feature.bookshelf.selection.navigation.BookshelfSelectionRoute
import com.sorrowblue.comicviewer.feature.bookshelf.selection.navigation.bookshelfSelectionScreen
import com.sorrowblue.comicviewer.feature.bookshelf.selection.navigation.navigateToBookshelfSelection
import com.sorrowblue.comicviewer.folder.navigation.folderRoute
import com.sorrowblue.comicviewer.folder.navigation.folderScreen
import com.sorrowblue.comicviewer.folder.navigation.navigateToFolder

const val BookshelfRoute = "bookshelf"
const val BookshelfGraphRoute = "bookshelf_graph"
val routeInBookshelfGraph get() = listOf(BookshelfRoute, folderRoute(BookshelfRoute))

fun NavController.navigateToBookshelfFolder(id: BookshelfId, path: String, position: Int = -1) {
    navigateToFolder(prefix = BookshelfRoute, bookshelfId = id, path = path, position = position)
}

private fun NavGraphBuilder.bookshelfScreen(
    contentPadding: PaddingValues,
    onSettingsClick: () -> Unit,
    onBookshelfClick: (BookshelfId, String) -> Unit,
    onEditClick: (BookshelfId) -> Unit,
) {
    composable(BookshelfRoute) {
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
    isMobile: Boolean,
    contentPadding: PaddingValues,
    navController: NavController,
    onSettingsClick: () -> Unit,
    navigateToBook: (BookshelfId, String, Int) -> Unit,
    navigateToSearch: (BookshelfId, String) -> Unit,
    onRestoreComplete: () -> Unit,
) {
    navigation(route = BookshelfGraphRoute, startDestination = BookshelfRoute) {
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
            isMobile = isMobile,
            onBackClick = navController::popBackStack,
            onComplete = {
                if (!navController.popBackStack(BookshelfSelectionRoute, true)) {
                    navController.popBackStack()
                }
            }
        )
        folderScreen(
            prefix = BookshelfRoute,
            contentPadding = contentPadding,
            navigateToSearch = navigateToSearch,
            onClickFile = { file, position ->
                when (file) {
                    is Book -> navigateToBook(file.bookshelfId, file.path, position)
                    is Folder -> navController.navigateToFolder(
                        prefix = BookshelfRoute,
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
