package com.sorrowblue.comicviewer.feature.bookshelf.delete

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.dataOrNull
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.GetBookshelfInfoUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.RemoveBookshelfUseCase
import com.sorrowblue.comicviewer.framework.ui.EventFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@Composable
internal fun rememberBookshelfDeleteScreenState(
    navArgs: BookshelfDeleteScreenArgs,
    scope: CoroutineScope = rememberCoroutineScope(),
    viewModel: BookshelfDeleteViewModel = hiltViewModel(),
): BookshelfDeleteScreenState {
    return remember {
        BookshelfDeleteScreenStateImpl(
            bookshelfInfoUseCase = viewModel.bookshelfInfoUseCase,
            scope = scope,
            navArgs = navArgs,
            removeBookshelfUseCase = viewModel.removeBookshelfUseCase
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
    private val removeBookshelfUseCase: RemoveBookshelfUseCase,
) : BookshelfDeleteScreenState {

    override val events = EventFlow<BookshelfDeleteScreenEvent>()

    override var uiState by mutableStateOf(BookshelfDeleteScreenUiState())
        private set

    init {
        bookshelfInfoUseCase(GetBookshelfInfoUseCase.Request(bookshelfId = navArgs.bookshelfId))
            .mapNotNull { it.dataOrNull() }
            .onEach {
                uiState = uiState.copy(title = it.bookshelf.displayName)
            }.launchIn(scope)
    }

    override fun onConfirmClick() {
        scope.launch {
            uiState = uiState.copy(isProcessing = true)
            delay(300)
            when (removeBookshelfUseCase(RemoveBookshelfUseCase.Request(navArgs.bookshelfId))) {
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
