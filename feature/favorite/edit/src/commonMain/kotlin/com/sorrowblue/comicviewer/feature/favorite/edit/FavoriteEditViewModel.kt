package com.sorrowblue.comicviewer.feature.favorite.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavType
import androidx.navigation.toRoute
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.usecase.favorite.GetFavoriteUseCase
import com.sorrowblue.comicviewer.domain.usecase.favorite.RemoveFavoriteFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.favorite.UpdateFavoriteUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingFavoriteFileUseCase
import kotlin.reflect.KType
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
internal class FavoriteEditViewModel(
    savedStateHandle: SavedStateHandle,
    typeMap: Map<KType, NavType<*>>,
    pagingFavoriteFileUseCase: PagingFavoriteFileUseCase,
    val getFavoriteUseCase: GetFavoriteUseCase,
    val removeFavoriteFileUseCase: RemoveFavoriteFileUseCase,
    val updateFavoriteUseCase: UpdateFavoriteUseCase,
) : ViewModel() {

    private val args = savedStateHandle.toRoute<FavoriteEdit>(typeMap)

    val pagingDataFlow = pagingFavoriteFileUseCase(
        PagingFavoriteFileUseCase.Request(PagingConfig(20), args.favoriteId)
    ).cachedIn(viewModelScope)
}
