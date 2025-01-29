package com.sorrowblue.comicviewer.feature.search.navigation

import androidx.navigation.NavController
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.search.Search
import com.sorrowblue.comicviewer.feature.search.SearchFolder
import com.sorrowblue.comicviewer.feature.search.SearchFolderScreenNavigator
import com.sorrowblue.comicviewer.feature.search.SearchScreenNavigator
import org.koin.core.annotation.Singleton

interface SearchNavGraphNavigator {
    fun onBookClick(book: Book)
    fun onFavoriteClick(bookshelfId: BookshelfId, path: String)
    fun onSettingsClick()
}

@Singleton
internal class SearchNavGraphNavigatorImpl(
    override val navController: NavController,
    private val navigator: SearchNavGraphNavigator,
) : SearchScreenNavigator, SearchFolderScreenNavigator {
    override fun navigateUp() {
        navController.navigateUp()
    }

    override fun onFileClick(file: File) {
        when (file) {
            is Book -> navigator.onBookClick(file)
            is Folder -> navController.navigate(SearchFolder(file.bookshelfId, file.path, null))
        }
    }

    override fun onFavoriteClick(bookshelfId: BookshelfId, path: String) {
        navigator.onFavoriteClick(bookshelfId, path)
    }

    override fun onOpenFolderClick(bookshelfId: BookshelfId, parent: String) {
        navController.navigate(SearchFolder(bookshelfId, parent, null))
    }

    override fun onSearchClick(bookshelfId: BookshelfId, path: String) {
        navController.navigate(Search(bookshelfId, path))
    }

    override fun onSettingsClick() {
        navigator.onSettingsClick()
    }
}
