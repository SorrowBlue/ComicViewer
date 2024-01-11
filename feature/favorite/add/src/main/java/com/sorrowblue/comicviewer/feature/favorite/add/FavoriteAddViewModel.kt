package com.sorrowblue.comicviewer.feature.favorite.add

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.model.favorite.Favorite
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteFile
import com.sorrowblue.comicviewer.domain.usecase.favorite.AddFavoriteFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.favorite.CreateFavoriteUseCase
import com.sorrowblue.comicviewer.domain.usecase.favorite.RemoveFavoriteFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingFavoriteUseCase
import com.sorrowblue.comicviewer.feature.favorite.add.destinations.FavoriteAddScreenDestination
import com.sorrowblue.comicviewer.feature.favorite.add.navigation.FavoriteAddArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltViewModel
internal class FavoriteAddViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    pagingFavoriteUseCase: PagingFavoriteUseCase,
    private val addFavoriteFileUseCase: AddFavoriteFileUseCase,
    private val removeFavoriteFileUseCase: RemoveFavoriteFileUseCase,
    private val createFavoriteUseCase: CreateFavoriteUseCase,
) : ViewModel() {

    private val args = FavoriteAddScreenDestination.argsFrom(savedStateHandle)

    val pagingDataFlow =
        pagingFavoriteUseCase.execute(
            PagingFavoriteUseCase.Request(
                PagingConfig(10),
                args.bookshelfId,
                args.path
            )
        )
            .cachedIn(viewModelScope)

    fun onCreateClick(name: String, done: () -> Unit) {
        viewModelScope.launch {
            createFavoriteUseCase.execute(CreateFavoriteUseCase.Request(name)).first()
            done()
        }
    }

    fun update(favorite: Favorite) {
        viewModelScope.launch {
            if (favorite.exist) {
                removeFavoriteFileUseCase.execute(
                    RemoveFavoriteFileUseCase.Request(
                        FavoriteFile(favorite.id, args.bookshelfId, args.path)
                    )
                ).collect()
            } else {
                addFavoriteFileUseCase.execute(
                    AddFavoriteFileUseCase.Request(
                        FavoriteFile(favorite.id, args.bookshelfId, args.path)
                    )
                ).collect()
            }
        }
    }
}
