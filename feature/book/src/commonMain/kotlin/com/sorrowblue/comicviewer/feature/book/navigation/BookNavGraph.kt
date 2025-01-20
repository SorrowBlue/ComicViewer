package com.sorrowblue.comicviewer.feature.book.navigation

import androidx.navigation.NavController
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.feature.book.Book
import com.sorrowblue.comicviewer.feature.book.BookScreenNavigator
import com.sorrowblue.comicviewer.feature.book.menu.BookMenu
import com.sorrowblue.comicviewer.framework.annotation.DestinationInGraph
import com.sorrowblue.comicviewer.framework.annotation.NavGraph
import kotlinx.serialization.Serializable
import org.koin.core.annotation.Factory

@Serializable
data object BookNavGraph

@NavGraph<BookNavGraph>(startDestination = Book::class)
internal class BookNavigation {

    @DestinationInGraph<Book>
    companion object
}

@Factory
internal class BookNavGraphNavigator(val navController: NavController) : BookScreenNavigator {
    override fun navigateUp() {
        navController.navigateUp()
    }

    override fun onSettingsClick() {
        TODO("Not yet implemented")
    }

    override fun onNextBookClick(
        book: com.sorrowblue.comicviewer.domain.model.file.Book,
        favoriteId: FavoriteId,
    ) {
        navController.navigate(Book(book.bookshelfId, book.path, book.name, favoriteId)) {
            popUpTo<Book> {
                inclusive = true
            }
        }
    }

    override fun onContainerLongClick() {
        navController.navigate(BookMenu)
    }
}
