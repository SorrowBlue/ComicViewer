package com.sorrowblue.comicviewer.feature.history.navigation

import androidx.navigation.NavController
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.history.ClearAllHistory
import com.sorrowblue.comicviewer.feature.history.HistoryFolder
import com.sorrowblue.comicviewer.feature.history.HistoryScreenNavigator
import com.sorrowblue.comicviewer.folder.FolderScreenNavigator
import org.koin.core.annotation.Factory

interface HistoryNavGraphNavigator {
    fun onBookClick(book: Book)
    fun onCollectionAddClick(bookshelfId: BookshelfId, path: String)
    fun onSettingsClick()
    fun onSearchClick(bookshelfId: BookshelfId, path: String)
}

@Factory
internal class HistoryNavGraphNavigatorImpl(
    override val navController: NavController,
    private val navigator: HistoryNavGraphNavigator,
) : HistoryScreenNavigator, FolderScreenNavigator {

    override fun navigateToBook(book: Book) = navigator.onBookClick(book)

    override fun navigateToFolder(file: File) {
        navController.navigate(HistoryFolder(file.bookshelfId, file.parent, file.path))
    }

    override fun onClearAllClick() {
        navController.navigate(ClearAllHistory)
    }

    override fun onCollectionAddClick(bookshelfId: BookshelfId, path: String) =
        navigator.onCollectionAddClick(bookshelfId, path)

    override fun onSearchClick(bookshelfId: BookshelfId, path: String) =
        navigator.onSearchClick(bookshelfId, path)

    override fun onSettingsClick() = navigator.onSettingsClick()

    override fun onFileClick(file: File) {
        when (file) {
            is Book -> navigator.onBookClick(file)
            is Folder -> navController.navigate(HistoryFolder(file.bookshelfId, file.path, null))
        }
    }

    override fun navigateUp() {
        navController.navigateUp()
    }
}
