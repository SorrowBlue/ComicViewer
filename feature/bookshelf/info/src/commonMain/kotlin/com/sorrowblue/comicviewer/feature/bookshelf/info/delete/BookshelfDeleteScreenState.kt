package com.sorrowblue.comicviewer.feature.bookshelf.info.delete

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.dataOrNull
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.GetBookshelfInfoUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.UpdateDeletionFlagUseCase
import com.sorrowblue.comicviewer.framework.ui.EventFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun rememberBookshelfDeleteScreenState(
    bookshelfId: BookshelfId,
    scope: CoroutineScope = rememberCoroutineScope(),
    viewModel: BookshelfDeleteViewModel = koinViewModel(),
): BookshelfDeleteScreenState {
    return remember {
        BookshelfDeleteScreenStateImpl(
            bookshelfInfoUseCase = viewModel.bookshelfInfoUseCase,
            scope = scope,
            bookshelfId = bookshelfId,
            updateDeletionFlagUseCase = viewModel.updateDeletionFlagUseCase
        )
    }
}

internal interface BookshelfDeleteScreenState {
    val uiState: BookshelfDeleteScreenUiState
    fun onConfirmClick(done: () -> Unit)
}

private class BookshelfDeleteScreenStateImpl(
    bookshelfInfoUseCase: GetBookshelfInfoUseCase,
    private val scope: CoroutineScope,
    private val bookshelfId: BookshelfId,
    private val updateDeletionFlagUseCase: UpdateDeletionFlagUseCase,
) : BookshelfDeleteScreenState {

    override var uiState by mutableStateOf(BookshelfDeleteScreenUiState())
        private set

    init {
        bookshelfInfoUseCase(GetBookshelfInfoUseCase.Request(bookshelfId = bookshelfId))
            .onEach {
                uiState = uiState.copy(title = it.dataOrNull()?.bookshelf?.displayName)
            }.launchIn(scope)
    }

    override fun onConfirmClick(done: () -> Unit) {
        uiState = uiState.copy(isProcessing = true)
        scope.launch {
            delay(300)
            when (
                updateDeletionFlagUseCase(
                    UpdateDeletionFlagUseCase.Request(bookshelfId, true)
                )
            ) {
                is Resource.Error -> {
                    uiState = uiState.copy(isProcessing = false)
                }

                is Resource.Success -> {
                    done()
                    uiState = uiState.copy(isProcessing = false)
                }
            }
        }
    }
}
