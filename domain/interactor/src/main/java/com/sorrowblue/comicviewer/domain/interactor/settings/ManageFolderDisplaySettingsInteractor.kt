package com.sorrowblue.comicviewer.domain.interactor.settings

import com.sorrowblue.comicviewer.domain.model.settings.FolderDisplaySettings
import com.sorrowblue.comicviewer.domain.repository.SettingsCommonRepository
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderDisplaySettingsUseCase
import javax.inject.Inject

internal class ManageFolderDisplaySettingsInteractor @Inject constructor(
    private val settingsCommonRepository: SettingsCommonRepository,
) : ManageFolderDisplaySettingsUseCase() {

    override val settings = settingsCommonRepository.folderDisplaySettings

    override suspend fun edit(action: (FolderDisplaySettings) -> FolderDisplaySettings) {
        settingsCommonRepository.updateFolderDisplaySettings(action::invoke)
    }
}
