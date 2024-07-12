package com.sorrowblue.comicviewer.favorite.navigation

import androidx.compose.runtime.Composable
import com.ramcosta.composedestinations.navigation.DependenciesContainerBuilder
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.navigation.navGraph
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.favorite.FavoriteScreenNavigator
import com.sorrowblue.comicviewer.favorite.list.FavoriteListNavigator
import com.sorrowblue.comicviewer.feature.favorite.NavGraphs
import com.sorrowblue.comicviewer.feature.favorite.destinations.FavoriteFolderScreenDestination
import com.sorrowblue.comicviewer.feature.favorite.destinations.FavoriteScreenDestination
import com.sorrowblue.comicviewer.feature.favorite.edit.FavoriteEditScreenNavigator
import com.sorrowblue.comicviewer.feature.favorite.edit.destinations.FavoriteEditScreenDestination
import com.sorrowblue.comicviewer.folder.FolderScreenNavigator

@Composable
fun DependenciesContainerBuilder<*>.FavoriteGraphDependencies(
    onBookClick: (Book, FavoriteId?) -> Unit,
    onFavoriteClick: (File) -> Unit,
    onSearchClick: (BookshelfId, String) -> Unit,
    onSettingsClick: () -> Unit,
) {
    navGraph(NavGraphs.favorite) {
        dependency(object :
            FavoriteListNavigator,
            FavoriteScreenNavigator,
            FolderScreenNavigator,
            FavoriteEditScreenNavigator {
            override val navigator get() = destinationsNavigator

            override fun onFavoriteClick(file: File) = onFavoriteClick(file)

            override fun onSearchClick(bookshelfId: BookshelfId, path: String) =
                onSearchClick(bookshelfId, path)

            override fun onSettingsClick() = onSettingsClick()

            override fun navigateUp() {
                navigator.navigateUp()
            }

            override fun onFavoriteClick(favoriteId: FavoriteId) {
                navigator.navigate(FavoriteScreenDestination(favoriteId))
            }

            override fun onOpenFolderClick(file: File) {
                navigator.navigate(
                    FavoriteFolderScreenDestination(
                        file.bookshelfId,
                        file.parent,
                        null
                    )
                )
            }

            override fun onEditClick(favoriteId: FavoriteId) {
                navigator.navigate(FavoriteEditScreenDestination(favoriteId))
            }

            override fun onFileClick(file: File, favoriteId: FavoriteId) {
                when (file) {
                    is Book -> onBookClick(file, favoriteId)
                    is Folder -> navigator.navigate(
                        FavoriteFolderScreenDestination(file.bookshelfId, file.path, null)
                    )
                }
            }

            override fun onComplete() {
                navController.popBackStack()
            }

            override fun onFileClick(file: File) {
                when (file) {
                    is Book -> onBookClick(file, null)
                    is Folder -> navigator.navigate(
                        FavoriteFolderScreenDestination(file.bookshelfId, file.path, null)
                    )
                }
            }
        })
    }
}
