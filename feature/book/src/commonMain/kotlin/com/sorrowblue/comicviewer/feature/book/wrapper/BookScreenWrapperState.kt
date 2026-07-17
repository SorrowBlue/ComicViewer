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
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.settings.ViewerSettings
import com.sorrowblue.comicviewer.feature.book.BookScreenUiState
import com.sorrowblue.comicviewer.feature.book.section.BookSheetUiState
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.first
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
            create(bookshelfId, path, collectionId)
        },
): BookScreenWrapperState {
    val coroutineScope = rememberCoroutineScope()
    return remember {
        BookScreenWrapperStateImpl(
            name = name,
            collectionId = collectionId,
            coroutineScope = coroutineScope,
            bookFlow = viewModel.bookFlow,
            viewerSettingsFlow = viewModel.viewerSettingsFlow,
        )
    }
}

private class BookScreenWrapperStateImpl(
    name: String,
    collectionId: CollectionId,
    coroutineScope: CoroutineScope,
    bookFlow: SharedFlow<Book?>,
    viewerSettingsFlow: SharedFlow<ViewerSettings>,
) : BookScreenWrapperState {

    override var uiState: BookScreenUiState by mutableStateOf(BookScreenUiState.Loading(name))
        private set

    init {
        bookFlow.onEach { book ->
            uiState = if (book != null) {
                BookScreenUiState.Loaded(
                    book = book,
                    collectionId = collectionId,
                    bookSheetUiState = BookSheetUiState(book),
                    alwaysOpenFromFirstPage = viewerSettingsFlow.first().alwaysOpenFromFirstPage,
                )
            } else {
                BookScreenUiState.Error(name)
            }
        }.launchIn(coroutineScope)
    }
}
