package com.sorrowblue.comicviewer.feature.search.navigation

import androidx.compose.runtime.Composable
import com.ramcosta.composedestinations.navigation.DependenciesContainerBuilder
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.navigation.navGraph
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.search.NavGraphs
import com.sorrowblue.comicviewer.feature.search.SearchScreenNavigator
import com.sorrowblue.comicviewer.feature.search.destinations.SearchFolderScreenDestination
import com.sorrowblue.comicviewer.folder.FolderScreenNavigator

@Composable
fun DependenciesContainerBuilder<*>.SearchGraphDependencies(
    onBookClick: (Book) -> Unit,
    onFavoriteClick: (File) -> Unit,
    onSearchClick: (BookshelfId, String) -> Unit,
    onSettingsClick: () -> Unit,
) {
    navGraph(NavGraphs.search) {
        dependency(object : SearchScreenNavigator, FolderScreenNavigator {
            override val navigator get() = destinationsNavigator
            override fun onFavoriteClick(file: File) {
                onFavoriteClick(file)
            }

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
                        SearchFolderScreenDestination(file.bookshelfId, file.path, null)
                    )
                }
            }

            override fun onOpenFolderClick(file: File) {
                navigator.navigate(
                    SearchFolderScreenDestination(file.bookshelfId, file.parent, null)
                )
            }

            override fun onNavigateSettings() {
                onSettingsClick()
            }

            override fun navigateUp() {
                navigator.navigateUp()
            }
        })
    }
}
