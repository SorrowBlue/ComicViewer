package com.sorrowblue.comicviewer.app.navigation

import androidx.navigation.NavController
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.feature.bookshelf.navgraph.BookshelfNavGraphNavigator
import org.koin.core.annotation.Factory

@Factory
internal class ComicViewerAppNavigator(
    private val navController: NavController,
) : BookshelfNavGraphNavigator {
    override fun onBookClick(book: Book) {
        navController.navigate(
            com.sorrowblue.comicviewer.feature.book.Book(
                book.bookshelfId,
                book.path,
                book.name
            )
        )
    }

    override fun onSettingsClick() {
        TODO("Not yet implemented")
    }

    override fun onFavoriteClick(bookshelfId: BookshelfId, path: String) {
        TODO("Not yet implemented")
    }

    override fun onRestoreComplete() {
        TODO("Not yet implemented")
    }

    override fun onSearchClick(bookshelfId: BookshelfId, path: String) {
        TODO("Not yet implemented")
    }
}
