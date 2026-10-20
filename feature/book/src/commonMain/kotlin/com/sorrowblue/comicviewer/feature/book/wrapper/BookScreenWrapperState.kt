/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.book.wrapper

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.feature.book.BookScreenUiState
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

internal interface BookScreenWrapperState {
    val uiState: BookScreenUiState
}

@Composable
internal fun rememberBookScreenWrapperState(
    bookshelfId: BookshelfId,
    path: String,
    name: String,
    collectionId: CollectionId,
    viewModel: BookWrapperViewModel =
        assistedMetroViewModel<BookWrapperViewModel, BookWrapperViewModel.Factory> {
            create(bookshelfId, path, name, collectionId)
        },
): BookScreenWrapperState {
    val coroutineScope = rememberCoroutineScope()
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    return remember(lifecycle) {
        BookScreenWrapperStateImpl(
            lifecycle = lifecycle,
            coroutineScope = coroutineScope,
            uiStateFlow = viewModel.uiStateFlow,
        )
    }
}

private class BookScreenWrapperStateImpl(
    lifecycle: Lifecycle,
    coroutineScope: CoroutineScope,
    uiStateFlow: StateFlow<BookScreenUiState>,
) : BookScreenWrapperState {

    override var uiState: BookScreenUiState by mutableStateOf(uiStateFlow.value)
        private set

    init {
        uiStateFlow
            .flowWithLifecycle(lifecycle)
            .onEach {
                uiState = it
            }
            .launchIn(coroutineScope)
    }
}
