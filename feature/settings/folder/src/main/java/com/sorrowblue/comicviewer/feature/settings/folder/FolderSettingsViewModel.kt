package com.sorrowblue.comicviewer.feature.settings.folder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.usecase.file.DeleteThumbnailsUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderDisplaySettingsUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@HiltViewModel
class FolderSettingsViewModel @Inject constructor(
    private val manageFolderSettingsUseCase: ManageFolderSettingsUseCase,
    private val manageFolderDisplaySettingsUseCase: ManageFolderDisplaySettingsUseCase,
    private val deleteThumbnailsUseCase: DeleteThumbnailsUseCase,
) : ViewModel() {

    val settings = manageFolderSettingsUseCase.settings
    val folderDisplaySettings = manageFolderDisplaySettingsUseCase.settings

    fun updateShowPreview(newValue: Boolean) {
        viewModelScope.launch {
            manageFolderDisplaySettingsUseCase.edit {
                it.copy(isEnabledThumbnail = newValue)
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
