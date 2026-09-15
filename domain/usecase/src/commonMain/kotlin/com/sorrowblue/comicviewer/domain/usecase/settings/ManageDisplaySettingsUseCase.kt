/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.settings

import com.sorrowblue.comicviewer.domain.model.settings.DisplaySettings
import com.sorrowblue.comicviewer.domain.repository.SettingsRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

interface ManageDisplaySettingsUseCase : ManageSettingsUseCase<DisplaySettings>

@Inject
@ContributesBinding(AppScope::class)
internal class ManageDisplaySettingsUseCaseImpl(
    private val settingsRepository: SettingsRepository,
) : ManageDisplaySettingsUseCase {

    override val settings: Flow<DisplaySettings> = settingsRepository.displaySettings

    override suspend fun edit(action: (DisplaySettings) -> DisplaySettings) {
        settingsRepository.updateDisplaySettings(action)
    }
}
