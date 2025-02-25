package com.sorrowblue.comicviewer.feature.history

import androidx.compose.runtime.Composable
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import com.sorrowblue.comicviewer.feature.history.navigation.HistoryNavGraph
import com.sorrowblue.comicviewer.folder.Folder
import com.sorrowblue.comicviewer.folder.FolderScreen
import com.sorrowblue.comicviewer.folder.FolderScreenNavigator
import com.sorrowblue.comicviewer.folder.SortTypeSelect
import com.sorrowblue.cmpdestinations.annotation.Destination
import com.sorrowblue.cmpdestinations.result.NavResultReceiver
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject

@Serializable
internal data class HistoryFolder(
    override val bookshelfId: BookshelfId,
    override val path: String,
    override val restorePath: String?,
) : Folder

@Destination<HistoryFolder>()
@Composable
internal fun HistoryFolderScreen(
    route: HistoryFolder,
    navigator: FolderScreenNavigator = koinInject(),
    sortTypeResultReceiver: NavResultReceiver<SortTypeSelect, SortType>,
) {
    FolderScreen(
        route = route,
        navigator = navigator,
        sortTypeResultReceiver = sortTypeResultReceiver,
    )
}
