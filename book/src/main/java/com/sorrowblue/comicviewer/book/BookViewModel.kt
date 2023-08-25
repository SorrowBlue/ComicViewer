package com.sorrowblue.comicviewer.book

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.book.compose.BookArgs
import com.sorrowblue.comicviewer.domain.entity.favorite.FavoriteFile
import com.sorrowblue.comicviewer.domain.entity.favorite.FavoriteId
import com.sorrowblue.comicviewer.domain.usecase.GetNextComicRel
import com.sorrowblue.comicviewer.domain.usecase.UpdateHistoryUseCase
import com.sorrowblue.comicviewer.domain.usecase.favorite.GetNextFavoriteBookUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.GetBookUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.GetNextBookUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.UpdateLastReadPageUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageViewerSettingsUseCase
import com.sorrowblue.comicviewer.framework.ui.navigation.SupportSafeArgs
import com.sorrowblue.comicviewer.framework.ui.navigation.stateIn
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
internal class BookViewModel @Inject constructor(
    getBookUseCase: GetBookUseCase,
    private val updateHistoryUseCase: UpdateHistoryUseCase,
    private val getNextBookUseCase: GetNextBookUseCase,
    private val getNextFavoriteBookUseCase: GetNextFavoriteBookUseCase,
    private val updateLastReadPageUseCase: UpdateLastReadPageUseCase,
    manageViewerSettingsUseCase: ManageViewerSettingsUseCase,
    override val savedStateHandle: SavedStateHandle,
) : ViewModel(), SupportSafeArgs {

    private val args = BookArgs(savedStateHandle)

    val placeholder = ""
    val favoriteId = FavoriteId(-1)

    val bookFlow =
        getBookUseCase.execute(
            GetBookUseCase.Request(args.bookshelfId, args.path)
        )
            .map { it.dataOrNull }
            .stateIn { null }

    val nextComic =
        bookFlow.filterNotNull().distinctUntilChangedBy { it.path }.flatMapLatest { it ->
            if (0 < favoriteId.value) {
                getNextFavoriteBookUseCase.execute(
                    GetNextFavoriteBookUseCase.Request(
                        FavoriteFile(
                            favoriteId,
                            it.bookshelfId,
                            it.path
                        ), GetNextComicRel.NEXT
                    )
                ).map { it.dataOrNull }
            } else {
                getNextBookUseCase
                    .execute(
                        GetNextBookUseCase.Request(it.bookshelfId, it.path, GetNextComicRel.NEXT)
                    ).map { it.dataOrNull }
            }
        }.stateIn(viewModelScope, SharingStarted.Lazily, null)
    val prevComic =
        bookFlow.filterNotNull().distinctUntilChangedBy { it.path }.flatMapLatest { it ->
            if (0 < favoriteId.value) {
                getNextFavoriteBookUseCase.execute(
                    GetNextFavoriteBookUseCase.Request(
                        FavoriteFile(favoriteId, it.bookshelfId, it.path),
                        GetNextComicRel.PREV
                    )
                ).map { it.dataOrNull }
            } else {
                getNextBookUseCase
                    .execute(
                        GetNextBookUseCase.Request(it.bookshelfId, it.path, GetNextComicRel.PREV)
                    ).map { it.dataOrNull }
            }
        }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    val state = MutableStateFlow(LoadingState.LOADING)

    val transitionName = ""
    val isVisibleUI = MutableStateFlow(false)
    val viewerSettings = manageViewerSettingsUseCase.settings

    val title = bookFlow.mapNotNull { it?.name }.stateIn { "" }

    val pageCount = bookFlow.mapNotNull { it?.totalPageCount }.stateIn { 1 }

    val pageIndex = MutableStateFlow(0)

    init {
//        viewModelScope.launch {
//            bookFlow.filterNotNull().collectLatest {
//                updateHistoryUseCase.execute(
//                    UpdateHistoryUseCase.Request(
//                        History(BookshelfId(args.bookshelfId), it.parent, args.position)
//                    )
//                )
//            }
//        }
    }

    fun updateLastReadPage(index: Int) {
        val book = bookFlow.replayCache.firstOrNull() ?: return
        val request = UpdateLastReadPageUseCase.Request(book.bookshelfId, book.path, index)
        viewModelScope.launch {
            updateLastReadPageUseCase.execute(request)
        }
    }
}
