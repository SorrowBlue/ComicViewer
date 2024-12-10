package com.sorrowblue.comicviewer.feature.history.navigation

import androidx.compose.runtime.Composable
import com.ramcosta.composedestinations.navigation.DependenciesContainerBuilder
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.navigation.navGraph
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.history.HistoryScreenNavigator
import com.sorrowblue.comicviewer.feature.history.NavGraphs
import com.sorrowblue.comicviewer.feature.history.destinations.ClearAllHistoryDialogDestination
import com.sorrowblue.comicviewer.feature.history.destinations.HistoryFolderScreenDestination
import com.sorrowblue.comicviewer.folder.FolderScreenNavigator

@Composable
fun DependenciesContainerBuilder<*>.HistoryGraphDependencies(
    onBookClick: (Book, FavoriteId?) -> Unit,
    onSearchClick: (BookshelfId, String) -> Unit,
    onSettingsClick: () -> Unit,
    navigateToBook: (Book) -> Unit,
    onFavoriteClick: (BookshelfId, String) -> Unit,
) {
    navGraph(NavGraphs.history) {
        dependency(
            object : HistoryScreenNavigator, FolderScreenNavigator {

                override fun navigateToBook(book: Book) = navigateToBook(book)

                override fun navigateToFolder(file: File) {
                    destinationsNavigator.navigate(
                        HistoryFolderScreenDestination(
                            file.bookshelfId,
                            file.parent,
                            file.path
                        )
                    )
                }

                override fun onClearAllClick() {
                    destinationsNavigator.navigate(ClearAllHistoryDialogDestination)
                }

                override val navigator get() = destinationsNavigator

                override fun onFavoriteClick(bookshelfId: BookshelfId, path: String) =
                    onFavoriteClick(bookshelfId, path)

                override fun onSearchClick(bookshelfId: BookshelfId, path: String) =
                    onSearchClick(bookshelfId, path)

                override fun onSettingsClick() = onSettingsClick()

                override fun onFileClick(file: File) {
                    when (file) {
                        is Book -> onBookClick(file, null)
                        is Folder -> destinationsNavigator.navigate(
                            HistoryFolderScreenDestination(file.bookshelfId, file.path, null)
                        )
                    }
                }

                override fun navigateUp() {
                    destinationsNavigator.navigateUp()
                }
            }
        )
    }
}
