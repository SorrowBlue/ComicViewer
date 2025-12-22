package com.sorrowblue.comicviewer.folder

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.folder.sorttype.SortTypeSelectScreenResultKey
import com.sorrowblue.comicviewer.framework.ui.EventEffect
import com.sorrowblue.comicviewer.framework.ui.NavigationResultEffect

@Composable
context(context: FolderScreenContext)
fun FolderScreenRoot(
    bookshelfId: BookshelfId,
    path: String,
    restorePath: String?,
    showSearch: Boolean,
    onBackClick: () -> Unit,
    onSearchClick: () -> Unit,
    onFileClick: (File) -> Unit,
    onFileInfoClick: (File) -> Unit,
    onSettingsClick: () -> Unit,
    onRestoreComplete: () -> Unit,
) {
    val state =
        rememberFolderScreenState(
            bookshelfId = bookshelfId,
            path = path,
            restorePath = restorePath,
            showSearch = showSearch,
        )
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
        modifier = Modifier.testTag("FolderScreenRoot"),
    )

    NavigationResultEffect(SortTypeSelectScreenResultKey, state::onSortTypeSelectScreenResult)

    EventEffect(state.events) {
        when (it) {
            FolderScreenEvent.Restore -> onRestoreComplete()
        }
    }
}
