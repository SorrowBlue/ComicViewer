package com.sorrowblue.comicviewer.feature.bookshelf.info

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.GetBookshelfInfoUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.UpdateDeletionFlagUseCase
import com.sorrowblue.comicviewer.framework.navigation.NavResult
import com.sorrowblue.comicviewer.framework.ui.EventFlow
import comicviewer.feature.bookshelf.info.generated.resources.Res
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_label_undo
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_msg_remove
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import logcat.logcat
import org.jetbrains.compose.resources.getString

@Composable
internal actual fun rememberBookshelfInfoSheetState(
    bookshelfId: BookshelfId,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope,
    viewModel: BookshelfInfoSheetViewModel,
): BookshelfInfoSheetState {
    val stateImpl = remember(bookshelfId) {
        BookshelfInfoSheetStateImpl(
            bookshelfId = bookshelfId,
            bookshelfInfoUseCase = viewModel.bookshelfInfoUseCase,
            updateDeletionFlagUseCase = viewModel.updateDeletionFlagUseCase,
            snackbarHostState = snackbarHostState,
            scope = scope,
        )
    }
    return stateImpl
}

private class BookshelfInfoSheetStateImpl(
    bookshelfInfoUseCase: GetBookshelfInfoUseCase,
    private val updateDeletionFlagUseCase: UpdateDeletionFlagUseCase,
    private val bookshelfId: BookshelfId,
    private val snackbarHostState: SnackbarHostState,
    private val scope: CoroutineScope,
) : BookshelfInfoSheetState {

    override var uiState by mutableStateOf<BookshelfInfoSheetUiState>(
        BookshelfInfoSheetUiState.Loading
    )
        private set

    init {
        bookshelfInfoUseCase(GetBookshelfInfoUseCase.Request(bookshelfId = bookshelfId))
            .onEach {
                uiState = when (it) {
                    is Resource.Error -> BookshelfInfoSheetUiState.Error
                    is Resource.Success -> BookshelfInfoSheetUiState.Loaded(it.data)
                }
            }.launchIn(scope)
    }

    override val events = EventFlow<BookshelfInfoSheetStateEvent>()

    override fun onAction(action: BookshelfInfoSheetAction) {
        when (action) {
            BookshelfInfoSheetAction.Close -> events.tryEmit(BookshelfInfoSheetStateEvent.Back)
            BookshelfInfoSheetAction.Edit ->
                events.tryEmit(BookshelfInfoSheetStateEvent.Edit(bookshelfId))

            BookshelfInfoSheetAction.Remove ->
                events.tryEmit(BookshelfInfoSheetStateEvent.Remove(bookshelfId))
        }
    }

    override fun onRemoveResult(result: NavResult<Boolean>) {
        logcat { "onRemoveResult(result: $result)" }
        when (result) {
            NavResult.Canceled -> Unit
            is NavResult.Value -> {
                if (result.value) {
                    showDeletionCompleteSnackbar()
                    events.tryEmit(BookshelfInfoSheetStateEvent.Back)
                }
            }
        }
    }

    private fun showDeletionCompleteSnackbar() {
        scope.launch {
            val snackbarResult = snackbarHostState.showSnackbar(
                message = getString(Res.string.bookshelf_info_msg_remove),
                actionLabel = getString(Res.string.bookshelf_info_label_undo),
                duration = SnackbarDuration.Long
            )
            when (snackbarResult) {
                SnackbarResult.Dismissed -> Unit
                SnackbarResult.ActionPerformed -> {
                    updateDeletionFlagUseCase(UpdateDeletionFlagUseCase.Request(bookshelfId, false))
                }
            }
        }
    }
}
