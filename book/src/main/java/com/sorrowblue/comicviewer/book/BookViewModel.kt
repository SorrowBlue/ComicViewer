package com.sorrowblue.comicviewer.book

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.book.compose.BookArgs
import com.sorrowblue.comicviewer.book.section.BookPagerUiState
import com.sorrowblue.comicviewer.domain.entity.favorite.FavoriteFile
import com.sorrowblue.comicviewer.domain.entity.file.Book
import com.sorrowblue.comicviewer.domain.usecase.GetNextComicRel
import com.sorrowblue.comicviewer.domain.usecase.favorite.GetNextFavoriteBookUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.GetBookUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.GetNextBookUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.UpdateLastReadPageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltViewModel
internal class BookViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getBookUseCase: GetBookUseCase,
    private val getNextBookUseCase: GetNextBookUseCase,
    private val getNextFavoriteBookUseCase: GetNextFavoriteBookUseCase,
    private val updateLastReadPageUseCase: UpdateLastReadPageUseCase,
) : ViewModel() {

    private val args = BookArgs(savedStateHandle)

    private val _uiState = MutableStateFlow<BookScreenUiState>(BookScreenUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val book = getBookUseCase.execute(GetBookUseCase.Request(args.bookshelfId, args.path))
                .first().dataOrNull ?: return@launch
            val bookPagerUiState = BookPagerUiState(
                book,
                nextBook = nextBook(GetNextComicRel.PREV),
                prevBook = nextBook(GetNextComicRel.NEXT),
            )
            _uiState.value = BookScreenUiState.Loaded(
                book,
                bookPagerUiState,
                true
            )
        }
    }

    private suspend fun nextBook(rel: GetNextComicRel): Book? {
        return if (0 < args.favoriteId.value) {
            getNextFavoriteBookUseCase.execute(
                GetNextFavoriteBookUseCase.Request(
                    FavoriteFile(
                        args.favoriteId,
                        args.bookshelfId,
                        args.path
                    ), rel
                )
            ).first().dataOrNull
        } else {
            getNextBookUseCase
                .execute(GetNextBookUseCase.Request(args.bookshelfId, args.path, rel))
                .first().dataOrNull
        }
    }

    fun toggleTooltip() {
        val uiState = _uiState.value
        _uiState.value = when (uiState) {
            is BookScreenUiState.Loaded -> uiState.copy(isVisibleTooltip = !uiState.isVisibleTooltip)
            BookScreenUiState.Loading -> uiState
        }
    }

    fun updateLastReadPage(index: Int) {
        val request = UpdateLastReadPageUseCase.Request(args.bookshelfId, args.path, index)
        viewModelScope.launch {
            updateLastReadPageUseCase.execute(request)
        }
    }
}
