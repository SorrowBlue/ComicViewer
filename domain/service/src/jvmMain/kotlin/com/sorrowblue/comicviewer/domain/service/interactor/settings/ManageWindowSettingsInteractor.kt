/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.service.interactor.settings

import com.sorrowblue.comicviewer.domain.model.settings.WindowSettings
import com.sorrowblue.comicviewer.domain.repository.JvmSettingsRepository
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageWindowSettingsUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding

@ContributesBinding(AppScope::class)
internal class ManageWindowSettingsInteractor(
    private val settingsRepository: JvmSettingsRepository,
) : ManageWindowSettingsUseCase {
    override val settings = settingsRepository.windowSettings

    override suspend fun edit(action: (WindowSettings) -> WindowSettings) {
        settingsRepository.updateWindowSettings(action)
    }
}
