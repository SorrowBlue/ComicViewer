package com.sorrowblue.comicviewer.feature.bookshelf.info

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.GetBookshelfInfoUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

internal interface BookshelfInfoScreenState {
    val uiState: BookshelfInfoSheetUiState
}

@Composable
context(context: BookshelfInfoScreenContext)
internal fun rememberBookshelfInfoScreenState(bookshelfId: BookshelfId): BookshelfInfoScreenState {
    val coroutineScope = rememberCoroutineScope()
    return remember(bookshelfId) {
        BookshelfInfoScreenStateImpl(
            bookshelfId = bookshelfId,
            getBookshelfInfoUseCase = context.bookshelfInfoUseCase,
            coroutineScope = coroutineScope,
        )
    }
}

private class BookshelfInfoScreenStateImpl(
    private val bookshelfId: BookshelfId,
    getBookshelfInfoUseCase: GetBookshelfInfoUseCase,
    coroutineScope: CoroutineScope,
) : BookshelfInfoScreenState {
    override var uiState by mutableStateOf<BookshelfInfoSheetUiState>(
        BookshelfInfoSheetUiState.Loading,
    )
        private set

    init {
        getBookshelfInfoUseCase(GetBookshelfInfoUseCase.Request(bookshelfId = bookshelfId))
            .onEach {
                uiState = when (it) {
                    is Resource.Error -> BookshelfInfoSheetUiState.Error
                    is Resource.Success -> BookshelfInfoSheetUiState.Loaded(it.data)
                }
            }.launchIn(coroutineScope)
    }
}
