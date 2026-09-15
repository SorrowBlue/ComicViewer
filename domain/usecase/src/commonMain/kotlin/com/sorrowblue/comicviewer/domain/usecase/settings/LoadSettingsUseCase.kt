/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.settings

import com.sorrowblue.comicviewer.domain.model.settings.Settings
import com.sorrowblue.comicviewer.domain.repository.SettingsRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

interface LoadSettingsUseCase : ManageSettingsUseCase<Settings>

@Inject
@ContributesBinding(AppScope::class)
internal class LoadSettingsUseCaseImpl(private val settingsRepository: SettingsRepository) :
    LoadSettingsUseCase {

    override val settings: Flow<Settings> = settingsRepository.settings

    override suspend fun edit(action: (Settings) -> Settings) {
        settingsRepository.updateSettings(action)
    }
}
