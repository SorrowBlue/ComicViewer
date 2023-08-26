package com.sorrowblue.comicviewer.favorite.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.usecase.favorite.CreateFavoriteUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltViewModel
internal class FavoriteListViewModel @Inject constructor(
    pagingFavoriteUseCase: PagingFavoriteUseCase,
    private val createFavoriteUseCase: CreateFavoriteUseCase
) : ViewModel() {

    val pagingDataFlow =
        pagingFavoriteUseCase.execute(PagingFavoriteUseCase.Request(PagingConfig(20)))
            .cachedIn(viewModelScope)

    private val _uiState =
        MutableStateFlow(FavoriteListScreenUiState(FavoriteCreateDialogUiState.Hide))

    val uiState = _uiState.asStateFlow()

    fun closeCreateDialog() {
        val uiState = _uiState.value
        _uiState.value =
            uiState.copy(favoriteCreateDialogUiState = FavoriteCreateDialogUiState.Hide)
    }

    fun onChangeText(text: String) {
        val uiState = _uiState.value
        _uiState.value =
            uiState.copy(favoriteCreateDialogUiState = FavoriteCreateDialogUiState.Show(text))
    }

    fun create() {
        val uiState = _uiState.value.favoriteCreateDialogUiState
        val name = if (uiState is FavoriteCreateDialogUiState.Show) {
            uiState.name
        } else {
            return
        }

        viewModelScope.launch {
            createFavoriteUseCase.execute(CreateFavoriteUseCase.Request(name)).first()
            closeCreateDialog()
        }
    }

    fun showCreateDialog() {
        val uiState = _uiState.value
        _uiState.value =
            uiState.copy(favoriteCreateDialogUiState = FavoriteCreateDialogUiState.Show(""))
    }
}
