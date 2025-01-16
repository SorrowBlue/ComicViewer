package com.sorrowblue.comicviewer.feature.bookshelf.delete

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import org.koin.compose.viewmodel.koinViewModel
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.dataOrNull
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.GetBookshelfInfoUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.UpdateDeletionFlagUseCase
import com.sorrowblue.comicviewer.framework.ui.EventFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@Composable
internal fun rememberBookshelfDeleteScreenState(
    navArgs: BookshelfDeleteScreenArgs,
    scope: CoroutineScope = rememberCoroutineScope(),
    viewModel: BookshelfDeleteViewModel = koinViewModel(),
): BookshelfDeleteScreenState {
    return remember {
        BookshelfDeleteScreenStateImpl(
            bookshelfInfoUseCase = viewModel.bookshelfInfoUseCase,
            scope = scope,
            navArgs = navArgs,
            updateDeletionFlagUseCase = viewModel.updateDeletionFlagUseCase
        )
    }
}

internal sealed interface BookshelfDeleteScreenEvent {
    data object RemoveSuccess : BookshelfDeleteScreenEvent
}

internal interface BookshelfDeleteScreenState {
    val uiState: BookshelfDeleteScreenUiState
    val events: EventFlow<BookshelfDeleteScreenEvent>
    fun onConfirmClick()
}

private class BookshelfDeleteScreenStateImpl(
    bookshelfInfoUseCase: GetBookshelfInfoUseCase,
    private val scope: CoroutineScope,
    private val navArgs: BookshelfDeleteScreenArgs,
    private val updateDeletionFlagUseCase: UpdateDeletionFlagUseCase,
) : BookshelfDeleteScreenState {

    override val events = EventFlow<BookshelfDeleteScreenEvent>()

    override var uiState by mutableStateOf(BookshelfDeleteScreenUiState())
        private set

    init {
        bookshelfInfoUseCase(GetBookshelfInfoUseCase.Request(bookshelfId = navArgs.bookshelfId))
            .onEach {
                uiState = uiState.copy(title = it.dataOrNull()?.bookshelf?.displayName)
            }.launchIn(scope)
    }

    override fun onConfirmClick() {
        uiState = uiState.copy(isProcessing = true)
        scope.launch {
            delay(300)
            when (
                updateDeletionFlagUseCase(
                    UpdateDeletionFlagUseCase.Request(navArgs.bookshelfId, true)
                )
            ) {
                is Resource.Error -> {
                    uiState = uiState.copy(isProcessing = false)
                }

                is Resource.Success -> {
                    events.tryEmit(BookshelfDeleteScreenEvent.RemoveSuccess)
                    uiState = uiState.copy(isProcessing = false)
                }
            }
        }
    }
}
