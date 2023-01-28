package com.sorrowblue.comicviewer.favorite.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow

@HiltViewModel
internal class FavoriteListViewModel @Inject constructor(
    pagingFavoriteUseCase: PagingFavoriteUseCase
) : ViewModel() {

    val isEmptyDataFlow = MutableStateFlow(false)

    val pagingDataFlow =
        pagingFavoriteUseCase.execute(PagingFavoriteUseCase.Request(PagingConfig(20)))
            .cachedIn(viewModelScope)
}
