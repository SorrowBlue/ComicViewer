package com.sorrowblue.comicviewer.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.model.settings.Settings
import com.sorrowblue.comicviewer.domain.usecase.settings.LoadSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import logcat.logcat

@HiltViewModel
internal class SettingsViewModel @Inject constructor(
    private val loadSettingsUseCase: LoadSettingsUseCase
) : ViewModel() {

    val settings =
        loadSettingsUseCase.settings.onEach {
            logcat { "settings=${it}" }
        }.shareIn(viewModelScope, SharingStarted.Eagerly, 1)

    init {
        logcat { "loadSettingsUseCase=${loadSettingsUseCase}" }
    }

    fun updateUseAuth(newValue: Boolean) {
        logcat { "updateUseAuth=${newValue}" }
        viewModelScope.launch {
            loadSettingsUseCase.edit {
                logcat { "loadSettingsUseCase=${loadSettingsUseCase}" }
                it.copy(useAuth = newValue)
            }
        }
    }

    fun updateRestoreOnLaunch(newValue: Boolean) {
        logcat { "updateRestoreOnLaunch=${newValue}" }
        viewModelScope.launch {
            loadSettingsUseCase.edit { it.copy(restoreOnLaunch = newValue) }
        }
    }
}
