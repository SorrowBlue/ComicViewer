package com.sorrowblue.comicviewer.feature.bookshelf.navgraph

import androidx.navigation.NavController
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfType
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.feature.bookshelf.BookshelfFolder
import com.sorrowblue.comicviewer.feature.bookshelf.BookshelfFolderScreenNavigator
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
import org.koin.core.annotation.Single

@Serializable
@NavGraph(startDestination = Bookshelf::class, transition = BookshelfNavGraphTransitions::class)
data object BookshelfNavGraph {

    @DestinationInGraph<Bookshelf>
    @DestinationInGraph<BookshelfDelete>
    @DestinationInGraph<BookshelfEdit>
    @DestinationInGraph<BookshelfFolder>
    @DestinationInGraph<BookshelfSelection>
    object Include
}

interface BookshelfNavGraphNavigator {
    fun onBookClick(book: Book)
    fun onSettingsClick()
    fun onFavoriteClick(bookshelfId: BookshelfId, path: String)
    fun onRestoreComplete()
    fun onSearchClick(bookshelfId: BookshelfId, path: String)
}

@Single
internal class BookshelfNavGraphNavigatorImpl(
    val navigator: BookshelfNavGraphNavigator,
    override val navController: NavController,
) : BookshelfScreenNavigator,
    BookshelfSelectionNavigator,
    BookshelfEditScreenNavigator,
    BookshelfFolderScreenNavigator {

    override fun onSettingsClick() {
        navigator.onSettingsClick()
    }

    override fun onFileClick(file: File) {
        when (file) {
            is Book -> navigator.onBookClick(file)

            is Folder -> navController.navigate(BookshelfFolder(file.bookshelfId, file.path, null))
        }
    }

    override fun onFavoriteClick(bookshelfId: BookshelfId, path: String) {
        navigator.onFavoriteClick(bookshelfId, path)
    }

    override fun onRestoreComplete() {
        navigator.onRestoreComplete()
    }

    override fun onFabClick() {
        navController.navigate(BookshelfSelection)
    }

    override fun onBookshelfClick(bookshelfId: BookshelfId, path: String) {
        navController.navigate(BookshelfFolder(bookshelfId, path, null))
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

    override fun onSearchClick(bookshelfId: BookshelfId, path: String) {
        navigator.onSearchClick(bookshelfId, path)
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
