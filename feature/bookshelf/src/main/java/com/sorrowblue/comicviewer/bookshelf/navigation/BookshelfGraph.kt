package com.sorrowblue.comicviewer.bookshelf.navigation

import androidx.navigation.NavGraphBuilder
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.bookshelf.edit.navigation.BookshelfEditRoute
import com.sorrowblue.comicviewer.feature.bookshelf.edit.navigation.bookshelfEditScreen
import com.sorrowblue.comicviewer.feature.bookshelf.edit.navigation.navigateToBookshelfEdit
import com.sorrowblue.comicviewer.feature.bookshelf.selection.navigation.BookshelfSelectionRoute
import com.sorrowblue.comicviewer.feature.bookshelf.selection.navigation.bookshelfSelectionScreen
import com.sorrowblue.comicviewer.feature.bookshelf.selection.navigation.navigateToBookshelfSelection
import com.sorrowblue.comicviewer.folder.navigation.folderRoute
import com.sorrowblue.comicviewer.folder.navigation.folderScreen
import com.sorrowblue.comicviewer.folder.navigation.navigateToFolder
import com.sorrowblue.comicviewer.framework.ui.ComposeTransition
import com.sorrowblue.comicviewer.framework.ui.ComposeValue
import com.sorrowblue.comicviewer.framework.ui.animatedNavigation

context(ComposeValue)
fun NavGraphBuilder.bookshelfGraph(
    onSettingsClick: () -> Unit,
    navigateToBook: (Book) -> Unit,
    navigateToSearch: (BookshelfId, String) -> Unit,
    onRestoreComplete: () -> Unit,
    onFavoriteClick: (File) -> Unit,
) {
    animatedNavigation(
        startDestination = BookshelfRoute,
        route = BookshelfGraphRoute,
        transitions = listOf(
            ComposeTransition(
                BookshelfRoute,
                folderRoute(BookshelfRoute),
                ComposeTransition.Type.SharedAxisX
            ),
            ComposeTransition(
                folderRoute(BookshelfRoute),
                folderRoute(BookshelfRoute),
                ComposeTransition.Type.SharedAxisX
            ),
            ComposeTransition(
                BookshelfRoute,
                BookshelfSelectionRoute,
                ComposeTransition.Type.SharedAxisY
            ),
            ComposeTransition(
                BookshelfRoute,
                BookshelfEditRoute,
                ComposeTransition.Type.SharedAxisY
            ),
            ComposeTransition(
                BookshelfSelectionRoute,
                BookshelfEditRoute,
                ComposeTransition.Type.SharedAxisX
            ),
            ComposeTransition(
                BookshelfGraphRoute,
                null,
                ComposeTransition.Type.ContainerTransform
            )
        ),
    ) {
        bookshelfScreen(
            onSettingsClick = onSettingsClick,
            onFabClick = navController::navigateToBookshelfSelection,
            onBookshelfClick = navController::navigateToBookshelfFolder,
            onEditClick = navController::navigateToBookshelfEdit
        )
        bookshelfSelectionScreen(
            onBackClick = navController::popBackStack,
            onSourceClick = navController::navigateToBookshelfEdit
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
