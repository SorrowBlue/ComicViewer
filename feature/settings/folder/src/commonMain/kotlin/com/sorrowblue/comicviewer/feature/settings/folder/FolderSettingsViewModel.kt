/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.settings.folder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.model.settings.FolderSettings
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderDisplaySettings
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderDisplaySettingsUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderSettingsUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch

@ViewModelKey
@ContributesIntoMap(AppScope::class)
internal class FolderSettingsViewModel(
    private val manageFolderSettingsUseCase: ManageFolderSettingsUseCase,
    private val manageFolderDisplaySettingsUseCase: ManageFolderDisplaySettingsUseCase,
) : ViewModel() {
    val folderSettingsFlow = manageFolderSettingsUseCase.settings
        .shareIn(viewModelScope, SharingStarted.Lazily, 1)

    val folderDisplaySettingsFlow = manageFolderDisplaySettingsUseCase.settings
        .shareIn(viewModelScope, SharingStarted.Lazily, 1)

    fun updateFolderSettings(action: (FolderSettings) -> FolderSettings) {
        viewModelScope.launch {
            manageFolderSettingsUseCase.edit(action)
        }
    }

    fun updateFolderDisplaySettings(action: (FolderDisplaySettings) -> FolderDisplaySettings) {
        viewModelScope.launch {
            manageFolderDisplaySettingsUseCase.edit(action)
        }
    }
}
