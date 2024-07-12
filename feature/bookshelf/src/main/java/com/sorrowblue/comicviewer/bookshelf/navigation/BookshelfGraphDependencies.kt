package com.sorrowblue.comicviewer.bookshelf.navigation

import androidx.compose.runtime.Composable
import com.ramcosta.composedestinations.navigation.DependenciesContainerBuilder
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.navigation.navGraph
import com.sorrowblue.comicviewer.bookshelf.BookshelfScreenNavigator
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfType
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.bookshelf.NavGraphs
import com.sorrowblue.comicviewer.feature.bookshelf.destinations.BookshelfFolderScreenDestination
import com.sorrowblue.comicviewer.feature.bookshelf.edit.BookshelfEditScreenNavigator
import com.sorrowblue.comicviewer.feature.bookshelf.edit.destinations.BookshelfEditScreenDestination
import com.sorrowblue.comicviewer.feature.bookshelf.selection.BookshelfSelectionScreenNavigator
import com.sorrowblue.comicviewer.feature.bookshelf.selection.destinations.BookshelfSelectionScreenDestination
import com.sorrowblue.comicviewer.folder.FolderScreenNavigator

@Composable
fun DependenciesContainerBuilder<*>.BookshelfGraphDependencies(
    onBookClick: (Book) -> Unit,
    onFavoriteClick: (File) -> Unit,
    onSearchClick: (BookshelfId, String) -> Unit,
    onRestoreComplete: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    navGraph(NavGraphs.bookshelf) {
        dependency(object :
            BookshelfScreenNavigator,
            BookshelfSelectionScreenNavigator,
            BookshelfEditScreenNavigator,
            FolderScreenNavigator {

            override val navigator get() = destinationsNavigator

            override fun onFavoriteClick(file: File) = onFavoriteClick(file)

            override fun onSearchClick(bookshelfId: BookshelfId, path: String) =
                onSearchClick(bookshelfId, path)

            override fun onSettingsClick() = onSettingsClick()

            override fun onRestoreComplete() = onRestoreComplete()

            override fun navigateUp() {
                navController.navigateUp()
            }

            override fun onFabClick() =
                navigator.navigate(BookshelfSelectionScreenDestination)

            override fun onBookshelfClick(bookshelfId: BookshelfId, path: String) =
                navigator.navigate(
                    BookshelfFolderScreenDestination(bookshelfId, path, null)
                )

            override fun onEditClick(bookshelfId: BookshelfId) =
                navigator.navigate(BookshelfEditScreenDestination(bookshelfId))

            override fun onSourceClick(bookshelfType: BookshelfType) =
                navigator.navigate(BookshelfEditScreenDestination(bookshelfType = bookshelfType))

            override fun onComplete() {
                if (!navigator.popBackStack(
                        BookshelfSelectionScreenDestination,
                        true
                    )
                ) {
                    navigator.popBackStack()
                }
            }

            override fun onFileClick(file: File) {
                when (file) {
                    is Book -> onBookClick(file)
                    is Folder -> navigator.navigate(
                        BookshelfFolderScreenDestination(file.bookshelfId, file.path, null)
                    )
                }
            }
        })
    }
}
