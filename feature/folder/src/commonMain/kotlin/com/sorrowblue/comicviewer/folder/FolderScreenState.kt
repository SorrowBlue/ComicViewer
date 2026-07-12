/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.folder

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.sorrowblue.comicviewer.domain.model.PagingException
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import com.sorrowblue.comicviewer.folder.sorttype.SortTypeSelectScreenResult
import com.sorrowblue.comicviewer.framework.permission.localnetwork.LocalNetworkPermissionRequester
import com.sorrowblue.comicviewer.framework.permission.localnetwork.LocalNetworkPermissionState
import com.sorrowblue.comicviewer.framework.permission.localnetwork.rememberLocalNetworkPermissionRequester
import com.sorrowblue.comicviewer.framework.ui.EventFlow
import com.sorrowblue.comicviewer.framework.ui.adaptive.AdaptiveNavigationSuiteScaffoldState
import com.sorrowblue.comicviewer.framework.ui.adaptive.rememberAdaptiveNavigationSuiteScaffoldState
import com.sorrowblue.comicviewer.framework.ui.paging.indexOf
import com.sorrowblue.comicviewer.framework.ui.paging.isLoading
import kotlin.math.min
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import logcat.asLog
import logcat.logcat

internal sealed interface FolderScreenEvent {
    data object Restore : FolderScreenEvent
}

@Stable
internal interface FolderScreenState {
    val scaffoldState: AdaptiveNavigationSuiteScaffoldState
    val events: EventFlow<FolderScreenEvent>
    val lazyPagingItems: LazyPagingItems<File>
    val lazyGridState: LazyGridState
    val uiState: FolderScreenUiState
    val snackbarHostState: SnackbarHostState
    val localNetworkPermissionRequester: LocalNetworkPermissionRequester

    fun onLoadStateChange(lazyPagingItems: LazyPagingItems<File>)

    fun onSortTypeSelectScreenResult(result: SortTypeSelectScreenResult)

    fun onSortClick(sortType: SortType)

    fun onFolderScopeOnlyClick()

    fun onRefresh()
}

@Composable
internal fun rememberFolderScreenState(
    lazyPagingItems: LazyPagingItems<File>,
    uiState: FolderScreenUiState,
    onPermissionChange: (Boolean) -> Unit,
    onSortClick: suspend (SortType) -> Boolean,
    onFolderScopeOnlyClick: suspend () -> Unit,
    onSortTypeSelectScreenResult: suspend (SortTypeSelectScreenResult) -> Boolean,
    restorePath: String?,
): FolderScreenState {
    val coroutineScope = rememberCoroutineScope()
    val lazyGridState = rememberLazyGridState()
    val snackbarHostState = remember { SnackbarHostState() }
    val permissionRequester = rememberLocalNetworkPermissionRequester(true)

    val isRestoredState = rememberSaveable { mutableStateOf(false) }

    val currentOnPermissionChange by rememberUpdatedState(onPermissionChange)

    LaunchedEffect(permissionRequester.state) {
        currentOnPermissionChange(permissionRequester.state == LocalNetworkPermissionState.Granted)
    }

    val state = remember(lazyPagingItems) {
        FolderScreenStateImpl(
            restorePath = restorePath,
            lazyGridState = lazyGridState,
            snackbarHostState = snackbarHostState,
            lazyPagingItems = lazyPagingItems,
            localNetworkPermissionRequester = permissionRequester,
            coroutineScope = coroutineScope,
            onSortClickAction = onSortClick,
            onFolderScopeOnlyClickAction = onFolderScopeOnlyClick,
            onSortTypeSelectScreenResultAction = onSortTypeSelectScreenResult,
            uiStateProvider = { uiState },
            isRestoredState = isRestoredState,
        )
    }.apply {
        scaffoldState = rememberAdaptiveNavigationSuiteScaffoldState()
    }

    LaunchedEffect(state) {
        snapshotFlow { state.lazyPagingItems.loadState }.collect {
            state.onLoadStateChange(state.lazyPagingItems)
        }
    }

    return state
}

private class FolderScreenStateImpl(
    private val restorePath: String?,
    override val lazyGridState: LazyGridState,
    override val snackbarHostState: SnackbarHostState,
    override val lazyPagingItems: LazyPagingItems<File>,
    override val localNetworkPermissionRequester: LocalNetworkPermissionRequester,
    private val coroutineScope: CoroutineScope,
    private val onSortClickAction: suspend (SortType) -> Boolean,
    private val onFolderScopeOnlyClickAction: suspend () -> Unit,
    private val onSortTypeSelectScreenResultAction: suspend (SortTypeSelectScreenResult) -> Boolean,
    private val uiStateProvider: () -> FolderScreenUiState,
    isRestoredState: MutableState<Boolean>,
) : FolderScreenState {
    override lateinit var scaffoldState: AdaptiveNavigationSuiteScaffoldState

    override val events = EventFlow<FolderScreenEvent>()
    override val uiState: FolderScreenUiState get() = uiStateProvider()

    private var isRestored by isRestoredState

    override fun onLoadStateChange(lazyPagingItems: LazyPagingItems<File>) {
        if (!isRestored && restorePath != null && 0 < lazyPagingItems.itemCount) {
            val index = lazyPagingItems.indexOf { it?.path == restorePath }
            if (0 <= index) {
                isRestored = true
                runCatching {
                    coroutineScope.launch {
                        lazyGridState.scrollToItem(min(index, lazyPagingItems.itemCount - 1))
                    }
                }.onFailure {
                    logcat { it.asLog() }
                }
                events.tryEmit(FolderScreenEvent.Restore)
            } else if (!lazyPagingItems.loadState.isLoading) {
                events.tryEmit(FolderScreenEvent.Restore)
            }
        }
        if (lazyPagingItems.loadState.refresh is LoadState.Error) {
            ((lazyPagingItems.loadState.refresh as LoadState.Error).error as? PagingException)?.let {
                coroutineScope.launch {
                    when (it) {
                        is PagingException.InvalidAuth -> snackbarHostState.showSnackbar(
                            "認証エラー",
                        )

                        is PagingException.InvalidServer -> snackbarHostState.showSnackbar(
                            "サーバーエラー",
                        )

                        is PagingException.NoNetwork -> snackbarHostState.showSnackbar(
                            "ネットワークエラー",
                        )

                        is PagingException.NotFound -> snackbarHostState.showSnackbar(
                            "見つかりませんでした",
                        )
                    }
                }
            }
        }
    }

    override fun onSortClick(sortType: SortType) {
        coroutineScope.launch {
            if (onSortClickAction(sortType)) {
                lazyPagingItems.refresh()
            }
        }
    }

    override fun onFolderScopeOnlyClick() {
        coroutineScope.launch {
            onFolderScopeOnlyClickAction()
        }
    }

    override fun onSortTypeSelectScreenResult(result: SortTypeSelectScreenResult) {
        coroutineScope.launch {
            if (onSortTypeSelectScreenResultAction(result)) {
                lazyPagingItems.refresh()
            }
        }
    }

    override fun onRefresh() {
        lazyPagingItems.refresh()
    }
}
