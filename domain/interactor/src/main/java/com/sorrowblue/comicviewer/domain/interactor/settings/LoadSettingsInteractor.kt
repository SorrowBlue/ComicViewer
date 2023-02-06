package com.sorrowblue.comicviewer.domain.interactor.settings

import com.sorrowblue.comicviewer.domain.entity.settings.Settings
import com.sorrowblue.comicviewer.domain.repository.SettingsCommonRepository
import com.sorrowblue.comicviewer.domain.usecase.settings.LoadSettingsUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

internal class LoadSettingsInteractor @Inject constructor(
    private val settingsCommonRepository: SettingsCommonRepository,
) : LoadSettingsUseCase() {

    override val settings: Flow<Settings> = settingsCommonRepository.settings

    override suspend fun edit(action: (Settings) -> Settings) {
        settingsCommonRepository.updateSettings(action)
    }
}
