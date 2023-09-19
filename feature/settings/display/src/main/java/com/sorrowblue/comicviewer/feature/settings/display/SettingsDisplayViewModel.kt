package com.sorrowblue.comicviewer.feature.settings.display

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.entity.settings.DarkMode
import com.sorrowblue.comicviewer.domain.usecase.settings.LoadSettingsUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageDisplaySettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@HiltViewModel
internal class SettingsDisplayViewModel @Inject constructor(
    private val settingsUseCase: ManageDisplaySettingsUseCase,
    private val loadSettingsUseCase: LoadSettingsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsDisplayScreenUiState())
    val uiState = _uiState.asStateFlow()

    init {
        settingsUseCase.settings.onEach {
            _uiState.value = _uiState.value.copy(darkMode = it.darkMode)
        }.launchIn(viewModelScope)
        loadSettingsUseCase.settings.onEach {
            _uiState.value = _uiState.value.copy(restoreOnLaunch = it.restoreOnLaunch)
        }.launchIn(viewModelScope)
    }

    fun updateDarkMode(newDarkMode: DarkMode) {
        viewModelScope.launch {
            settingsUseCase.edit { it.copy(darkMode = newDarkMode) }
            when (newDarkMode) {
                DarkMode.DEVICE -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                DarkMode.DARK -> AppCompatDelegate.MODE_NIGHT_YES
                DarkMode.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            }.let(AppCompatDelegate::setDefaultNightMode)
        }
    }

    fun onRestoreOnLaunchChange(value: Boolean) {
        viewModelScope.launch {
            loadSettingsUseCase.edit {
                it.copy(restoreOnLaunch = value)
            }
        }
    }
}
