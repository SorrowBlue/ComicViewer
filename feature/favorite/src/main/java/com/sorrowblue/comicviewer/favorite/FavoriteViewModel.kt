package com.sorrowblue.comicviewer.favorite

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.usecase.favorite.DeleteFavoriteUseCase
import com.sorrowblue.comicviewer.domain.usecase.favorite.GetFavoriteUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingFavoriteFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderDisplaySettingsUseCase
import com.sorrowblue.comicviewer.feature.favorite.destinations.FavoriteScreenDestination
import org.koin.android.annotation.KoinViewModel


@KoinViewModel
internal class FavoriteViewModel(
    savedStateHandle: SavedStateHandle,
    pagingFavoriteFileUseCase: PagingFavoriteFileUseCase,
    val getFavoriteUseCase: GetFavoriteUseCase,
    val displaySettingsUseCase: ManageFolderDisplaySettingsUseCase,
    val deleteFavoriteUseCase: DeleteFavoriteUseCase,
) : ViewModel() {

    private val args = FavoriteScreenDestination.argsFrom(savedStateHandle)

    val pagingDataFlow = pagingFavoriteFileUseCase(
        PagingFavoriteFileUseCase.Request(PagingConfig(20), args.favoriteId)
    ).cachedIn(viewModelScope)
}
