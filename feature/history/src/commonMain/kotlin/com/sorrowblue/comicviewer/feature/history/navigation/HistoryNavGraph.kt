package com.sorrowblue.comicviewer.feature.history.navigation

import androidx.navigation.NavController
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.history.ClearAllHistory
import com.sorrowblue.comicviewer.feature.history.History
import com.sorrowblue.comicviewer.feature.history.HistoryFolder
import com.sorrowblue.comicviewer.feature.history.HistoryScreenNavigator
import com.sorrowblue.comicviewer.folder.FolderScreenNavigator
import com.sorrowblue.comicviewer.framework.annotation.DestinationInGraph
import com.sorrowblue.comicviewer.framework.annotation.NavGraph
import com.sorrowblue.comicviewer.framework.navigation.AppNavController
import kotlinx.serialization.Serializable
import org.koin.core.annotation.Qualifier
import org.koin.core.annotation.Singleton

@Serializable
@NavGraph(startDestination = History::class)
data object HistoryNavGraph {

    @DestinationInGraph<History>
    @DestinationInGraph<HistoryFolder>
    @DestinationInGraph<ClearAllHistory>
    object Include
}

interface HistoryNavGraphNavigator {
    fun onBookClick(book: Book)
    fun onFavoriteClick(bookshelfId: BookshelfId, path: String)
    fun onSettingsClick()
    fun onSearchClick(bookshelfId: BookshelfId, path: String)
}

@Singleton
internal class HistoryNavGraphNavigatorImpl(
    @Qualifier(AppNavController::class) override val navController: NavController,
    private val navigator: HistoryNavGraphNavigator,
) : HistoryScreenNavigator, FolderScreenNavigator {

    override fun navigateToBook(book: Book) = navigator.onBookClick(book)

    override fun navigateToFolder(file: File) {
        navController.navigate(HistoryFolder(file.bookshelfId, file.parent, file.path))
    }

    override fun onClearAllClick() {
        navController.navigate(ClearAllHistory)
    }

    override fun onFavoriteClick(bookshelfId: BookshelfId, path: String) =
        navigator.onFavoriteClick(bookshelfId, path)

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
