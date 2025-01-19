package com.sorrowblue.comicviewer.feature.bookshelf.navgraph

import androidx.navigation.NavController
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfType
import com.sorrowblue.comicviewer.feature.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.feature.bookshelf.BookshelfScreenNavigator
import com.sorrowblue.comicviewer.feature.bookshelf.edit.BookshelfEdit
import com.sorrowblue.comicviewer.feature.bookshelf.edit.BookshelfEditMode
import com.sorrowblue.comicviewer.feature.bookshelf.edit.BookshelfEditScreenNavigator
import com.sorrowblue.comicviewer.feature.bookshelf.info.delete.BookshelfDelete
import com.sorrowblue.comicviewer.feature.bookshelf.info.notification.NotificationRequest
import com.sorrowblue.comicviewer.feature.bookshelf.info.notification.ScanType
import com.sorrowblue.comicviewer.feature.bookshelf.selection.BookshelfSelection
import com.sorrowblue.comicviewer.feature.bookshelf.selection.BookshelfSelectionNavigator
import com.sorrowblue.comicviewer.framework.annotation.DestinationInGraph
import com.sorrowblue.comicviewer.framework.annotation.NavGraph
import kotlinx.serialization.Serializable
import org.koin.core.annotation.Factory

@Serializable
data object BookshelfNavGraph

@NavGraph<BookshelfNavGraph>(startDestination = Bookshelf::class)
internal class BookshelfNavigation {
    @DestinationInGraph<Bookshelf>
    @DestinationInGraph<BookshelfDelete>
    @DestinationInGraph<BookshelfEdit>
    @DestinationInGraph<BookshelfSelection>
    companion object
}

@Factory
internal class BookshelfNavGraphNavigator(val navController: NavController) :
    BookshelfScreenNavigator,
    BookshelfSelectionNavigator,
    BookshelfEditScreenNavigator {
    override fun onSettingsClick() {
        TODO("Not yet implemented")
    }

    override fun onFabClick() {
        navController.navigate(BookshelfSelection)
    }

    override fun onBookshelfClick(bookshelfId: BookshelfId, path: String) {
//        navController.navigate(BookshelfEdit(BookshelfEditMode.Edit(bookshelfId)))
    }

    override fun notificationRequest(type: ScanType) {
        navController.navigate(NotificationRequest(type))
    }

    override fun onEditClick(id: BookshelfId) {
        navController.navigate(BookshelfEdit(BookshelfEditMode.Edit(id)))
    }

    override fun onRemoveClick(bookshelfId: BookshelfId) {
        navController.navigate(BookshelfDelete(bookshelfId))
    }

    override fun navigateUp() {
        navController.navigateUp()
    }

    override fun onSourceClick(bookshelfType: BookshelfType) {
        navController.navigate(BookshelfEdit(BookshelfEditMode.Register(bookshelfType))) {
            popUpTo(route = Bookshelf)
        }
    }

    override fun onBack(editMode: BookshelfEditMode) {
        when (editMode) {
            is BookshelfEditMode.Edit ->
                navController.navigateUp()

            is BookshelfEditMode.Register -> {
                navController.navigate(BookshelfSelection) {
                    popUpTo(Bookshelf)
                }
            }
        }
    }

    override fun onComplete() {
        if (!navController.popBackStack(BookshelfSelection, true)) {
            navController.popBackStack()
        }
    }
}

