package com.sorrowblue.comicviewer.feature.bookshelf.info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingBookshelfBookUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

@HiltViewModel
internal class BookshelfInfoSheetViewModel @Inject constructor(
    private val pagingBookshelfBookUseCase: PagingBookshelfBookUseCase,
) : ViewModel() {

    private var bookshelfThumbnailPagingDataFlow: Pair<BookshelfId, Flow<PagingData<BookThumbnail>>>? =
        null

    fun pagingDataFlow(id: BookshelfId) =
        if (bookshelfThumbnailPagingDataFlow?.first == id) {
            bookshelfThumbnailPagingDataFlow?.second!!
        } else {
            pagingBookshelfBookUseCase.execute(
                PagingBookshelfBookUseCase.Request(id, PagingConfig(10))
            ).cachedIn(viewModelScope).also {
                bookshelfThumbnailPagingDataFlow = id to it
            }
        }
}
