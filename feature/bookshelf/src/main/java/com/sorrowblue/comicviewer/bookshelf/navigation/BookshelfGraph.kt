package com.sorrowblue.comicviewer.bookshelf.navigation

import androidx.navigation.NavGraphBuilder
import com.ramcosta.composedestinations.navigation.navigate
import com.sorrowblue.comicviewer.bookshelf.destinations.BookshelfScreenDestination
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.bookshelf.edit.destinations.BookshelfEditScreenDestination
import com.sorrowblue.comicviewer.feature.bookshelf.edit.navigation.bookshelfEditScreen
import com.sorrowblue.comicviewer.feature.bookshelf.selection.destinations.BookshelfSelectionScreenDestination
import com.sorrowblue.comicviewer.feature.bookshelf.selection.navigation.bookshelfSelectionScreen
import com.sorrowblue.comicviewer.folder.navigation.folderRoute
import com.sorrowblue.comicviewer.folder.navigation.folderScreen
import com.sorrowblue.comicviewer.folder.navigation.navigateToFolder
import com.sorrowblue.comicviewer.framework.ui.ComposeTransition
import com.sorrowblue.comicviewer.framework.ui.ComposeValue
import com.sorrowblue.comicviewer.framework.ui.animatedNavigation

const val BookshelfGraphRoute = "bookshelf_graph"
private val BookshelfFolderRoute = folderRoute(BookshelfScreenDestination.baseRoute)

val routeInBookshelfGraph get() = listOf(BookshelfScreenDestination.route, BookshelfFolderRoute)

context(ComposeValue)
fun NavGraphBuilder.bookshelfGraph(
    onSettingsClick: () -> Unit,
    navigateToBook: (Book) -> Unit,
    navigateToSearch: (BookshelfId, String) -> Unit,
    onRestoreComplete: () -> Unit,
    onFavoriteClick: (File) -> Unit,
) {
    animatedNavigation(
        startDestination = BookshelfScreenDestination.route,
        route = BookshelfGraphRoute,
        transitions = listOf(
            ComposeTransition(
                BookshelfScreenDestination.route,
                BookshelfFolderRoute,
                ComposeTransition.Type.SharedAxisX
            ),
            ComposeTransition(
                BookshelfFolderRoute,
                BookshelfFolderRoute,
                ComposeTransition.Type.SharedAxisX
            ),
            ComposeTransition(
                BookshelfScreenDestination.route,
                BookshelfSelectionScreenDestination.route,
                ComposeTransition.Type.SharedAxisY
            ),
            ComposeTransition(
                BookshelfScreenDestination.route,
                BookshelfEditScreenDestination.route,
                ComposeTransition.Type.SharedAxisY
            ),
            ComposeTransition(
                BookshelfSelectionScreenDestination.route,
                BookshelfEditScreenDestination.route,
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
            onFabClick = { navController.navigate(BookshelfSelectionScreenDestination) },
            onBookshelfClick = navController::navigateToBookshelfFolder,
            onEditClick = {
                navController.navigate(BookshelfEditScreenDestination(bookshelfId = it))
            }
        )
        bookshelfSelectionScreen(
            onBackClick = navController::popBackStack,
            onSourceClick = {
                navController.navigate(BookshelfEditScreenDestination(bookshelfType = it))
            }
        )
        bookshelfEditScreen(
            onBackClick = navController::popBackStack,
            onComplete = {
                if (!navController.popBackStack(BookshelfSelectionScreenDestination.route, true)) {
                    navController.popBackStack()
                }
            }
        )
        folderScreen(
            prefix = BookshelfScreenDestination.baseRoute,
            onBackClick = navController::popBackStack,
            onSearchClick = navigateToSearch,
            onSettingsClick = onSettingsClick,
            onClickFile = { file ->
                when (file) {
                    is Book -> navigateToBook(file)
                    is Folder -> navController.navigateToFolder(
                        prefix = BookshelfScreenDestination.baseRoute,
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
