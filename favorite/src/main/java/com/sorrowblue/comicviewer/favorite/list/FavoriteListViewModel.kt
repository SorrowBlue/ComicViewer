package com.sorrowblue.comicviewer.favorite.list

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.entity.favorite.Favorite
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingFavoriteUseCase
import com.sorrowblue.comicviewer.framework.ui.fragment.PagingViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class FavoriteListViewModel @Inject constructor(
    pagingFavoriteUseCase: PagingFavoriteUseCase
) : PagingViewModel<Favorite>() {

    override val transitionName = null
    override val pagingDataFlow =
        pagingFavoriteUseCase.execute(PagingFavoriteUseCase.Request(PagingConfig(20)))
            .cachedIn(viewModelScope)
}
