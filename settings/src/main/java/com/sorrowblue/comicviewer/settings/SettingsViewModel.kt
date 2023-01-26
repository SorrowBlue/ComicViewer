package com.sorrowblue.comicviewer.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.usecase.settings.LoadSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
internal class SettingsViewModel @Inject constructor(
    private val loadSettingsUseCase: LoadSettingsUseCase
) : ViewModel() {

    val settings = loadSettingsUseCase.settings

    fun updateRestoreOnLaunch(newValue: Boolean) {
        viewModelScope.launch {
            loadSettingsUseCase.edit { it.copy(restoreOnLaunch = newValue) }
        }
    }
}
