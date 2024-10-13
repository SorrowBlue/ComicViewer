package com.sorrowblue.comicviewer.feature.settings.display

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageDisplaySettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
internal class DisplaySettingsViewModel @Inject constructor(
    val displaySettingsUseCase: ManageDisplaySettingsUseCase,
) : ViewModel() {

    val displaySettings = displaySettingsUseCase.settings

    fun onRestoreOnLaunchChange(value: Boolean) {
        viewModelScope.launch {
            displaySettingsUseCase.edit {
                it.copy(restoreOnLaunch = value)
            }
        }
    }
}
