/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.folder

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.dropUnlessResumed
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.framework.permission.localnetwork.LocalNetworkPermissionState
import com.sorrowblue.comicviewer.framework.permission.localnetwork.rememberLocalNetworkPermissionRequester
import com.sorrowblue.comicviewer.framework.ui.EventEffect
import com.sorrowblue.comicviewer.framework.ui.adaptive.rememberAdaptiveNavigationSuiteScaffoldState
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
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scaffoldState = rememberAdaptiveNavigationSuiteScaffoldState()
    val permissionRequester = rememberLocalNetworkPermissionRequester(true)
    val state = rememberFolderScreenState(
        bookshelfId = bookshelfId,
        path = path,
        restorePath = restorePath,
        showSearch = showSearch,
    )
    scaffoldState.FolderScreen(
        uiState = uiState,
        lazyPagingItems = state.lazyPagingItems,
        lazyGridState = state.lazyGridState,
        localNetworkPermissionRequester = permissionRequester,
        snackbarHostState = state.snackbarHostState,
        onBackClick = dropUnlessResumed(block = onBackClick),
        onSearchClick = dropUnlessResumed(block = onSearchClick),
        onFileClick = dropUnlessResumed(block = onFileClick),
        onFileInfoClick = dropUnlessResumed(block = onFileInfoClick),
        onSortClick = viewModel::onSortClick,
        onFolderScopeOnlyClick = viewModel::onFolderScopeOnlyClick,
        onSettingsClick = dropUnlessResumed(block = onSettingsClick),
        onRefresh = state::onRefresh,
        modifier = modifier.testTag("FolderScreenRoot"),
    )

    LaunchedEffect(permissionRequester.state) {
        viewModel.updatePermission(permissionRequester.state == LocalNetworkPermissionState.Granted)
    }
    EventEffect(state.events) {
        when (it) {
            FolderScreenEvent.Restore -> onRestoreComplete()
        }
    }
    EventEffect(viewModel.events) {
        when (it) {
            FolderScreenUiEvent.Reload -> {
                state.onRefresh()
            }
        }
    }
}
