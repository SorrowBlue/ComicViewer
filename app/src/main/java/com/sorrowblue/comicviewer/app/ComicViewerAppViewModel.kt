package com.sorrowblue.comicviewer.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.usecase.settings.LoadSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltViewModel
class ComicViewerAppViewModel @Inject constructor(
    private val loadSettingsUseCase: LoadSettingsUseCase
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<ComicViewerAppUiState>(ComicViewerAppUiState.Initializing)
    val uiState = _uiState.asStateFlow()

    var shouldKeepOnScreen = true

    init {
        viewModelScope.launch {
            if (!loadSettingsUseCase.settings.first().doneTutorial) {
                _uiState.value = ComicViewerAppUiState.Tutorial
                shouldKeepOnScreen = false
            } else {
                _uiState.value = ComicViewerAppUiState.Main
                shouldKeepOnScreen = false
            }
        }
    }

    fun completeTutorial() {
        viewModelScope.launch {
            loadSettingsUseCase.edit {
                it.copy(doneTutorial = true)
            }
            _uiState.value = ComicViewerAppUiState.Main
        }
    }
}
