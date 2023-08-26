package com.sorrowblue.comicviewer.favorite.add

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.Base64.decodeFromBase64
import com.sorrowblue.comicviewer.domain.entity.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.entity.favorite.Favorite
import com.sorrowblue.comicviewer.domain.entity.favorite.FavoriteFile
import com.sorrowblue.comicviewer.domain.entity.favorite.FavoriteId
import com.sorrowblue.comicviewer.domain.usecase.favorite.AddFavoriteFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingFavoriteUseCase
import com.sorrowblue.comicviewer.favorite.navigation.FavoriteAddArgs
import com.sorrowblue.comicviewer.framework.ui.navigation.SupportSafeArgs
import com.sorrowblue.comicviewer.framework.ui.navigation.navArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@HiltViewModel
internal class FavoriteAddViewModel @Inject constructor(
    pagingFavoriteUseCase: PagingFavoriteUseCase,
    private val addFavoriteFileUseCase: AddFavoriteFileUseCase,
    savedStateHandle: SavedStateHandle
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
