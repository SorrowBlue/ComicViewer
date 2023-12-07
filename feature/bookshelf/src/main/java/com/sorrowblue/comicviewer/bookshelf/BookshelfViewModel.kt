package com.sorrowblue.comicviewer.bookshelf

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.bookshelf.section.BookshelfRemoveDialogUiState
import com.sorrowblue.comicviewer.domain.model.Scan
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.file.IFolder
import com.sorrowblue.comicviewer.domain.usecase.ScanBookshelfUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.RemoveBookshelfUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingBookshelfFolderUseCase
import com.sorrowblue.comicviewer.framework.ui.lifecycle.ComposeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

sealed interface BookshelfUiEvent {

    data class Message(val text: String) : BookshelfUiEvent
}

@HiltViewModel
class BookshelfViewModel @Inject constructor(
    pagingBookshelfFolderUseCase: PagingBookshelfFolderUseCase,
    private val removeBookshelfUseCase: RemoveBookshelfUseCase,
    private val scanBookshelfUseCase: ScanBookshelfUseCase,
) : ComposeViewModel<BookshelfUiEvent>() {

    private val _uiState = MutableStateFlow(BookshelfScreenUiState())
    val uiState = _uiState.asStateFlow()

    fun scan(folder: IFolder) {
        viewModelScope.launch {
            scanBookshelfUseCase.execute(ScanBookshelfUseCase.Request(folder, Scan.ALL)).first()
        }
    }

    val pagingDataFlow =
        pagingBookshelfFolderUseCase.execute(PagingBookshelfFolderUseCase.Request(PagingConfig(20)))
            .cachedIn(viewModelScope)

    fun remove(bookshelf: Bookshelf) {
        val uiState = _uiState.value
        viewModelScope.launch {
            removeBookshelfUseCase.execute(RemoveBookshelfUseCase.Request(bookshelf))
            _uiState.value = uiState.copy(
                removeDialogUiState = BookshelfRemoveDialogUiState.Hide,
            )
            updateUiEvent(BookshelfUiEvent.Message(bookshelf.displayName))
        }
    }

    fun onRemoveClick(bookshelf: Bookshelf) {
        val uiState = _uiState.value
        val displayName = bookshelf.displayName
        _uiState.value =
            uiState.copy(removeDialogUiState = BookshelfRemoveDialogUiState.Show(displayName))
    }

    fun onRemoveDialogDismissRequest() {
        val uiState = _uiState.value
        _uiState.value = uiState.copy(removeDialogUiState = BookshelfRemoveDialogUiState.Hide)
    }
}
