package com.sorrowblue.comicviewer.feature.settings.folder

import androidx.lifecycle.ViewModel
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderDisplaySettingsUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FolderSettingsViewModel @Inject constructor(
    val manageFolderSettingsUseCase: ManageFolderSettingsUseCase,
    val manageFolderDisplaySettingsUseCase: ManageFolderDisplaySettingsUseCase,
) : ViewModel()
