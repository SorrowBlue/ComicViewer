package com.sorrowblue.comicviewer.feature.settings.display

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.model.settings.DarkMode
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageDisplaySettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
internal class DisplaySettingsViewModel @Inject constructor(
    val displaySettingsUseCase: ManageDisplaySettingsUseCase,
) : ViewModel() {

    val displaySettings = displaySettingsUseCase.settings

    fun updateDarkMode(newDarkMode: DarkMode) {
        viewModelScope.launch {
            displaySettingsUseCase.edit { it.copy(darkMode = newDarkMode) }
            when (newDarkMode) {
                DarkMode.DEVICE -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                DarkMode.DARK -> AppCompatDelegate.MODE_NIGHT_YES
                DarkMode.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            }.let(AppCompatDelegate::setDefaultNightMode)
        }
    }

    fun onRestoreOnLaunchChange(value: Boolean) {
        viewModelScope.launch {
            displaySettingsUseCase.edit {
                it.copy(restoreOnLaunch = value)
            }
        }
    }
}
