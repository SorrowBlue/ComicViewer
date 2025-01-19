package com.sorrowblue.comicviewer.feature.bookshelf.info.section

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingBookshelfBookUseCase
import kotlinx.coroutines.flow.Flow
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
internal class BookshelfInfoMainContentsViewModel(
    private val pagingBookshelfBookUseCase: PagingBookshelfBookUseCase,
) : ViewModel() {

    private var bookshelfThumbnailPagingDataFlow: Pair<BookshelfId, Flow<PagingData<BookThumbnail>>>? =
        null

    fun pagingDataFlow(id: BookshelfId) =
        if (bookshelfThumbnailPagingDataFlow?.first == id) {
            bookshelfThumbnailPagingDataFlow?.second!!
        } else {
            pagingBookshelfBookUseCase(
                PagingBookshelfBookUseCase.Request(id, PagingConfig(10))
            ).cachedIn(viewModelScope).also {
                bookshelfThumbnailPagingDataFlow = id to it
            }
        }
}
