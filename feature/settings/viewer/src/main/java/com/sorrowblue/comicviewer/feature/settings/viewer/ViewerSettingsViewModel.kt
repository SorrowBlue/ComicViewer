package com.sorrowblue.comicviewer.feature.settings.viewer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageViewerSettingsUseCase
import org.koin.android.annotation.KoinViewModel

import kotlinx.coroutines.launch
import logcat.logcat

@KoinViewModel
internal class ViewerSettingsViewModel(
    private val settingsUseCase: ManageViewerSettingsUseCase,
) : ViewModel() {

    val settings = settingsUseCase.settings

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
