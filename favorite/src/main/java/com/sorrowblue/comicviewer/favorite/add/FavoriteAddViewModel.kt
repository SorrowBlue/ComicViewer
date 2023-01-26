package com.sorrowblue.comicviewer.favorite.add

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.entity.Favorite
import com.sorrowblue.comicviewer.domain.entity.FavoriteBook
import com.sorrowblue.comicviewer.domain.entity.server.ServerId
import com.sorrowblue.comicviewer.domain.usecase.AddFavoriteBookUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingFavoriteUseCase
import com.sorrowblue.comicviewer.framework.ui.navigation.SupportSafeArgs
import com.sorrowblue.comicviewer.framework.ui.navigation.navArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@HiltViewModel
internal class FavoriteAddViewModel @Inject constructor(
    private val pagingFavoriteUseCase: PagingFavoriteUseCase,
    private val addFavoriteBookUseCase: AddFavoriteBookUseCase,
    override val savedStateHandle: SavedStateHandle
) : ViewModel(), SupportSafeArgs {
    private val args: FavoriteAddFragmentArgs by navArgs()

    val pagingDataFlow =
        pagingFavoriteUseCase.execute(PagingFavoriteUseCase.Request(PagingConfig(10)))
            .cachedIn(viewModelScope)

    fun add(favorite: Favorite) {
        viewModelScope.launch {
            addFavoriteBookUseCase.execute(
                AddFavoriteBookUseCase.Request(
                    FavoriteBook(
                        favorite.id,
                        ServerId(args.serverId),
                        args.filePath
                    )
                )
            ).collect()
        }
    }
}
