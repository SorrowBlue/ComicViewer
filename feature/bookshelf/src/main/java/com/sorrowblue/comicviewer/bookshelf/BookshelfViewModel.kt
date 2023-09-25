package com.sorrowblue.comicviewer.bookshelf

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.bookshelf.section.BookshelfRemoveDialogUiState
import com.sorrowblue.comicviewer.domain.entity.BookshelfFolder
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.RemoveBookshelfUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingBookshelfFolderUseCase
import com.sorrowblue.comicviewer.framework.ui.lifecycle.ComposeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface BookshelfUiEvent {

    data class Message(val text: String) : BookshelfUiEvent
}

@HiltViewModel
class BookshelfViewModel @Inject constructor(
    pagingBookshelfFolderUseCase: PagingBookshelfFolderUseCase,
    private val removeBookshelfUseCase: RemoveBookshelfUseCase,
) : ComposeViewModel<BookshelfUiEvent>() {

    private val _uiState = MutableStateFlow(BookshelfScreenUiState())
    val uiState = _uiState.asStateFlow()

    val pagingDataFlow =
        pagingBookshelfFolderUseCase.execute(PagingBookshelfFolderUseCase.Request(PagingConfig(20)))
            .cachedIn(viewModelScope)

    fun remove() {
        val uiState = _uiState.value
        val bookshelf = uiState.bookshelfInfoSheetUiState.bookshelfFolder?.bookshelf ?: return
        viewModelScope.launch {
            removeBookshelfUseCase.execute(RemoveBookshelfUseCase.Request(bookshelf))
            _uiState.value = uiState.copy(
                removeDialogUiState = BookshelfRemoveDialogUiState.Hide,
                bookshelfInfoSheetUiState = uiState.bookshelfInfoSheetUiState.copy(
                    isVisibleSheet = false,
                    bookshelfFolder = null
                )
            )
            updateUiEvent(BookshelfUiEvent.Message("${bookshelf.displayName} を削除しました。"))
        }
    }

    fun onBookshelfLongClick(bookshelfFolder: BookshelfFolder) {
        val uiState = _uiState.value
        _uiState.value = uiState.copy(
            bookshelfInfoSheetUiState = uiState.bookshelfInfoSheetUiState.copy(
                isVisibleSheet = true,
                bookshelfFolder = bookshelfFolder
            )
        )
    }

    fun onRemoveClick() {
        val uiState = _uiState.value
        val displayName =
            uiState.bookshelfInfoSheetUiState.bookshelfFolder?.bookshelf?.displayName ?: return
        _uiState.value =
            uiState.copy(removeDialogUiState = BookshelfRemoveDialogUiState.Show(displayName))
    }

    fun close() {
        val uiState = _uiState.value
        _uiState.value = uiState.copy(
            bookshelfInfoSheetUiState = uiState.bookshelfInfoSheetUiState.copy(
                isVisibleSheet = false,
            )
        )
    }

    fun onRemoveDialogDismissRequest() {
        val uiState = _uiState.value
        _uiState.value = uiState.copy(removeDialogUiState = BookshelfRemoveDialogUiState.Hide)
    }
}
