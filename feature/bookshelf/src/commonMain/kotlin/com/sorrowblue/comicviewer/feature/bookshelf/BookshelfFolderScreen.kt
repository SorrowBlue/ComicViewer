package com.sorrowblue.comicviewer.feature.bookshelf

import androidx.compose.runtime.Composable
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import com.sorrowblue.comicviewer.folder.Folder
import com.sorrowblue.comicviewer.folder.FolderScreen
import com.sorrowblue.comicviewer.folder.FolderScreenNavigator
import com.sorrowblue.comicviewer.folder.SortTypeSelect
import com.sorrowblue.comicviewer.framework.annotation.Destination
import com.sorrowblue.comicviewer.framework.navigation.NavResultReceiver
import kotlinx.serialization.Serializable

@Serializable
data class BookshelfFolder(
    override val bookshelfId: BookshelfId,
    override val path: String,
    override val restorePath: String?,
) : Folder

internal interface BookshelfFolderScreenNavigator : FolderScreenNavigator

@Destination<BookshelfFolder>
@Composable
internal fun BookshelfFolderScreen(
    route: BookshelfFolder,
    navigator: BookshelfFolderScreenNavigator,
    sortTypeResultReceiver: NavResultReceiver<SortTypeSelect, SortType>,
) {
    FolderScreen(
        route = route,
        navigator = navigator,
        sortTypeResultReceiver = sortTypeResultReceiver
    )
}
