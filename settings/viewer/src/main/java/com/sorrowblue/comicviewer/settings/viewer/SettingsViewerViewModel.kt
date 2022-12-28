package com.sorrowblue.comicviewer.settings.viewer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.usecase.settings.ViewerSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@HiltViewModel
internal class SettingsViewerViewModel @Inject constructor(
    private val settingsUseCase: ViewerSettingsUseCase,
) : ViewModel() {

    val settings =
        settingsUseCase.settings.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            runBlocking { settingsUseCase.settings.first() })

    fun updateStatusBar(newValue: Boolean) {
        viewModelScope.launch {
            settingsUseCase.edit { it.copy(showStatusBar = newValue) }
        }
    }

    fun updateNavigationBar(newValue: Boolean) {
        viewModelScope.launch {
            settingsUseCase.edit { it.copy(showNavigationBar = newValue) }
        }
    }

    fun updateBrightnessLevel(brightness: Float) {
        viewModelScope.launch {
            settingsUseCase.edit { it.copy(screenBrightness = brightness) }
        }
    }

    fun updateBrightnessControl(newValue: Boolean) {
        viewModelScope.launch {
            settingsUseCase.edit { it.copy(enableBrightnessControl = newValue) }
        }
    }

    fun updateKeepOnScreen(newValue: Boolean) {
        viewModelScope.launch {
            settingsUseCase.edit { it.copy(keepOnScreen = newValue) }
        }
    }

    fun updateImageQuality(newValue: Int) {
        viewModelScope.launch {
            settingsUseCase.edit { it.copy(imageQuality = newValue) }
        }
    }
}
