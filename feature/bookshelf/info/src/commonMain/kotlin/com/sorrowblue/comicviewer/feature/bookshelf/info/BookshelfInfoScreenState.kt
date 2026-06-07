package com.sorrowblue.comicviewer.feature.bookshelf.info

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

internal interface BookshelfInfoScreenState {
    val uiState: BookshelfInfoSheetUiState
}

@Composable
internal fun rememberBookshelfInfoScreenState(bookshelfId: BookshelfId): BookshelfInfoScreenState {
    val coroutineScope = rememberCoroutineScope()
    val viewModel = assistedMetroViewModel<BookshelfInfoViewModel, BookshelfInfoViewModel.Factory> {
        create(bookshelfId)
    }
    return remember(viewModel) {
        BookshelfInfoScreenStateImpl(
            viewModel = viewModel,
            coroutineScope = coroutineScope,
        )
    }
}

private class BookshelfInfoScreenStateImpl(
    viewModel: BookshelfInfoViewModel,
    coroutineScope: CoroutineScope,
) : BookshelfInfoScreenState {
    override var uiState by mutableStateOf<BookshelfInfoSheetUiState>(
        BookshelfInfoSheetUiState.Loading,
    )
        private set

    init {
        viewModel.bookshelfInfo.onEach {
            uiState = BookshelfInfoSheetUiState.Loaded(it)
        }.launchIn(coroutineScope)
    }
}
