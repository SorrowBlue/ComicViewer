package com.sorrowblue.comicviewer.feature.book.navigation

import androidx.navigation.NavController
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.feature.book.Book
import com.sorrowblue.comicviewer.feature.book.BookScreenNavigator
import com.sorrowblue.comicviewer.feature.book.menu.BookMenu
import org.koin.core.annotation.Factory

interface BookNavGraphNavigator {
    fun onSettingsClick()
}

@Factory
internal class BookNavGraphNavigatorImpl(
    private val navController: NavController,
    private val navigator: BookNavGraphNavigator,
) : BookScreenNavigator {
    override fun navigateUp() {
        navController.navigateUp()
    }

    override fun onSettingsClick() {
        navigator.onSettingsClick()
    }

    override fun onNextBookClick(
        book: com.sorrowblue.comicviewer.domain.model.file.Book,
        collectionId: CollectionId,
    ) {
        navController.navigate(
            Book(
                bookshelfId = book.bookshelfId,
                path = book.path,
                name = book.name,
                collectionId = collectionId
            )
        ) {
            popUpTo<Book> {
                inclusive = true
            }
        }
    }

    override fun onContainerLongClick() {
        navController.navigate(BookMenu)
    }
}
