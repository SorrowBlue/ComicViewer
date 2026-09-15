/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.settings

import com.sorrowblue.comicviewer.domain.model.settings.ViewerSettings
import com.sorrowblue.comicviewer.domain.repository.SettingsRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

interface ManageViewerSettingsUseCase : ManageSettingsUseCase<ViewerSettings>

@Inject
@ContributesBinding(AppScope::class)
internal class ManageViewerSettingsUseCaseImpl(
    private val settingsRepository: SettingsRepository,
) : ManageViewerSettingsUseCase {

    override val settings: Flow<ViewerSettings> = settingsRepository.viewerSettings

    override suspend fun edit(action: (ViewerSettings) -> ViewerSettings) {
        settingsRepository.updateViewerSettings(action)
    }
}
