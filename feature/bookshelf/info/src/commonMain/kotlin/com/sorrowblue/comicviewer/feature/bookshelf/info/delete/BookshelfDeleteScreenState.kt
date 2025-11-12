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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

internal interface BookshelfDeleteScreenState {
    val uiState: BookshelfDeleteScreenUiState

    fun onConfirmClick(done: () -> Unit)
}

@Composable
internal fun rememberBookshelfDeleteScreenState(
    bookshelfId: BookshelfId,
    getBookshelfInfoUseCase: GetBookshelfInfoUseCase,
    updateDeletionFlagUseCase: UpdateDeletionFlagUseCase,
): BookshelfDeleteScreenState {
    val coroutineScope = rememberCoroutineScope()
    return remember {
        BookshelfDeleteScreenStateImpl(
            getBookshelfInfoUseCase = getBookshelfInfoUseCase,
            scope = coroutineScope,
            bookshelfId = bookshelfId,
            updateDeletionFlagUseCase = updateDeletionFlagUseCase,
        )
    }
}

private class BookshelfDeleteScreenStateImpl(
    getBookshelfInfoUseCase: GetBookshelfInfoUseCase,
    private val scope: CoroutineScope,
    private val bookshelfId: BookshelfId,
    private val updateDeletionFlagUseCase: UpdateDeletionFlagUseCase,
) : BookshelfDeleteScreenState {
    override var uiState by mutableStateOf(BookshelfDeleteScreenUiState())
        private set

    init {
        getBookshelfInfoUseCase(GetBookshelfInfoUseCase.Request(bookshelfId = bookshelfId))
            .onEach {
                uiState = uiState.copy(title = it.dataOrNull()?.bookshelf?.displayName)
            }.launchIn(scope)
    }

    override fun onConfirmClick(done: () -> Unit) {
        uiState = uiState.copy(isProcessing = true)
        scope.launch {
            when (
                updateDeletionFlagUseCase(
                    UpdateDeletionFlagUseCase.Request(bookshelfId, true),
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
