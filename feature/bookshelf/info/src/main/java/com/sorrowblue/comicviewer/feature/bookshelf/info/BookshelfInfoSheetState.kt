package com.sorrowblue.comicviewer.feature.bookshelf.info

import android.content.Context
import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.result.NavResult
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.GetBookshelfInfoUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.UpdateDeletionFlagUseCase
import com.sorrowblue.comicviewer.framework.ui.EventFlow
import com.sorrowblue.comicviewer.framework.ui.adaptive.LocalCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import logcat.logcat

internal sealed interface BookshelfInfoSheetStateEvent {
    data object Back : BookshelfInfoSheetStateEvent
    data class Edit(val id: BookshelfId) : BookshelfInfoSheetStateEvent
    data class Remove(val bookshelfId: BookshelfId) : BookshelfInfoSheetStateEvent
}

internal interface BookshelfInfoSheetState {
    val uiState: BookshelfInfoSheetUiState
    val events: EventFlow<BookshelfInfoSheetStateEvent>

    fun onAction(action: BookshelfInfoSheetAction)
    fun onRemoveResult(result: NavResult<Boolean>)
}

@Composable
internal fun rememberBookshelfInfoSheetState(
    bookshelfId: BookshelfId,
    snackbarHostState: SnackbarHostState,
    context: Context = LocalContext.current,
    scope: CoroutineScope = LocalCoroutineScope.current,
    viewModel: BookshelfInfoSheetViewModel = hiltViewModel(),
): BookshelfInfoSheetState {
    val stateImpl = remember(bookshelfId) {
        BookshelfInfoSheetStateImpl(
            bookshelfId = bookshelfId,
            bookshelfInfoUseCase = viewModel.bookshelfInfoUseCase,
            updateDeletionFlagUseCase = viewModel.updateDeletionFlagUseCase,
            context = context,
            snackbarHostState = snackbarHostState,
            scope = scope,
        )
    }
    stateImpl.intentLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { }
    return stateImpl
}

private class BookshelfInfoSheetStateImpl(
    bookshelfInfoUseCase: GetBookshelfInfoUseCase,
    private val updateDeletionFlagUseCase: UpdateDeletionFlagUseCase,
    private val bookshelfId: BookshelfId,
    private val context: Context,
    private val snackbarHostState: SnackbarHostState,
    private val scope: CoroutineScope,
) : BookshelfInfoSheetState, IntentLauncher {

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

    override lateinit var intentLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>

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
                message = context.getString(R.string.bookshelf_info_msg_remove),
                actionLabel = context.getString(R.string.bookshelf_info_label_undo),
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
