package com.sorrowblue.comicviewer.domain.usecase.interactor

import com.sorrowblue.comicviewer.domain.model.DisplaySettings
import com.sorrowblue.comicviewer.domain.repository.SettingsCommonRepository
import com.sorrowblue.comicviewer.domain.usecase.settings.DisplaySettingsUseCase
import javax.inject.Inject

internal class DisplaySettingsInteractor @Inject constructor(
    private val repository: SettingsCommonRepository,
) : DisplaySettingsUseCase() {

    override val settings = repository.displaySettings

    override suspend fun edit(action: (DisplaySettings) -> DisplaySettings) {
        repository.updateDisplaySettings(action)
    }
}
