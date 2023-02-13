package com.sorrowblue.comicviewer.settings.folder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
internal class SettingsFolderViewModel @Inject constructor(
    private val manageFolderSettingsUseCase: ManageFolderSettingsUseCase
) : ViewModel() {
    fun updateShowPreview(newValue: Boolean) {
        viewModelScope.launch {
            manageFolderSettingsUseCase.edit {
                it.copy(showPreview = newValue)
            }
        }
    }

    val settings = manageFolderSettingsUseCase.settings
}
