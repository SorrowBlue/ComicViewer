package com.sorrowblue.comicviewer.domain.usecase.settings.interactor

import com.sorrowblue.comicviewer.domain.entity.settings.DisplaySettings
import com.sorrowblue.comicviewer.domain.repository.SettingsCommonRepository
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageDisplaySettingsUseCase
import javax.inject.Inject

internal class ManageDisplaySettingsInteractor @Inject constructor(
    private val settingsCommonRepository: SettingsCommonRepository,
) : ManageDisplaySettingsUseCase() {

    override val settings = settingsCommonRepository.displaySettings

    override suspend fun edit(action: (DisplaySettings) -> DisplaySettings) {
        settingsCommonRepository.updateDisplaySettings(action)
    }
}
