package com.sorrowblue.comicviewer.favorite

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
data class FavoriteFolder(
    override val bookshelfId: BookshelfId,
    override val path: String,
    override val restorePath: String?,
) : Folder

internal interface FavoriteFolderScreenNavigator : FolderScreenNavigator

@Destination<FavoriteFolder>
@Composable
internal fun FavoriteFolderScreen(
    route: FavoriteFolder,
    navigator: FavoriteFolderScreenNavigator,
    sortTypeResultReceiver: NavResultReceiver<SortTypeSelect, SortType>,
) {
    FolderScreen(
        route = route,
        navigator = navigator,
        sortTypeResultReceiver = sortTypeResultReceiver
    )
}
