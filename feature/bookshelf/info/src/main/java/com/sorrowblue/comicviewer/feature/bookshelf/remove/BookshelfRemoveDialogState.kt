package com.sorrowblue.comicviewer.feature.bookshelf.remove

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.sorrowblue.comicviewer.domain.model.dataOrNull
import com.sorrowblue.comicviewer.domain.model.fold
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.GetBookshelfInfoUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.RemoveBookshelfUseCase
import com.sorrowblue.comicviewer.framework.ui.ScreenStateEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

internal sealed interface BookshelfRemoveDialogEvent {
    data object RemoveSuccess : BookshelfRemoveDialogEvent
}

internal interface BookshelfRemoveDialogState : ScreenStateEvent<BookshelfRemoveDialogEvent> {
    val uiState: BookshelfRemoveDialogUiState
    fun remove()
}

@Composable
internal fun rememberBookshelfRemoveDialogState(
    navArgs: BookshelfRemoveDialogArgs,
    scope: CoroutineScope = rememberCoroutineScope(),
    viewModel: BookshelfRemoveDialogViewModel = hiltViewModel(),
): BookshelfRemoveDialogState {
    return remember {
        BookshelfRemoveDialogStateImpl(
            bookshelfInfoUseCase = viewModel.bookshelfInfoUseCase,
            scope = scope,
            navArgs = navArgs,
            removeBookshelfUseCase = viewModel.removeBookshelfUseCase
        )
    }
}

private class BookshelfRemoveDialogStateImpl(
    bookshelfInfoUseCase: GetBookshelfInfoUseCase,
    override val scope: CoroutineScope,
    private val navArgs: BookshelfRemoveDialogArgs,
    private val removeBookshelfUseCase: RemoveBookshelfUseCase,
) : BookshelfRemoveDialogState {

    override val event = MutableSharedFlow<BookshelfRemoveDialogEvent>()

    override var uiState by mutableStateOf(BookshelfRemoveDialogUiState())
        private set

    init {
        bookshelfInfoUseCase(GetBookshelfInfoUseCase.Request(bookshelfId = navArgs.bookshelfId))
            .mapNotNull { it.dataOrNull() }
            .onEach {
                uiState = uiState.copy(title = it.bookshelf.displayName)
            }.launchIn(scope)
    }

    override fun remove() {
        scope.launch {
            uiState = uiState.copy(isProcessing = true)
            delay(300)
            removeBookshelfUseCase(RemoveBookshelfUseCase.Request(navArgs.bookshelfId)).fold(
                onSuccess = {
                    sendEvent(BookshelfRemoveDialogEvent.RemoveSuccess)
                },
                onError = {
                    uiState = uiState.copy(isProcessing = false)
                }
            )
        }
    }
}
