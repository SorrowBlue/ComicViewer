package com.sorrowblue.comicviewer.domain.usecase.settings.interactor

import com.sorrowblue.comicviewer.domain.entity.settings.DisplaySettings
import com.sorrowblue.comicviewer.domain.repository.SettingsCommonRepository
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageDisplaySettingsUseCase
import javax.inject.Inject

internal class ManageDisplaySettingsInteractor @Inject constructor(
    private val repository: SettingsCommonRepository,
) : ManageDisplaySettingsUseCase() {

    override val settings = repository.displaySettings

    override suspend fun edit(action: (DisplaySettings) -> DisplaySettings) {
        repository.updateDisplaySettings(action)
    }
}
