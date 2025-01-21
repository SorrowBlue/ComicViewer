package com.sorrowblue.comicviewer.feature.settings.folder

import androidx.lifecycle.ViewModel
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderDisplaySettingsUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderSettingsUseCase
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class FolderSettingsViewModel(
    val manageFolderSettingsUseCase: ManageFolderSettingsUseCase,
    val manageFolderDisplaySettingsUseCase: ManageFolderDisplaySettingsUseCase,
) : ViewModel()
