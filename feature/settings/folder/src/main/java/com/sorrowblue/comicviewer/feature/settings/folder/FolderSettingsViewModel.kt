package com.sorrowblue.comicviewer.feature.settings.folder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.usecase.file.DeleteThumbnailsUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@HiltViewModel
class FolderSettingsViewModel @Inject constructor(
    private val manageFolderSettingsUseCase: ManageFolderSettingsUseCase,
    private val deleteThumbnailsUseCase: DeleteThumbnailsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(FolderSettingsScreenUiState())
    val settings = manageFolderSettingsUseCase.settings

    init {
        manageFolderSettingsUseCase.settings.onEach {
            _uiState.value = _uiState.value.copy(
                isOpenImageFolder = it.resolveImageFolder,
                isThumbnailEnabled = it.showPreview
            )
        }.launchIn(viewModelScope)
    }

    fun updateShowPreview(newValue: Boolean) {
        viewModelScope.launch {
            manageFolderSettingsUseCase.edit {
                it.copy(showPreview = newValue)
            }
        }
    }

    fun updateResolveImageFolder(newValue: Boolean) {
        viewModelScope.launch {
            manageFolderSettingsUseCase.edit {
                it.copy(resolveImageFolder = newValue)
            }
        }
    }

    fun deleteThumbnail() {
        viewModelScope.launch {
            deleteThumbnailsUseCase.execute(DeleteThumbnailsUseCase.Request).collect()
        }
    }
}
