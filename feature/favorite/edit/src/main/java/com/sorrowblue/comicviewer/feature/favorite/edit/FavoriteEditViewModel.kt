package com.sorrowblue.comicviewer.feature.favorite.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.usecase.favorite.GetFavoriteUseCase
import com.sorrowblue.comicviewer.domain.usecase.favorite.RemoveFavoriteFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.favorite.UpdateFavoriteUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingFavoriteFileUseCase
import com.sorrowblue.comicviewer.feature.favorite.edit.destinations.FavoriteEditScreenDestination
import org.koin.android.annotation.KoinViewModel


@KoinViewModel
internal class FavoriteEditViewModel(
    savedStateHandle: SavedStateHandle,
    pagingFavoriteFileUseCase: PagingFavoriteFileUseCase,
    val getFavoriteUseCase: GetFavoriteUseCase,
    val removeFavoriteFileUseCase: RemoveFavoriteFileUseCase,
    val updateFavoriteUseCase: UpdateFavoriteUseCase,
) : ViewModel() {

    private val args = FavoriteEditScreenDestination.argsFrom(savedStateHandle)

    val pagingDataFlow = pagingFavoriteFileUseCase(
        PagingFavoriteFileUseCase.Request(PagingConfig(20), args.favoriteId)
    ).cachedIn(viewModelScope)
}
