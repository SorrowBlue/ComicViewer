package com.sorrowblue.comicviewer.app.navigation

import androidx.navigation.NavController
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.favorite.navigation.FavoriteNavGraphNavigator
import com.sorrowblue.comicviewer.feature.bookshelf.navgraph.BookshelfNavGraphNavigator
import com.sorrowblue.comicviewer.feature.favorite.add.FavoriteAdd
import com.sorrowblue.comicviewer.feature.favorite.add.FavoriteAddScreenNavigator
import com.sorrowblue.comicviewer.feature.favorite.create.FavoriteCreate
import com.sorrowblue.comicviewer.feature.readlater.navigation.ReadLaterNavGraphNavigator
import com.sorrowblue.comicviewer.feature.search.Search
import com.sorrowblue.comicviewer.feature.search.navigation.SearchNavGraphNavigator
import com.sorrowblue.comicviewer.feature.tutorial.navigation.TutorialNavGraphNavigator
import com.sorrowblue.comicviewer.feature.book.Book as BookRoute

internal class ComicViewerAppNavigator(
    private val navController: NavController,
    private val onRestoreComplete: () -> Unit,
) : BookshelfNavGraphNavigator,
    ReadLaterNavGraphNavigator,
    FavoriteNavGraphNavigator,
    SearchNavGraphNavigator,
    FavoriteAddScreenNavigator,
    TutorialNavGraphNavigator {

    override fun navigateToCreateFavorite(bookshelfId: BookshelfId, path: String) {
        navController.navigate(FavoriteCreate(bookshelfId, path))
    }

    override fun navigateUp() {
        navController.navigateUp()
    }

    override fun onBookClick(book: Book) {
        navController.navigate(BookRoute(book.bookshelfId, book.path, book.name))
    }

    override fun onBookClick(book: Book, favoriteId: FavoriteId) {
        navController.navigate(BookRoute(book.bookshelfId, book.path, book.name, favoriteId))
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
        this.onRestoreComplete.invoke()
    }

    override fun onSearchClick(bookshelfId: BookshelfId, path: String) {
        navController.navigate(Search(bookshelfId, path))
    }

    override fun onCompleteTutorial() {
        navController.navigateUp()
    }
}
