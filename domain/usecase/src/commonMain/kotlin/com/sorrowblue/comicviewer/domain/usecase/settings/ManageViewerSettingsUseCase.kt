/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.settings

import com.sorrowblue.comicviewer.domain.model.settings.ViewerSettings
import com.sorrowblue.comicviewer.domain.repository.SettingsRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

@Inject
class ManageViewerSettingsUseCase(private val settingsRepository: SettingsRepository) :
    ManageSettingsUseCase<ViewerSettings> {
    override val settings: Flow<ViewerSettings> = settingsRepository.viewerSettings

    override suspend fun edit(action: (ViewerSettings) -> ViewerSettings) {
        settingsRepository.updateViewerSettings(action)
    }
}
