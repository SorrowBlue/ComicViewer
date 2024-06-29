package com.sorrowblue.comicviewer.feature.book

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.sorrowblue.comicviewer.domain.usecase.file.GetBookUseCase
import com.sorrowblue.comicviewer.feature.book.section.BookSheetUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
internal fun rememberBookScreenState(
    args: BookArgs,
    scope: CoroutineScope = rememberCoroutineScope(),
    viewModel: BookViewModel = hiltViewModel(),
): BookScreenState = remember {
    BookScreenStateImpl(
        args = args,
        scope = scope,
        viewModel = viewModel
    )
}

@Stable
internal interface BookScreenState {
    val uiState: BookScreenUiState
}

private class BookScreenStateImpl(
    args: BookArgs,
    scope: CoroutineScope,
    private val viewModel: BookViewModel,
) : BookScreenState {

    override var uiState: BookScreenUiState by mutableStateOf(BookScreenUiState.Loading(args.name))
        private set

    init {
        scope.launch {
            val book = viewModel.getBookUseCase
                .execute(GetBookUseCase.Request(args.bookshelfId, args.path))
                .first().dataOrNull
            uiState = if (book == null || book.totalPageCount <= 0) {
                BookScreenUiState.Error(args.name)
            } else {
                BookScreenUiState.Loaded(book, BookSheetUiState(book))
            }
        }
    }
}
