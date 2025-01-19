package com.sorrowblue.comicviewer.feature.search

import androidx.compose.runtime.Composable
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.parameters.CodeGenVisibility
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.feature.search.navigation.SearchGraph
import com.sorrowblue.comicviewer.feature.search.navigation.SearchGraphTransitions
import com.sorrowblue.comicviewer.folder.Folder
import com.sorrowblue.comicviewer.folder.FolderScreenNavigator
import kotlinx.serialization.Serializable

@Serializable
data class SearchFolder(
    override val bookshelfId: BookshelfId,
    override val path: String,
    override val restorePath: String?,
) : Folder

@Destination<SearchGraph>(
    navArgs = SearchFolder::class,
    style = SearchGraphTransitions::class,
    visibility = CodeGenVisibility.INTERNAL
)
@Composable
internal fun SearchFolderScreen(
    args: Folder,
    navigator: FolderScreenNavigator,
//    sortTypeResultRecipient: ResultRecipient<SortTypeDialogDestination, SortType>,
) {
//    FolderScreen(
//        route = args,
//        navigator = navigator,
//        sortTypeResultReceiver = sortTypeResultRecipient
//    )
}
