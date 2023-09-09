package com.sorrowblue.comicviewer.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.usecase.settings.LoadSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@HiltViewModel
class ComicViewerAppViewModel @Inject constructor(
    private val loadSettingsUseCase: LoadSettingsUseCase
) : ViewModel() {

    private val _uiEvents =
        MutableSharedFlow<ComicViewerAppUiEvent>(0, 2, BufferOverflow.DROP_OLDEST)
    val uiEvents = _uiEvents.asSharedFlow()

    var shouldKeepOnScreen = true

    fun initialize() {
        viewModelScope.launch {
            if (!loadSettingsUseCase.settings.first().doneTutorial) {
                _uiEvents.emit(ComicViewerAppUiEvent.StartTutorial)
                shouldKeepOnScreen = false
            } else {
                shouldKeepOnScreen = false
            }
        }
    }

    fun completeTutorial() {
        val oldDoneTutorial = runBlocking { loadSettingsUseCase.settings.first() }.doneTutorial
        _uiEvents.tryEmit(ComicViewerAppUiEvent.CompleteTutorial(!oldDoneTutorial))
        viewModelScope.launch {
            loadSettingsUseCase.edit {
                it.copy(doneTutorial = true)
            }
        }
    }
}
