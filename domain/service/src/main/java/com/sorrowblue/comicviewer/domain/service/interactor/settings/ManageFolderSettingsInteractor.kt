package com.sorrowblue.comicviewer.domain.service.interactor.settings

import com.sorrowblue.comicviewer.domain.model.settings.FolderSettings
import com.sorrowblue.comicviewer.domain.service.repository.SettingsCommonRepository
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderSettingsUseCase
import javax.inject.Inject

internal class ManageFolderSettingsInteractor @Inject constructor(
    private val settingsCommonRepository: SettingsCommonRepository,
) : ManageFolderSettingsUseCase {

    override val settings = settingsCommonRepository.folderSettings

    override suspend fun edit(action: (FolderSettings) -> FolderSettings) {
        settingsCommonRepository.updateFolderSettings(action::invoke)
    }
}
