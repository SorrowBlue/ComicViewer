package com.sorrowblue.comicviewer.feature.bookshelf

import androidx.compose.runtime.Composable
import com.sorrowblue.cmpdestinations.annotation.Destination
import com.sorrowblue.cmpdestinations.result.NavResultReceiver
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.folder.Folder
import com.sorrowblue.comicviewer.folder.FolderScreen
import com.sorrowblue.comicviewer.folder.FolderScreenNavigator
import com.sorrowblue.comicviewer.folder.SortTypeSelect
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject

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
    sortTypeResultReceiver: NavResultReceiver<SortTypeSelect, SortTypeSelect>,
    navigator: BookshelfFolderScreenNavigator = koinInject(),
) {
    FolderScreen(
        route = route,
        navigator = navigator,
        sortTypeResultReceiver = sortTypeResultReceiver
    )
}
