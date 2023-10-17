package com.sorrowblue.comicviewer.feature.favorite.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.usecase.favorite.CreateFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltViewModel
internal class FavoriteCreateViewModel @Inject constructor(
    private val createFavoriteUseCase: CreateFavoriteUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoriteCreateScreenUiState())
    val uiState = _uiState.asStateFlow()

    private val _uiEvent =
        MutableSharedFlow<FavoriteCreateScreenUiEvent>(0, 2, BufferOverflow.DROP_OLDEST)
    val uiEvent = _uiEvent.asSharedFlow()

    fun onNameChange(name: String) {
        _uiState.value = _uiState.value.copy(name = name)
    }

    fun onCreateClick() {
        val name = _uiState.value.name.ifBlank {
            _uiState.value = _uiState.value.copy(error = R.string.favorite_create_message_error)
            return
        }
        _uiState.value = _uiState.value.copy(name = name)
        viewModelScope.launch {
            createFavoriteUseCase.execute(CreateFavoriteUseCase.Request(name)).first()
            _uiEvent.emit(FavoriteCreateScreenUiEvent.DoneCreate)
        }
    }
}
