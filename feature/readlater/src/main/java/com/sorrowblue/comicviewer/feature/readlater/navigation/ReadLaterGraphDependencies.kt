package com.sorrowblue.comicviewer.feature.readlater.navigation

import androidx.compose.runtime.Composable
import com.ramcosta.composedestinations.navigation.DependenciesContainerBuilder
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.navigation.navGraph
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.readlater.NavGraphs
import com.sorrowblue.comicviewer.feature.readlater.ReadLaterScreenNavigator
import com.sorrowblue.comicviewer.feature.readlater.destinations.ReadLaterFolderScreenDestination
import com.sorrowblue.comicviewer.folder.FolderScreenNavigator

@Composable
fun DependenciesContainerBuilder<*>.ReadLaterGraphDependencies(
    onBookClick: (Book) -> Unit,
    onFavoriteClick: (BookshelfId, String) -> Unit,
    onSearchClick: (BookshelfId, String) -> Unit,
    onSettingsClick: () -> Unit,
) {
    navGraph(NavGraphs.readLater) {
        dependency(object : ReadLaterScreenNavigator, FolderScreenNavigator {
            override val navigator get() = destinationsNavigator

            override fun onFavoriteClick(bookshelfId: BookshelfId, path: String) =
                onFavoriteClick(bookshelfId, path)

            override fun onSearchClick(bookshelfId: BookshelfId, path: String) {
                onSearchClick(bookshelfId, path)
            }

            override fun onSettingsClick() {
                onSettingsClick()
            }

            override fun onFileClick(file: File) {
                when (file) {
                    is Book -> onBookClick(file)
                    is Folder -> navigator.navigate(
                        ReadLaterFolderScreenDestination(file.bookshelfId, file.path, null)
                    )
                }
            }

            override fun onOpenFolderClick(file: File) {
                navigator.navigate(
                    ReadLaterFolderScreenDestination(file.bookshelfId, file.parent, file.path)
                )
            }

            override fun navigateUp() {
                navigator.navigateUp()
            }
        })
    }
}
