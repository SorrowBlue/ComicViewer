package com.sorrowblue.comicviewer.domain.service.interactor.settings

import com.sorrowblue.comicviewer.domain.model.settings.SecuritySettings
import com.sorrowblue.comicviewer.domain.service.repository.SettingsCommonRepository
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageSecuritySettingsUseCase
import javax.inject.Inject

internal class ManageSecuritySettingsInteractor @Inject constructor(
    private val settingsCommonRepository: SettingsCommonRepository,
) : ManageSecuritySettingsUseCase() {

    override val settings = settingsCommonRepository.securitySettings

    override suspend fun edit(action: (SecuritySettings) -> SecuritySettings) {
        settingsCommonRepository.updateSecuritySettings(action::invoke)
    }
}
