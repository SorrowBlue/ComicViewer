package com.sorrowblue.comicviewer.domain.interactor.settings

import com.sorrowblue.comicviewer.domain.model.settings.OneTimeFlag
import com.sorrowblue.comicviewer.domain.model.settings.Settings
import com.sorrowblue.comicviewer.domain.repository.SettingsCommonRepository
import com.sorrowblue.comicviewer.domain.usecase.settings.LoadSettingsUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageOneTimeFlagUseCase
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

internal class ManageOneTimeFlagInteractor @Inject constructor(
    private val settingsCommonRepository: SettingsCommonRepository,
) : ManageOneTimeFlagUseCase() {

    override val settings: Flow<OneTimeFlag> = settingsCommonRepository.oneTimeFlag

    override suspend fun edit(action: (OneTimeFlag) -> OneTimeFlag) {
        settingsCommonRepository.updateOneTimeFlag(action)
    }
}
