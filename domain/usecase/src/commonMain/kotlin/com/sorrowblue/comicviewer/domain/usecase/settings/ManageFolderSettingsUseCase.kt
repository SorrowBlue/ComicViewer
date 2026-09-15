/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.settings

import com.sorrowblue.comicviewer.domain.model.settings.FolderSettings
import com.sorrowblue.comicviewer.domain.repository.SettingsRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

@Inject
class ManageFolderSettingsUseCase(private val settingsRepository: SettingsRepository) :
    ManageSettingsUseCase<FolderSettings> {
    override val settings: Flow<FolderSettings> = settingsRepository.folderSettings

    override suspend fun edit(action: (FolderSettings) -> FolderSettings) {
        settingsRepository.updateFolderSettings(action::invoke)
    }
}
