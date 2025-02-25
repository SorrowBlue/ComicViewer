package com.sorrowblue.comicviewer.feature.readlater

import androidx.compose.runtime.Composable
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import com.sorrowblue.comicviewer.folder.Folder
import com.sorrowblue.comicviewer.folder.FolderScreen
import com.sorrowblue.comicviewer.folder.FolderScreenNavigator
import com.sorrowblue.comicviewer.folder.SortTypeSelect
import com.sorrowblue.cmpdestinations.annotation.Destination
import com.sorrowblue.cmpdestinations.result.NavResultReceiver
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject

@Serializable
data class ReadLaterFolder(
    override val bookshelfId: BookshelfId,
    override val path: String,
    override val restorePath: String?,
) : Folder

internal interface ReadLaterFolderScreenNavigator : FolderScreenNavigator

@Destination<ReadLaterFolder>
@Composable
internal fun ReadLaterFolderScreen(
    route: ReadLaterFolder,
    navigator: ReadLaterFolderScreenNavigator = koinInject(),
    sortTypeResultReceiver: NavResultReceiver<SortTypeSelect, SortType>,
) {
    FolderScreen(
        route = route,
        navigator = navigator,
        sortTypeResultReceiver = sortTypeResultReceiver,
    )
}
