package com.sorrowblue.comicviewer.favorite.navigation

import androidx.navigation.NavController
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.favorite.Favorite
import com.sorrowblue.comicviewer.favorite.FavoriteFolder
import com.sorrowblue.comicviewer.favorite.FavoriteFolderScreenNavigator
import com.sorrowblue.comicviewer.favorite.FavoriteScreenNavigator
import com.sorrowblue.comicviewer.favorite.list.FavoriteListNavigator
import com.sorrowblue.comicviewer.feature.favorite.edit.FavoriteEdit
import com.sorrowblue.comicviewer.feature.favorite.edit.FavoriteEditScreenNavigator
import org.koin.core.annotation.Factory

interface FavoriteNavGraphNavigator {
    fun onBookClick(book: Book, favoriteId: FavoriteId = FavoriteId())
    fun onNewFavoriteClick()
    fun onSettingsClick()
    fun onFavoriteClick(bookshelfId: BookshelfId, path: String)
    fun onSearchClick(bookshelfId: BookshelfId, path: String)
}

@Factory
internal class FavoriteNavGraphNavigatorImpl(
    override val navController: NavController,
    val navigator: FavoriteNavGraphNavigator,
) : FavoriteListNavigator,
    FavoriteScreenNavigator,
    FavoriteFolderScreenNavigator,
    FavoriteEditScreenNavigator {

    override fun onFavoriteClick(bookshelfId: BookshelfId, path: String) =
        navigator.onFavoriteClick(bookshelfId, path)

    override fun onSearchClick(bookshelfId: BookshelfId, path: String) =
        navigator.onSearchClick(bookshelfId, path)

    override fun onSettingsClick() = navigator.onSettingsClick()

    override fun navigateUp() {
        navController.navigateUp()
    }

    override fun navigateToEdit(favoriteId: FavoriteId) {
        navController.navigate(FavoriteEdit(favoriteId))
    }

    override fun navigateToFavorite(favoriteId: FavoriteId) {
        navController.navigate(Favorite(favoriteId))
    }

    override fun onNewFavoriteClick() {
        navigator.onNewFavoriteClick()
    }

    override fun onOpenFolderClick(file: File) =
        navController.navigate(
            FavoriteFolder(
                file.bookshelfId,
                file.parent,
                file.path
            )
        )

    override fun onEditClick(favoriteId: FavoriteId) {
        navController.navigate(FavoriteEdit(favoriteId))
    }

    override fun onFileClick(file: File, favoriteId: FavoriteId) {
        when (file) {
            is Book -> navigator.onBookClick(file, favoriteId)
            is Folder -> navController.navigate(FavoriteFolder(file.bookshelfId, file.path, null))
        }
    }

    override fun onComplete() {
        navController.popBackStack()
    }

    override fun onFileClick(file: File) {
        when (file) {
            is Book -> navigator.onBookClick(file)
            is Folder -> navController.navigate(FavoriteFolder(file.bookshelfId, file.path, null))
        }
    }
}
