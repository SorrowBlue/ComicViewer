/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.bookshelf.info

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

internal interface BookshelfInfoScreenState {
    val uiState: BookshelfInfoSheetUiState
}

@Composable
internal fun rememberBookshelfInfoScreenState(
    bookshelfId: BookshelfId,
    viewModel: BookshelfInfoViewModel =
        assistedMetroViewModel<BookshelfInfoViewModel, BookshelfInfoViewModel.Factory> {
            create(bookshelfId)
        },
): BookshelfInfoScreenState {
    val coroutineScope = rememberCoroutineScope()
    return remember {
        BookshelfInfoScreenStateImpl(
            coroutineScope = coroutineScope,
            bookshelfInfoFlow = viewModel.bookshelfInfoFlow,
        )
    }
}

private class BookshelfInfoScreenStateImpl(
    coroutineScope: CoroutineScope,
    bookshelfInfoFlow: SharedFlow<BookshelfFolder?>,
) : BookshelfInfoScreenState {
    override var uiState by mutableStateOf<BookshelfInfoSheetUiState>(
        BookshelfInfoSheetUiState.Loading,
    )
        private set

    init {
        bookshelfInfoFlow.onEach {
            uiState = if (it != null) {
                BookshelfInfoSheetUiState.Loaded(it)
            } else {
                BookshelfInfoSheetUiState.Error
            }
        }.launchIn(coroutineScope)
    }
}
