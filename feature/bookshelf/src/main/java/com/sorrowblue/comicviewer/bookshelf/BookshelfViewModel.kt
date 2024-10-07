package com.sorrowblue.comicviewer.bookshelf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingBookshelfBookUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingBookshelfFolderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

@HiltViewModel
internal class BookshelfViewModel @Inject constructor(
    pagingBookshelfFolderUseCase: PagingBookshelfFolderUseCase,
    private val pagingBookshelfBookUseCase: PagingBookshelfBookUseCase,
) : ViewModel() {

    val pagingDataFlow =
        pagingBookshelfFolderUseCase.execute(PagingBookshelfFolderUseCase.Request(PagingConfig(20)))
            .cachedIn(viewModelScope)

    private var bookshelfThumbnailPagingDataFlow: Pair<BookshelfId, Flow<PagingData<BookThumbnail>>>? =
        null

    fun pagingDataFlow2(bookshelfId: BookshelfId): Flow<PagingData<BookThumbnail>> =
        if (bookshelfThumbnailPagingDataFlow?.first == bookshelfId) {
            bookshelfThumbnailPagingDataFlow?.second!!
        } else {
            pagingBookshelfBookUseCase.execute(
                PagingBookshelfBookUseCase.Request(
                    bookshelfId,
                    PagingConfig(10)
                )
            ).cachedIn(viewModelScope).also {
                bookshelfThumbnailPagingDataFlow = bookshelfId to it
            }
        }
}
