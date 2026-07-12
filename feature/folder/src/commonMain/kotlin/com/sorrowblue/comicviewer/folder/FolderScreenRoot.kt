/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.folder

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.folder.sorttype.SortTypeSelectScreenResultKey
import com.sorrowblue.comicviewer.framework.ui.EventEffect
import com.sorrowblue.comicviewer.framework.ui.NavigationResultEffect
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel

@Composable
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
    modifier: Modifier = Modifier,
) {
    val viewModel = assistedMetroViewModel<FolderViewModel, FolderViewModel.Factory> {
        create(bookshelfId, path, restorePath, showSearch)
    }
    val lazyPagingItems = viewModel.pagingFlow.collectAsLazyPagingItems()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val state =
        rememberFolderScreenState(
            lazyPagingItems = lazyPagingItems,
            uiState = uiState,
            onPermissionChange = viewModel::updatePermission,
            onSortClick = viewModel::onSortClick,
            onFolderScopeOnlyClick = viewModel::onFolderScopeOnlyClick,
            onSortTypeSelectScreenResult = viewModel::onSortTypeSelectScreenResult,
            restorePath = restorePath,
        )
    state.scaffoldState.FolderScreen(
        uiState = state.uiState,
        lazyPagingItems = state.lazyPagingItems,
        lazyGridState = state.lazyGridState,
        localNetworkPermissionRequester = state.localNetworkPermissionRequester,
        snackbarHostState = state.snackbarHostState,
        onBackClick = onBackClick,
        onSearchClick = onSearchClick,
        onFileClick = onFileClick,
        onFileInfoClick = onFileInfoClick,
        onSortClick = { state.onSortClick(it) },
        onFolderScopeOnlyClick = { state.onFolderScopeOnlyClick() },
        onSettingsClick = onSettingsClick,
        onRefresh = state::onRefresh,
        modifier = modifier.testTag("FolderScreenRoot"),
    )

    NavigationResultEffect(SortTypeSelectScreenResultKey, state::onSortTypeSelectScreenResult)

    EventEffect(state.events) {
        when (it) {
            FolderScreenEvent.Restore -> onRestoreComplete()
        }
    }
}
