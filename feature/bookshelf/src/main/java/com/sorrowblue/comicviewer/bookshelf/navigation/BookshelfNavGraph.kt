package com.sorrowblue.comicviewer.bookshelf.navigation

import com.ramcosta.composedestinations.navigation.DependenciesContainerBuilder
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.spec.DestinationSpec
import com.ramcosta.composedestinations.spec.Route
import com.sorrowblue.comicviewer.bookshelf.BookshelfFolderScreenNavigator
import com.sorrowblue.comicviewer.bookshelf.BookshelfScreenNavigator
import com.sorrowblue.comicviewer.bookshelf.destinations.BookshelfFolderScreenDestination
import com.sorrowblue.comicviewer.bookshelf.destinations.BookshelfScreenDestination
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfType
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.bookshelf.edit.BookshelfEditScreenNavigator
import com.sorrowblue.comicviewer.feature.bookshelf.edit.destinations.BookshelfEditScreenDestination
import com.sorrowblue.comicviewer.feature.bookshelf.selection.BookshelfSelectionScreenNavigator
import com.sorrowblue.comicviewer.feature.bookshelf.selection.destinations.BookshelfSelectionScreenDestination
import com.sorrowblue.comicviewer.framework.ui.AnimatedNavGraphSpec
import com.sorrowblue.comicviewer.framework.ui.TransitionsConfigure

object BookshelfNavGraph : AnimatedNavGraphSpec {
    override val route = "bookshelf_graph"
    override val startRoute: Route = BookshelfScreenDestination
    override val destinationsByRoute: Map<String, DestinationSpec<*>> = listOf(
        BookshelfScreenDestination,
        BookshelfSelectionScreenDestination,
        BookshelfEditScreenDestination,
        BookshelfFolderScreenDestination
    ).associateBy { it.route }

    override val transitions: List<TransitionsConfigure> = listOf(
        TransitionsConfigure(
            BookshelfScreenDestination.route,
            BookshelfFolderScreenDestination.route,
            TransitionsConfigure.Type.SharedAxisX
        ),
        TransitionsConfigure(
            BookshelfFolderScreenDestination.route,
            BookshelfFolderScreenDestination.route,
            TransitionsConfigure.Type.SharedAxisX
        ),
        TransitionsConfigure(
            BookshelfScreenDestination.route,
            BookshelfSelectionScreenDestination.route,
            TransitionsConfigure.Type.SharedAxisY
        ),
        TransitionsConfigure(
            BookshelfScreenDestination.route,
            BookshelfEditScreenDestination.route,
            TransitionsConfigure.Type.SharedAxisY
        ),
        TransitionsConfigure(
            BookshelfSelectionScreenDestination.route,
            BookshelfEditScreenDestination.route,
            TransitionsConfigure.Type.SharedAxisX
        ),
        TransitionsConfigure(
            route,
            null,
            TransitionsConfigure.Type.ContainerTransform
        )
    )
}

fun DependenciesContainerBuilder<*>.dependencyBookshelfNavGraph(
    onBookClick: (Book) -> Unit,
    onFavoriteClick: (File) -> Unit,
    onSearchClick: (BookshelfId, String) -> Unit,
    onRestoreComplete: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    dependency(BookshelfNavGraph) {
        object :
            BookshelfScreenNavigator,
            BookshelfSelectionScreenNavigator,
            BookshelfEditScreenNavigator,
            BookshelfFolderScreenNavigator {
            override fun onFavoriteClick(file: File) = onFavoriteClick(file)
            override fun onSearchClick(bookshelfId: BookshelfId, path: String) =
                onSearchClick(bookshelfId, path)

            override fun onSettingsClick() = onSettingsClick()

            override fun onRestoreComplete() {
                onRestoreComplete()
            }

            override fun navigateUp() {
                navController.navigateUp()
            }

            override fun onFabClick() {
                navController.navigate(BookshelfSelectionScreenDestination)
            }

            override fun onBookshelfClick(bookshelfId: BookshelfId, path: String) {
                navController.navigate(BookshelfFolderScreenDestination(bookshelfId, path, null))
            }

            override fun onEditClick(bookshelfId: BookshelfId) {
                navController.navigate(BookshelfEditScreenDestination(bookshelfId))
            }

            override fun onSourceClick(bookshelfType: BookshelfType) {
                navController.navigate(BookshelfEditScreenDestination(bookshelfType = bookshelfType))
            }

            override fun onComplete() {
                if (!navController.popBackStack(BookshelfSelectionScreenDestination.route, true)) {
                    navController.popBackStack()
                }
            }

            override fun onFileClick(file: File) {
                when (file) {
                    is Book -> onBookClick(file)
                    is Folder -> navController.navigate(
                        BookshelfFolderScreenDestination(file.bookshelfId, file.path, null)
                    )
                }
            }
        }
    }
}
