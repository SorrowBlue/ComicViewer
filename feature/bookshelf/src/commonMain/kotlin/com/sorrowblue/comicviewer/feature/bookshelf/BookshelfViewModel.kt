package com.sorrowblue.comicviewer.feature.bookshelf

import androidx.lifecycle.ViewModel
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingBookshelfFolderUseCase
import kotlinx.coroutines.flow.Flow
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
internal class BookshelfViewModel(pagingBookshelfFolderUseCase: PagingBookshelfFolderUseCase) :
    ViewModel() {
    val pagingDataFlow: Flow<PagingData<BookshelfFolder>> =
        pagingBookshelfFolderUseCase(PagingBookshelfFolderUseCase.Request(PagingConfig(20)))
}
