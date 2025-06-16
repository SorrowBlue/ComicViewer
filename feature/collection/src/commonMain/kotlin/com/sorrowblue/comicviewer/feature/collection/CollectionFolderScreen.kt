package com.sorrowblue.comicviewer.feature.collection

import androidx.compose.runtime.Composable
import com.sorrowblue.cmpdestinations.annotation.Destination
import com.sorrowblue.cmpdestinations.result.NavResultReceiver
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import com.sorrowblue.comicviewer.folder.Folder
import com.sorrowblue.comicviewer.folder.FolderScreen
import com.sorrowblue.comicviewer.folder.FolderScreenNavigator
import com.sorrowblue.comicviewer.folder.SortTypeSelect
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject

@Serializable
internal data class CollectionFolder(
    override val bookshelfId: BookshelfId,
    override val path: String,
    override val restorePath: String?,
) : Folder

internal interface CollectionFolderScreenNavigator : FolderScreenNavigator

@Destination<CollectionFolder>
@Composable
internal fun CollectionFolderScreen(
    route: CollectionFolder,
    sortTypeResultReceiver: NavResultReceiver<SortTypeSelect, SortTypeSelect>,
    navigator: CollectionFolderScreenNavigator = koinInject(),
) {
    FolderScreen(
        route = route,
        navigator = navigator,
        sortTypeResultReceiver = sortTypeResultReceiver
    )
}
