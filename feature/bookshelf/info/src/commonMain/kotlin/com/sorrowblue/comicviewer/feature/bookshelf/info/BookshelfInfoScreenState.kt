package com.sorrowblue.comicviewer.feature.bookshelf.info

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.GetBookshelfInfoUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.UpdateDeletionFlagUseCase
import com.sorrowblue.comicviewer.framework.ui.GlobalSnackbarState
import com.sorrowblue.comicviewer.framework.ui.LocalGlobalSnackbarState
import comicviewer.feature.bookshelf.info.generated.resources.Res
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_label_undo
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_msg_remove
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

internal interface BookshelfInfoScreenState {
    val uiState: BookshelfInfoSheetUiState
}

@Composable
context(context: BookshelfInfoScreenContext)
internal fun rememberBookshelfInfoScreenState(bookshelfId: BookshelfId): BookshelfInfoScreenState {
    val scope = rememberCoroutineScope()
    val globalSnackbarState = LocalGlobalSnackbarState.current
    val stateImpl = remember(bookshelfId) {
        BookshelfInfoScreenStateImpl(
            bookshelfId = bookshelfId,
            bookshelfInfoUseCase = context.bookshelfInfoUseCase,
            updateDeletionFlagUseCase = context.updateDeletionFlagUseCase,
            globalSnackbarState = globalSnackbarState,
            scope = scope,
        )
    }
    return stateImpl
}


private class BookshelfInfoScreenStateImpl(
    bookshelfInfoUseCase: GetBookshelfInfoUseCase,
    private val updateDeletionFlagUseCase: UpdateDeletionFlagUseCase,
    private val bookshelfId: BookshelfId,
    private val globalSnackbarState: GlobalSnackbarState,
    private val scope: CoroutineScope,
) : BookshelfInfoScreenState {

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

    private fun showDeletionCompleteSnackbar() {
        scope.launch {
            globalSnackbarState.showSnackbar(
                message = getString(Res.string.bookshelf_info_msg_remove),
                actionLabel = getString(Res.string.bookshelf_info_label_undo),
                duration = SnackbarDuration.Long
            ) {
                when (it) {
                    SnackbarResult.Dismissed -> Unit
                    SnackbarResult.ActionPerformed -> {
                        updateDeletionFlagUseCase(
                            UpdateDeletionFlagUseCase.Request(
                                bookshelfId,
                                false
                            )
                        )
                    }
                }
            }
        }
    }
}
