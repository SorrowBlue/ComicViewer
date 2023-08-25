package com.sorrowblue.comicviewer.bookshelf.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Add
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.sorrowblue.comicviewer.bookshelf.BookshelfRoute
import com.sorrowblue.comicviewer.domain.entity.BookshelfFolder
import com.sorrowblue.comicviewer.domain.entity.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.entity.file.Book
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.entity.file.Folder
import com.sorrowblue.comicviewer.feature.bookshelf.edit.navigation.bookshelfEditScreen
import com.sorrowblue.comicviewer.feature.bookshelf.edit.navigation.navigateToBookshelfEdit
import com.sorrowblue.comicviewer.feature.bookshelf.selection.navigation.BookshelfSelectionRoute
import com.sorrowblue.comicviewer.feature.bookshelf.selection.navigation.bookshelfSelectionScreen
import com.sorrowblue.comicviewer.folder.navigation.folderScreen
import com.sorrowblue.comicviewer.folder.navigation.navigateToFolder
import com.sorrowblue.comicviewer.framework.compose.FabVisibleState
import com.sorrowblue.comicviewer.framework.compose.LocalLifecycleState
import kotlinx.coroutines.launch

const val BookshelfRoute = "bookshelf"

private fun NavGraphBuilder.bookshelfScreen(
    onSettingsClick: () -> Unit,
    onBookshelfClick: (BookshelfFolder) -> Unit,
    onEditClick: (BookshelfId) -> Unit,
    onClickFab: () -> Unit,
    fabState: FabVisibleState
) {
    composable(BookshelfRoute) {
        val scope = rememberCoroutineScope()
        LocalLifecycleState(
            onStart = {
                scope.launch {
                    fabState.show(Icons.TwoTone.Add) {
                        onClickFab()
                    }
                }
            },
            onStop = {
                fabState.hide()
            }
        )
        BookshelfRoute(
            onSettingsClick = onSettingsClick,
            onBookshelfClick = onBookshelfClick,
            onEditClick = onEditClick
        )
    }
}

const val BookshelfGroupRoute = "bookshelf_group"

fun NavGraphBuilder.bookshelfGroup(
    navController: NavController,
    fabState: FabVisibleState,
    onSettingsClick: () -> Unit,
    navigateToBook: (BookshelfId, String) -> Unit,
    navigateToSearch: (BookshelfId, String) -> Unit,
    onAddFavoriteClick: (File) -> Unit,
) {
    navigation(route = BookshelfGroupRoute, startDestination = BookshelfRoute) {
        bookshelfScreen(
            onSettingsClick = onSettingsClick,
            onBookshelfClick = {
                navController.navigateToFolder(
                    it.bookshelf.id,
                    it.folder.path, BookshelfRoute
                )
            },
            onEditClick = navController::navigateToBookshelfEdit,
            onClickFab = navController::navigateToBookshelfSelection,
            fabState = fabState
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
            onAddFavoriteClick =onAddFavoriteClick
        )
    }
}

fun NavController.navigateToBookshelfSelection(navOptions: NavOptions? = null) {
    navigate(BookshelfSelectionRoute, navOptions)
}
