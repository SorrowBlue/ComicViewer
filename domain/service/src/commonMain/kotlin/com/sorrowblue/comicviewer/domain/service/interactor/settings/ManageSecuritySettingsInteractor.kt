/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.service.interactor.settings

import com.sorrowblue.comicviewer.domain.model.settings.SecuritySettings
import com.sorrowblue.comicviewer.domain.repository.SettingsRepository
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageSecuritySettingsUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding

@ContributesBinding(AppScope::class)
internal class ManageSecuritySettingsInteractor(
    private val settingsRepository: SettingsRepository,
) : ManageSecuritySettingsUseCase {
    override val settings = settingsRepository.securitySettings

    override suspend fun edit(action: (SecuritySettings) -> SecuritySettings) {
        settingsRepository.updateSecuritySettings(action::invoke)
    }
}
