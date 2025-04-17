package com.sorrowblue.comicviewer.app.navigation

import androidx.navigation.NavController
import com.sorrowblue.comicviewer.domain.model.SearchCondition
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.feature.book.navigation.BookNavGraphNavigator
import com.sorrowblue.comicviewer.feature.bookshelf.navgraph.BookshelfNavGraphNavigator
import com.sorrowblue.comicviewer.feature.collection.navigation.CollectionNavGraphNavigator
import com.sorrowblue.comicviewer.feature.history.navigation.HistoryNavGraphNavigator
import com.sorrowblue.comicviewer.feature.readlater.navigation.ReadLaterNavGraphNavigator
import com.sorrowblue.comicviewer.feature.search.Search
import com.sorrowblue.comicviewer.feature.search.navigation.SearchNavGraphNavigator
import com.sorrowblue.comicviewer.feature.settings.navigation.SettingsNavGraph
import com.sorrowblue.comicviewer.feature.book.Book as BookRoute

internal class ComicViewerAppNavigator(
    private val onRestoreComplete: () -> Unit,
    private val navController: NavController,
) : BookshelfNavGraphNavigator,
    ReadLaterNavGraphNavigator,
    CollectionNavGraphNavigator,
    SearchNavGraphNavigator,
    HistoryNavGraphNavigator,
    BookNavGraphNavigator {

    override fun onBookClick(book: Book) {
        navController.navigate(BookRoute(book.bookshelfId, book.path, book.name))
    }

    override fun onBookClick(book: Book, collectionId: CollectionId) {
        navController.navigate(
            BookRoute(
                bookshelfId = book.bookshelfId,
                path = book.path,
                name = book.name,
                collectionId = collectionId
            )
        )
    }

    override fun onSettingsClick() {
        navController.navigate(SettingsNavGraph)
    }

    override fun onRestoreComplete() {
        this.onRestoreComplete.invoke()
    }

    override fun onSearchClick(bookshelfId: BookshelfId, path: String) {
        navController.navigate(Search(bookshelfId, path))
    }

    override fun onSmartCollectionClick(
        bookshelfId: BookshelfId,
        searchCondition: SearchCondition,
    ) {
        navController.navigateSmartCollectionCreate(bookshelfId, searchCondition)
    }

    override fun onCollectionAddClick(bookshelfId: BookshelfId, path: String) {
        navController.navigateCollectionAdd(bookshelfId, path)
    }
}
