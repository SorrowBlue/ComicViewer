package com.sorrowblue.comicviewer.feature.bookshelf.info

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.dataOrNull
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.GetBookshelfInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach

internal interface BookshelfInfoSheetWrapperState {
    val uiState: BookshelfInfoSheetWrapperUiState
}

@Composable
internal fun rememberBookshelfInfoSheetWrapperState(
    bookshelfId: BookshelfId,
    scope: CoroutineScope = rememberCoroutineScope(),
    viewModel: BookshelfInfoSheetWrapperViewModel = hiltViewModel(),
): BookshelfInfoSheetWrapperState {
    return remember {
        BookshelfInfoSheetWrapperStateImpl(
            bookshelfId = bookshelfId,
            bookshelfInfoUseCase = viewModel.bookshelfInfoUseCase,
            scope = scope
        )
    }
}

private class BookshelfInfoSheetWrapperStateImpl(
    bookshelfId: BookshelfId,
    bookshelfInfoUseCase: GetBookshelfInfoUseCase,
    scope: CoroutineScope,
) : BookshelfInfoSheetWrapperState {

    override var uiState by mutableStateOf<BookshelfInfoSheetWrapperUiState>(
        BookshelfInfoSheetWrapperUiState.Loading
    )
        private set

    init {
        bookshelfInfoUseCase(GetBookshelfInfoUseCase.Request(bookshelfId = bookshelfId)).mapNotNull { it.dataOrNull() }
            .onEach {
                uiState = BookshelfInfoSheetWrapperUiState.Loaded(it)
            }.launchIn(scope)
    }
}

@HiltViewModel
internal class BookshelfInfoSheetWrapperViewModel @Inject constructor(
    val bookshelfInfoUseCase: GetBookshelfInfoUseCase,
) : ViewModel()
