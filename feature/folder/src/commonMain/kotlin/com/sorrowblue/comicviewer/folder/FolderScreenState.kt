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
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.domain.model.PagingException
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.framework.ui.EventFlow
import com.sorrowblue.comicviewer.framework.ui.paging.indexOf
import com.sorrowblue.comicviewer.framework.ui.paging.isLoading
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel
import kotlin.math.min
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import logcat.asLog
import logcat.logcat

internal sealed interface FolderScreenEvent {
    data object Restore : FolderScreenEvent
}

@Stable
internal interface FolderScreenState {
    val events: EventFlow<FolderScreenEvent>
    val lazyPagingItems: LazyPagingItems<File>
    val lazyGridState: LazyGridState
    val snackbarHostState: SnackbarHostState

    fun onLoadStateChange(lazyPagingItems: LazyPagingItems<File>)

    fun onRefresh()
}

@Composable
internal fun rememberFolderScreenState(
    bookshelfId: BookshelfId,
    path: String,
    restorePath: String?,
    showSearch: Boolean,
    viewModel: FolderViewModel = assistedMetroViewModel<FolderViewModel, FolderViewModel.Factory> {
        create(bookshelfId, path, restorePath, showSearch)
    },
): FolderScreenState {
    val coroutineScope = rememberCoroutineScope()
    val lazyGridState = rememberLazyGridState()
    val snackbarHostState = remember { SnackbarHostState() }
    val isRestoredState = rememberSaveable { mutableStateOf(false) }
    val lazyPagingItems = viewModel.pagingFlow.collectAsLazyPagingItems()
    val events = retain { EventFlow<FolderScreenEvent>() }
    val state = remember(bookshelfId, path, showSearch, restorePath) {
        FolderScreenStateImpl(
            restorePath = restorePath,
            lazyPagingItems = lazyPagingItems,
            lazyGridState = lazyGridState,
            snackbarHostState = snackbarHostState,
            events = events,
            coroutineScope = coroutineScope,
            isRestoredState = isRestoredState,
        )
    }

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(state) {
        snapshotFlow { state.lazyPagingItems.loadState }
            .flowWithLifecycle(lifecycle)
            .collect {
                state.onLoadStateChange(state.lazyPagingItems)
            }
    }

    return state
}

private class FolderScreenStateImpl(
    private val restorePath: String?,
    override val lazyPagingItems: LazyPagingItems<File>,
    override val lazyGridState: LazyGridState,
    override val snackbarHostState: SnackbarHostState,
    override val events: MutableSharedFlow<FolderScreenEvent>,
    private val coroutineScope: CoroutineScope,
    isRestoredState: MutableState<Boolean>,
) : FolderScreenState {

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

    override fun onRefresh() {
        lazyPagingItems.refresh()
    }
}
