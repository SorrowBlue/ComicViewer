package com.sorrowblue.comicviewer.domain.service.interactor.settings

import com.sorrowblue.comicviewer.domain.model.settings.DisplaySettings
import com.sorrowblue.comicviewer.domain.service.repository.SettingsCommonRepository
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageDisplaySettingsUseCase
import javax.inject.Inject

internal class ManageDisplaySettingsInteractor @Inject constructor(
    private val settingsCommonRepository: SettingsCommonRepository,
) : ManageDisplaySettingsUseCase {

    override val settings = settingsCommonRepository.displaySettings

    override suspend fun edit(action: (DisplaySettings) -> DisplaySettings) {
        settingsCommonRepository.updateDisplaySettings(action)
    }
}
