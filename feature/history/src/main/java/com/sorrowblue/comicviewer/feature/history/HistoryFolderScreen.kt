package com.sorrowblue.comicviewer.feature.history

import androidx.compose.runtime.Composable
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.parameters.CodeGenVisibility
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.feature.history.navigation.HistoryGraph
import com.sorrowblue.comicviewer.folder.Folder
import com.sorrowblue.comicviewer.folder.FolderScreenNavigator
import kotlinx.serialization.Serializable

@Serializable
data class HistoryFolder(
    override val bookshelfId: BookshelfId,
    override val path: String,
    override val restorePath: String?,
) : Folder

@Destination<HistoryGraph>(
    navArgs = HistoryFolder::class,
    visibility = CodeGenVisibility.INTERNAL
)
@Composable
internal fun HistoryFolderScreen(
    args: Folder,
    navigator: FolderScreenNavigator,
//    sortTypeResultRecipient: ResultRecipient<SortTypeDialogDestination, SortType>,
) {
//    FolderScreen(
//        route = args,
//        navigator = navigator,
//        sortTypeResultRecipient = sortTypeResultRecipient
//    )
}
