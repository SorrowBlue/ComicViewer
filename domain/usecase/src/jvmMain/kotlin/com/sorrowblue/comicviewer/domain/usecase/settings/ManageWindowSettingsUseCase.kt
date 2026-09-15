/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.settings

import com.sorrowblue.comicviewer.domain.model.settings.WindowSettings
import com.sorrowblue.comicviewer.domain.repository.JvmSettingsRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

interface ManageWindowSettingsUseCase : ManageSettingsUseCase<WindowSettings>

@Inject
@ContributesBinding(AppScope::class)
internal class ManageWindowSettingsUseCaseImpl(
    private val settingsRepository: JvmSettingsRepository,
) : ManageWindowSettingsUseCase {

    override val settings: Flow<WindowSettings> = settingsRepository.windowSettings

    override suspend fun edit(action: (WindowSettings) -> WindowSettings) {
        settingsRepository.updateWindowSettings(action)
    }
}

