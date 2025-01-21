package com.sorrowblue.comicviewer.feature.settings.display

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageDisplaySettingsUseCase
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
internal class DisplaySettingsViewModel(
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
