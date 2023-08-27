package com.sorrowblue.comicviewer.feature.favorite.add

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.entity.favorite.FavoriteFile
import com.sorrowblue.comicviewer.domain.entity.favorite.FavoriteId
import com.sorrowblue.comicviewer.domain.usecase.favorite.AddFavoriteFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.favorite.CreateFavoriteUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingFavoriteUseCase
import com.sorrowblue.comicviewer.feature.favorite.add.navigation.FavoriteAddArgs
import com.sorrowblue.comicviewer.feature.favorite.common.section.FavoriteCreateDialogUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltViewModel
internal class FavoriteAddViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    pagingFavoriteUseCase: PagingFavoriteUseCase,
    private val addFavoriteFileUseCase: AddFavoriteFileUseCase,
    private val createFavoriteUseCase: CreateFavoriteUseCase
) : ViewModel() {

    private val args = FavoriteAddArgs(savedStateHandle)

    private val _uiState =
        MutableStateFlow(FavoriteAddScreenUiState(FavoriteCreateDialogUiState.Hide))

    val uiState = _uiState.asStateFlow()

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

    fun showFavoriteCreateDialog() {
        val uiState = _uiState.value
        _uiState.value =
            uiState.copy(favoriteCreateDialogUiState = FavoriteCreateDialogUiState.Show(""))
    }

    fun onFavoriteNameChange(name: String) {
        val uiState = _uiState.value
        _uiState.value =
            uiState.copy(favoriteCreateDialogUiState = FavoriteCreateDialogUiState.Show(name))
    }

    fun dismissFavoriteCreateDialog() {
        val uiState = _uiState.value
        _uiState.value =
            uiState.copy(favoriteCreateDialogUiState = FavoriteCreateDialogUiState.Hide)
    }

    fun createFavorite() {
        val uiState = _uiState.value.favoriteCreateDialogUiState
        val name = if (uiState is FavoriteCreateDialogUiState.Show) {
            uiState.name
        } else {
            return
        }
        viewModelScope.launch {
            createFavoriteUseCase.execute(CreateFavoriteUseCase.Request(name)).first()
            dismissFavoriteCreateDialog()
        }
    }
}
