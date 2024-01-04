package com.sorrowblue.comicviewer.bookshelf.navigation

import androidx.navigation.NavController
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.scope.DestinationScopeWithNoDependencies
import com.sorrowblue.comicviewer.bookshelf.BookshelfFolderScreenNavigator
import com.sorrowblue.comicviewer.bookshelf.BookshelfScreenNavigator
import com.sorrowblue.comicviewer.bookshelf.destinations.BookshelfFolderScreenDestination
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfType
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.bookshelf.edit.BookshelfEditScreenNavigator
import com.sorrowblue.comicviewer.feature.bookshelf.edit.destinations.BookshelfEditScreenDestination
import com.sorrowblue.comicviewer.feature.bookshelf.selection.BookshelfSelectionScreenNavigator
import com.sorrowblue.comicviewer.feature.bookshelf.selection.destinations.BookshelfSelectionScreenDestination

fun DestinationScopeWithNoDependencies<*>.bookshelfNavGraphNavigator(navigator: BookshelfNavGraphNavigator) =
    BookshelfNavGraphNavigatorImpl(navigator, navController)

interface BookshelfNavGraphNavigator {
    fun navigateToBook(book: Book)
    fun onFavoriteClick(file: File)
    fun onSearchClick(bookshelfId: BookshelfId, path: String)
    fun onRestoreComplete()
    fun onSettingsClick()
}

class BookshelfNavGraphNavigatorImpl internal constructor(
    navigator: BookshelfNavGraphNavigator,
    private val navController: NavController,
) : BookshelfScreenNavigator,
    BookshelfSelectionScreenNavigator,
    BookshelfEditScreenNavigator,
    BookshelfFolderScreenNavigator,
    BookshelfNavGraphNavigator by navigator {

    override fun navigateUp() {
        navController.navigateUp()
    }

    override fun onFabClick() {
        navController.navigate(BookshelfSelectionScreenDestination)
    }

    override fun onBookshelfClick(bookshelfId: BookshelfId, path: String) {
        navController.navigate(BookshelfFolderScreenDestination(bookshelfId, path, null))
    }

    override fun onEditClick(bookshelfId: BookshelfId) {
        navController.navigate(BookshelfEditScreenDestination(bookshelfId))
    }

    override fun onSourceClick(bookshelfType: BookshelfType) {
        navController.navigate(BookshelfEditScreenDestination(bookshelfType = bookshelfType))
    }

    override fun onComplete() {
        if (!navController.popBackStack(BookshelfSelectionScreenDestination.route, true)) {
            navController.popBackStack()
        }
    }

    override fun onFileClick(file: File) {
        when (file) {
            is Book -> navigateToBook(file)
            is Folder -> navController.navigate(
                BookshelfFolderScreenDestination(file.bookshelfId, file.path, null)
            )
        }
    }
}
