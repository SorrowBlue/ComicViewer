package com.sorrowblue.comicviewer.feature.readlater

import androidx.compose.runtime.Composable
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.parameters.CodeGenVisibility
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.feature.readlater.navigation.ReadLaterGraph
import com.sorrowblue.comicviewer.feature.readlater.navigation.ReadLaterGraphTransitions
import com.sorrowblue.comicviewer.folder.Folder
import com.sorrowblue.comicviewer.folder.FolderScreenNavigator
import kotlinx.serialization.Serializable

@Serializable
data class ReadLaterFolder(
    override val bookshelfId: BookshelfId,
    override val path: String,
    override val restorePath: String?,
) : Folder

@Destination<ReadLaterGraph>(
    navArgs = ReadLaterFolder::class,
    style = ReadLaterGraphTransitions::class,
    visibility = CodeGenVisibility.INTERNAL
)
@Composable
internal fun ReadLaterFolderScreen(
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
