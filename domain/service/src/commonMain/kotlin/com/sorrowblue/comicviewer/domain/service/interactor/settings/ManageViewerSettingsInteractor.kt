/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.service.interactor.settings

import com.sorrowblue.comicviewer.domain.model.settings.ViewerSettings
import com.sorrowblue.comicviewer.domain.repository.SettingsRepository
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageViewerSettingsUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding

@ContributesBinding(AppScope::class)
internal class ManageViewerSettingsInteractor(private val settingsRepository: SettingsRepository) :
    ManageViewerSettingsUseCase {
    override val settings = settingsRepository.viewerSettings

    override suspend fun edit(action: (ViewerSettings) -> ViewerSettings) {
        settingsRepository.updateViewerSettings(action)
    }
}
