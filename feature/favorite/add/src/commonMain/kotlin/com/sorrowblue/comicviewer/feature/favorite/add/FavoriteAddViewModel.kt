package com.sorrowblue.comicviewer.feature.favorite.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.usecase.favorite.AddFavoriteFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.favorite.RemoveFavoriteFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingFavoriteUseCase
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.InjectedParam

@KoinViewModel
internal class FavoriteAddViewModel(
    @InjectedParam route: FavoriteAdd,
    pagingFavoriteUseCase: PagingFavoriteUseCase,
    val addFavoriteFileUseCase: AddFavoriteFileUseCase,
    val removeFavoriteFileUseCase: RemoveFavoriteFileUseCase,
) : ViewModel() {

    val pagingDataFlow = pagingFavoriteUseCase(
        PagingFavoriteUseCase.Request(PagingConfig(20), route.bookshelfId, route.path)
    ).cachedIn(viewModelScope)

    val recentPagingDataFlow = pagingFavoriteUseCase(
        PagingFavoriteUseCase.Request(
            PagingConfig(pageSize = 10),
            route.bookshelfId,
            route.path,
            true
        )
    ).cachedIn(viewModelScope)
}
