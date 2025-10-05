package com.sorrowblue.comicviewer.feature.bookshelf.navgraph

import androidx.navigation.NavController
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.bookshelf.BookshelfFolder
import com.sorrowblue.comicviewer.feature.bookshelf.BookshelfFolderScreenNavigator
import com.sorrowblue.comicviewer.feature.bookshelf.BookshelfScreenNavigator
import com.sorrowblue.comicviewer.feature.bookshelf.info.delete.BookshelfDelete
import com.sorrowblue.comicviewer.feature.bookshelf.info.notification.NotificationRequest
import com.sorrowblue.comicviewer.feature.bookshelf.info.notification.ScanType
import org.koin.core.annotation.Factory

interface BookshelfNavGraphNavigator {
    fun onBookClick(book: Book)
    fun onSettingsClick()
    fun onCollectionAddClick(bookshelfId: BookshelfId, path: String)
    fun onRestoreComplete()
    fun onSearchClick(bookshelfId: BookshelfId, path: String)
}

@Factory
internal class BookshelfNavGraphNavigatorImpl(
    val navigator: BookshelfNavGraphNavigator,
    override val navController: NavController,
) : BookshelfScreenNavigator,
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

    override fun onCollectionAddClick(bookshelfId: BookshelfId, path: String) {
        navigator.onCollectionAddClick(bookshelfId, path)
    }

    override fun onRestoreComplete() {
        navigator.onRestoreComplete()
    }

    override fun onBookshelfClick(bookshelfId: BookshelfId, path: String) {
        navController.navigate(BookshelfFolder(bookshelfId, path, null))
    }

    override fun notificationRequest(type: ScanType) {
        navController.navigate(NotificationRequest(type))
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
}
