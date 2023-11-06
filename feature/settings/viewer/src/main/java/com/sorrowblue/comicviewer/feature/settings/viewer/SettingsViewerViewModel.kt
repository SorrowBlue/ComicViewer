package com.sorrowblue.comicviewer.feature.settings.viewer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageViewerSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import logcat.logcat

@HiltViewModel
internal class SettingsViewerViewModel @Inject constructor(
    private val settingsUseCase: ManageViewerSettingsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsViewerScreenUiState())
    val uiState = _uiState.asStateFlow()

    init {
        settingsUseCase.settings.onEach {
            it.imageQuality
            _uiState.value = _uiState.value.copy(
                isStatusBarShow = it.showStatusBar,
                isNavigationBarShow = it.showNavigationBar,
                isTurnOnScreen = it.keepOnScreen,
                isCacheImage = false,
                isDisplayFirstPage = false,
                isCutWhitespace = false,
                preloadPages = it.readAheadPageCount.toFloat(),
                imageQuality = it.imageQuality.toFloat(),
                isFixScreenBrightness = it.enableBrightnessControl,
                screenBrightness = it.screenBrightness
            )
        }.launchIn(viewModelScope)
    }

    fun onStatusBarShowChange(value: Boolean) {
        viewModelScope.launch {
            settingsUseCase.edit {
                it.copy(showStatusBar = value)
            }
        }
    }

    fun onNavigationBarShowChange(value: Boolean) {
        viewModelScope.launch {
            settingsUseCase.edit {
                it.copy(showNavigationBar = value)
            }
        }
    }

    fun onTurnOnScreenChange(value: Boolean) {
        viewModelScope.launch {
            settingsUseCase.edit {
                it.copy(keepOnScreen = value)
            }
        }
    }

    fun onCutWhitespaceChange(value: Boolean) {
        logcat { "onCutWhitespaceChange $value" }
    }

    fun onCacheImageChange(value: Boolean) {
        logcat { "onCacheImageChange $value" }
    }

    fun onDisplayFirstPageChange(value: Boolean) {
        logcat { "onDisplayFirstPageChange $value" }
    }

    fun onImageQualityChange(value: Float) {
        viewModelScope.launch {
            settingsUseCase.edit {
                it.copy(imageQuality = value.toInt())
            }
        }
    }

    fun onPreloadPagesChange(value: Float) {
        viewModelScope.launch {
            settingsUseCase.edit {
                it.copy(readAheadPageCount = value.toInt())
            }
        }
    }

    fun onFixScreenBrightnessChange(value: Boolean) {
        viewModelScope.launch {
            settingsUseCase.edit {
                it.copy(enableBrightnessControl = value)
            }
        }
    }

    fun onScreenBrightnessChange(value: Float) {
        viewModelScope.launch {
            settingsUseCase.edit {
                it.copy(screenBrightness = value)
            }
        }
    }
}
