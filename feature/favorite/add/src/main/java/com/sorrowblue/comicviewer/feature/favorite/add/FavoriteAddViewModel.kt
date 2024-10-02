package com.sorrowblue.comicviewer.feature.favorite.add

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.usecase.favorite.AddFavoriteFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.favorite.RemoveFavoriteFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingFavoriteUseCase
import com.sorrowblue.comicviewer.feature.favorite.add.destinations.FavoriteAddDialogScreenDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class FavoriteAddViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    pagingFavoriteUseCase: PagingFavoriteUseCase,
    val addFavoriteFileUseCase: AddFavoriteFileUseCase,
    val removeFavoriteFileUseCase: RemoveFavoriteFileUseCase,
) : ViewModel() {

    private val args = FavoriteAddDialogScreenDestination.argsFrom(savedStateHandle)

    val pagingDataFlow = pagingFavoriteUseCase.execute(
        PagingFavoriteUseCase.Request(PagingConfig(20), args.bookshelfId, args.path)
    ).cachedIn(viewModelScope)

    val recentPagingDataFlow = pagingFavoriteUseCase.execute(
        PagingFavoriteUseCase.Request(
            PagingConfig(pageSize = 10),
            args.bookshelfId,
            args.path,
            true
        )
    ).cachedIn(viewModelScope)
}
