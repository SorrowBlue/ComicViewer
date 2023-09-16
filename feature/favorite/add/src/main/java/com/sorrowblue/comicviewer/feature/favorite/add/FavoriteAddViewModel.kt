package com.sorrowblue.comicviewer.feature.favorite.add

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.entity.favorite.FavoriteFile
import com.sorrowblue.comicviewer.domain.entity.favorite.FavoriteId
import com.sorrowblue.comicviewer.domain.usecase.favorite.AddFavoriteFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingFavoriteUseCase
import com.sorrowblue.comicviewer.feature.favorite.add.navigation.FavoriteAddArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@HiltViewModel
internal class FavoriteAddViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    pagingFavoriteUseCase: PagingFavoriteUseCase,
    private val addFavoriteFileUseCase: AddFavoriteFileUseCase,
) : ViewModel() {

    private val args = FavoriteAddArgs(savedStateHandle)

    val pagingDataFlow =
        pagingFavoriteUseCase.execute(PagingFavoriteUseCase.Request(PagingConfig(10)))
            .cachedIn(viewModelScope)

    fun add(favoriteId: FavoriteId) {
        viewModelScope.launch {
            addFavoriteFileUseCase.execute(
                AddFavoriteFileUseCase.Request(
                    FavoriteFile(favoriteId, args.bookshelfId, args.path)
                )
            ).collect()
        }
    }
}
