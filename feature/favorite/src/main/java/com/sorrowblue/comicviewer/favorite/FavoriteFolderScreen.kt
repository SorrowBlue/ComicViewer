package com.sorrowblue.comicviewer.favorite

import androidx.compose.runtime.Composable
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.parameters.CodeGenVisibility
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.favorite.navigation.FavoriteGraph
import com.sorrowblue.comicviewer.favorite.navigation.FavoriteGraphTransitions
import com.sorrowblue.comicviewer.folder.Folder
import com.sorrowblue.comicviewer.folder.FolderScreenNavigator
import kotlinx.serialization.Serializable

@Serializable
data class FavoriteFolder(
    override val bookshelfId: BookshelfId,
    override val path: String,
    override val restorePath: String?,
) : Folder

@Destination<FavoriteGraph>(
    navArgs = FavoriteFolder::class,
    style = FavoriteGraphTransitions::class,
    visibility = CodeGenVisibility.INTERNAL
)
@Composable
internal fun FavoriteFolderScreen(
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
