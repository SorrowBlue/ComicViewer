package com.sorrowblue.comicviewer.feature.favorite.add

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavType
import androidx.navigation.toRoute
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.usecase.favorite.AddFavoriteFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.favorite.RemoveFavoriteFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingFavoriteUseCase
import kotlin.reflect.KType
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
internal class FavoriteAddViewModel(
    savedStateHandle: SavedStateHandle,
    typeMap: Map<KType, NavType<*>>,
    pagingFavoriteUseCase: PagingFavoriteUseCase,
    val addFavoriteFileUseCase: AddFavoriteFileUseCase,
    val removeFavoriteFileUseCase: RemoveFavoriteFileUseCase,
) : ViewModel() {

    private val route = savedStateHandle.toRoute<FavoriteAdd>(typeMap)

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
