package com.sorrowblue.comicviewer.folder

import androidx.compose.runtime.Composable
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import com.sorrowblue.comicviewer.folder.sorttype.SortTypeSelectScreenResultKey
import com.sorrowblue.comicviewer.framework.ui.EventEffect
import com.sorrowblue.comicviewer.framework.ui.NavigationResultEffect

@Composable
context(context: FolderScreenContext)
fun FolderScreenRoot(
    bookshelfId: BookshelfId,
    path: String,
    restorePath: String?,
    onBackClick: () -> Unit,
    onSearchClick: () -> Unit,
    onFileClick: (File) -> Unit,
    onFileInfoClick: (File) -> Unit,
    onSortClick: (SortType, Boolean) -> Unit,
    onSettingsClick: () -> Unit,
    onRestored: () -> Unit,
) {
    val state =
        rememberFolderScreenState(bookshelfId = bookshelfId, path = path, restorePath = restorePath)
    state.scaffoldState.FolderScreen(
        uiState = state.uiState,
        lazyPagingItems = state.lazyPagingItems,
        lazyGridState = state.lazyGridState,
        onBackClick = onBackClick,
        onSearchClick = onSearchClick,
        onFileClick = onFileClick,
        onFileInfoClick = onFileInfoClick,
        onSortClick = { state.onSortClick(it) },
        onFolderScopeOnlyClick = { state.onFolderScopeOnlyClick() },
        onSettingsClick = onSettingsClick,
        onRefresh = state::onRefresh,
    )

    NavigationResultEffect(SortTypeSelectScreenResultKey, state::onSortTypeSelectScreenResult)

    EventEffect(state.events) {
        when (it) {
            FolderScreenEvent.Restore -> onRestored()
        }
    }
}
