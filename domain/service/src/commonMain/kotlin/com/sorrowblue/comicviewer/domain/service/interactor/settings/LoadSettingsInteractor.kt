/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.service.interactor.settings

import com.sorrowblue.comicviewer.domain.model.settings.Settings
import com.sorrowblue.comicviewer.domain.repository.SettingsRepository
import com.sorrowblue.comicviewer.domain.usecase.settings.LoadSettingsUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import kotlinx.coroutines.flow.Flow

@ContributesBinding(AppScope::class)
internal class LoadSettingsInteractor(private val settingsRepository: SettingsRepository) :
    LoadSettingsUseCase {
    override val settings: Flow<Settings> = settingsRepository.settings

    override suspend fun edit(action: (Settings) -> Settings) {
        settingsRepository.updateSettings(action)
    }
}
