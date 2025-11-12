package com.sorrowblue.comicviewer.feature.book

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.usecase.file.GetBookUseCase
import com.sorrowblue.comicviewer.feature.book.section.BookSheetUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

internal interface BookPrepareScreenState {
    val uiState: BookScreenUiState
}

@Composable
context(context: BookScreenContext)
internal fun rememberBookPrepareScreenState(
    bookshelfId: BookshelfId,
    path: String,
    name: String,
    collectionId: CollectionId,
    scope: CoroutineScope = rememberCoroutineScope(),
): BookPrepareScreenState = remember {
    BookPrepareScreenStateImpl(
        bookshelfId = bookshelfId,
        path = path,
        name = name,
        collectionId = collectionId,
        scope = scope,
        getBookUseCase = context.getBookUseCase,
    )
}

private class BookPrepareScreenStateImpl(
    bookshelfId: BookshelfId,
    path: String,
    name: String,
    collectionId: CollectionId,
    scope: CoroutineScope,
    getBookUseCase: GetBookUseCase,
) : BookPrepareScreenState {
    override var uiState: BookScreenUiState by mutableStateOf(BookScreenUiState.Loading(name))
        private set

    init {
        getBookUseCase(GetBookUseCase.Request(bookshelfId, path))
            .onEach {
                uiState = when (it) {
                    is Resource.Success ->
                        BookScreenUiState.Loaded(
                            book = it.data,
                            collectionId = collectionId,
                            bookSheetUiState = BookSheetUiState(it.data),
                        )

                    is Resource.Error -> when (it.error) {
                        GetBookUseCase.Error.NotFound ->
                            BookScreenUiState.Error(name)

                        GetBookUseCase.Error.ReportedSystemError ->
                            BookScreenUiState.Error(name)
                    }
                }
            }.launchIn(scope)
    }
}
