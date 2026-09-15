/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.settings

import com.sorrowblue.comicviewer.domain.model.settings.SecuritySettings
import com.sorrowblue.comicviewer.domain.repository.SettingsRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

@Inject
class ManageSecuritySettingsUseCase(private val settingsRepository: SettingsRepository) :
    ManageSettingsUseCase<SecuritySettings> {
    override val settings: Flow<SecuritySettings> = settingsRepository.securitySettings

    override suspend fun edit(action: (SecuritySettings) -> SecuritySettings) {
        settingsRepository.updateSecuritySettings(action::invoke)
    }
}
