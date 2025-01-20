package com.sorrowblue.comicviewer.favorite.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.usecase.favorite.DeleteFavoriteUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingFavoriteUseCase
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
internal class FavoriteListViewModel(
    pagingFavoriteUseCase: PagingFavoriteUseCase,
    val deleteFavoriteUseCase: DeleteFavoriteUseCase,
) : ViewModel() {

    val pagingDataFlow = pagingFavoriteUseCase(
        PagingFavoriteUseCase.Request(PagingConfig(20), BookshelfId(), "")
    ).cachedIn(viewModelScope)
}
