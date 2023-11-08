package com.sorrowblue.comicviewer.feature.favorite.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteFile
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.usecase.favorite.GetFavoriteUseCase
import com.sorrowblue.comicviewer.domain.usecase.favorite.RemoveFavoriteFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.favorite.UpdateFavoriteUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingFavoriteFileUseCase
import com.sorrowblue.comicviewer.feature.favorite.edit.navigation.FavoriteEditArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltViewModel
internal class FavoriteEditViewModel @Inject constructor(
    pagingFavoriteFileUseCase: PagingFavoriteFileUseCase,
    private val getFavoriteUseCase: GetFavoriteUseCase,
    private val removeFavoriteFileUseCase: RemoveFavoriteFileUseCase,
    private val updateFavoriteUseCase: UpdateFavoriteUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val args = FavoriteEditArgs(savedStateHandle)

    private val favoriteId = args.favoriteId

    private val _uiState = MutableStateFlow(FavoriteEditScreenUiState())
    val uiState = _uiState.asStateFlow()

    val pagingDataFlow = pagingFavoriteFileUseCase.execute(
        PagingFavoriteFileUseCase.Request(PagingConfig(20), favoriteId)
    ).cachedIn(viewModelScope)

    init {
        viewModelScope.launch {
            getFavoriteUseCase.execute(GetFavoriteUseCase.Request(favoriteId)).first()
        }
    }

    fun removeFile(file: File) {
        val request =
            RemoveFavoriteFileUseCase.Request(FavoriteFile(favoriteId, file.bookshelfId, file.path))

        viewModelScope.launch {
            removeFavoriteFileUseCase.execute(request).collect()
        }
    }

    fun save(done: () -> Unit) {
        viewModelScope.launch {
            val favorite = getFavoriteUseCase.execute(GetFavoriteUseCase.Request(favoriteId))
                .first().dataOrNull ?: return@launch
            updateFavoriteUseCase.execute(UpdateFavoriteUseCase.Request(favorite.copy(name = _uiState.value.name)))
                .collect()
            done()
        }
    }

    fun updateName(name: String) {
        val uiState = _uiState.value
        _uiState.value = uiState.copy(name = name)
    }
}
