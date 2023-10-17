package com.sorrowblue.comicviewer.feature.settings.folder

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.model.SupportExtension
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@HiltViewModel
internal class SupportExtensionViewModel @Inject constructor(
    application: Application,
    private val settingsUseCase: ManageFolderSettingsUseCase
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(SupportExtensionScreenUiState())
    val uiState = _uiState.asStateFlow()

    init {
        settingsUseCase.settings.onEach {
            _uiState.value = _uiState.value.copy(supportExtension = it.supportExtension)
        }.launchIn(viewModelScope)
    }

    fun toggleExtension(extension: SupportExtension) {
        viewModelScope.launch {
            settingsUseCase.edit {
                val newValue = if (extension in it.supportExtension) {
                    it.supportExtension.filterNot { it == extension }
                } else {
                    it.supportExtension + extension
                }
                it.copy(supportExtension = newValue.sortedBy(SupportExtension::extension).toSet())
            }
        }
    }
}
