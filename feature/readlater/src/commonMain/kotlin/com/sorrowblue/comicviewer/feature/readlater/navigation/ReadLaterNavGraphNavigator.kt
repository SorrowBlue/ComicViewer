package com.sorrowblue.comicviewer.feature.readlater.navigation

import androidx.navigation.NavController
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.readlater.ReadLaterFolder
import com.sorrowblue.comicviewer.feature.readlater.ReadLaterFolderScreenNavigator
import com.sorrowblue.comicviewer.feature.readlater.ReadLaterScreenNavigator
import org.koin.core.annotation.Factory

interface ReadLaterNavGraphNavigator {
    fun onSearchClick(bookshelfId: BookshelfId, path: String)
    fun onSettingsClick()
    fun onBookClick(book: Book)
    fun onCollectionAddClick(bookshelfId: BookshelfId, path: String)
}

@Factory
internal class ReadLaterNavGraphNavigatorImpl(
    override val navController: NavController,
    private val navigator: ReadLaterNavGraphNavigator,
) : ReadLaterScreenNavigator, ReadLaterFolderScreenNavigator {
    override fun navigateUp() {
        navController.navigateUp()
    }

    override fun onSearchClick(bookshelfId: BookshelfId, path: String) {
        navigator.onSearchClick(bookshelfId, path)
    }

    override fun onSettingsClick() {
        navigator.onSettingsClick()
    }

    override fun onFileClick(file: File) {
        when (file) {
            is Book -> navigator.onBookClick(file)
            is Folder -> navController.navigate(ReadLaterFolder(file.bookshelfId, file.path, null))
        }
    }

    override fun onCollectionAddClick(bookshelfId: BookshelfId, path: String) {
        navigator.onCollectionAddClick(bookshelfId, path)
    }

    override fun onOpenFolderClick(file: File) {
        navController.navigate(ReadLaterFolder(file.bookshelfId, file.parent, file.path))
    }
}
