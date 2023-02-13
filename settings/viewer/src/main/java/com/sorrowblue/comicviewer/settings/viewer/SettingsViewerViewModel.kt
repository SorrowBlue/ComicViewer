package com.sorrowblue.comicviewer.settings.viewer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.entity.settings.ViewerSettings
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageViewerSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
internal class SettingsViewerViewModel @Inject constructor(
    private val settingsUseCase: ManageViewerSettingsUseCase,
) : ViewModel() {

    val settings = settingsUseCase.settings

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

    fun updateBindingDirection(newValue: ViewerSettings.BindingDirection) {
        viewModelScope.launch {
            settingsUseCase.edit { it.copy(bindingDirection = newValue) }
        }
    }
}
