package com.sorrowblue.comicviewer.favorite.navigation

import androidx.navigation.NavController
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.scope.DestinationScopeWithNoDependencies
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.favorite.FavoriteScreenNavigator
import com.sorrowblue.comicviewer.favorite.destinations.FavoriteFolderScreenDestination
import com.sorrowblue.comicviewer.favorite.destinations.FavoriteScreenDestination
import com.sorrowblue.comicviewer.favorite.list.FavoriteListNavigator
import com.sorrowblue.comicviewer.feature.favorite.edit.FavoriteEditScreenNavigator
import com.sorrowblue.comicviewer.feature.favorite.edit.destinations.FavoriteEditScreenDestination
import com.sorrowblue.comicviewer.folder.FolderScreenNavigator

fun DestinationScopeWithNoDependencies<*>.favoriteNavGraphNavigator(navigator: FavoriteNavGraphNavigator) =
    FavoriteNavGraphNavigatorImpl(navigator, navController)

interface FavoriteNavGraphNavigator {
    fun navigateToBook(book: Book)
    fun navigateToBook(book: Book, favoriteId: FavoriteId)
    fun onFavoriteClick(file: File)
    fun onSearchClick(bookshelfId: BookshelfId, path: String)
    fun onSettingsClick()
}

class FavoriteNavGraphNavigatorImpl internal constructor(
    navigator: FavoriteNavGraphNavigator,
    private val navController: NavController,
) : FavoriteListNavigator,
    FavoriteScreenNavigator,
    FolderScreenNavigator,
    FavoriteEditScreenNavigator,
    FavoriteNavGraphNavigator by navigator {

    override fun navigateUp() {
        navController.navigateUp()
    }

    override fun onFavoriteClick(favoriteId: FavoriteId) {
        navController.navigate(FavoriteScreenDestination(favoriteId))
    }

    override fun onOpenFolderClick(file: File) {
        navController.navigate(FavoriteFolderScreenDestination(file.bookshelfId, file.parent, null))
    }

    override fun onEditClick(favoriteId: FavoriteId) {
        navController.navigate(FavoriteEditScreenDestination(favoriteId))
    }

    override fun onFileClick(file: File, favoriteId: FavoriteId) {
        when (file) {
            is Book -> navigateToBook(file, favoriteId)
            is Folder -> navController.navigate(
                FavoriteFolderScreenDestination(file.bookshelfId, file.path, null)
            )
        }
    }

    override fun onComplete() {
        navController.popBackStack()
    }

    override fun onFileClick(file: File) {
        when (file) {
            is Book -> navigateToBook(file)
            is Folder -> navController.navigate(
                FavoriteFolderScreenDestination(file.bookshelfId, file.path, null)
            )
        }
    }
}
