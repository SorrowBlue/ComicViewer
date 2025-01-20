package com.sorrowblue.comicviewer.app.navigation

import androidx.navigation.NavController
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.favorite.navigation.FavoriteNavGraphNavigator
import com.sorrowblue.comicviewer.feature.bookshelf.navgraph.BookshelfNavGraphNavigator
import com.sorrowblue.comicviewer.feature.favorite.add.FavoriteAdd
import com.sorrowblue.comicviewer.feature.favorite.create.FavoriteCreate
import com.sorrowblue.comicviewer.feature.readlater.navigation.ReadLaterNavGraphNavigator
import org.koin.core.annotation.Factory

@Factory
internal class ComicViewerAppNavigator(
    private val navController: NavController,
) : BookshelfNavGraphNavigator, ReadLaterNavGraphNavigator, FavoriteNavGraphNavigator {
    override fun onBookClick(book: Book) {
        navController.navigate(
            com.sorrowblue.comicviewer.feature.book.Book(
                book.bookshelfId,
                book.path,
                book.name
            )
        )
    }

    override fun onBookClick(file: Book, favoriteId: FavoriteId?) {
        TODO("Not yet implemented")
    }

    override fun onNewFavoriteClick() {
        navController.navigate(FavoriteCreate())
    }

    override fun onSettingsClick() {
        TODO("Not yet implemented")
    }

    override fun onFavoriteClick(bookshelfId: BookshelfId, path: String) {
        navController.navigate(FavoriteAdd(bookshelfId, path))
    }

    override fun onRestoreComplete() {
        TODO("Not yet implemented")
    }

    override fun onSearchClick(bookshelfId: BookshelfId, path: String) {
        TODO("Not yet implemented")
    }
}
