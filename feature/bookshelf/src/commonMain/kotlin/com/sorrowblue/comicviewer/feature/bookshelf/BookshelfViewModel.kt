package com.sorrowblue.comicviewer.feature.bookshelf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.PagingBookshelfFolderUseCase
import kotlinx.coroutines.flow.Flow
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
internal class BookshelfViewModel(
    pagingBookshelfFolderUseCase: PagingBookshelfFolderUseCase,
) : ViewModel() {

    val pagingDataFlow: Flow<PagingData<BookshelfFolder>> =
        pagingBookshelfFolderUseCase(PagingBookshelfFolderUseCase.Request(PagingConfig(20)))
            .cachedIn(viewModelScope)
}
