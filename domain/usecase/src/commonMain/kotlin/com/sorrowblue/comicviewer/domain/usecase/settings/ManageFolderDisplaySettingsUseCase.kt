/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.settings

import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderDisplaySettings
import com.sorrowblue.comicviewer.domain.repository.SettingsRepository
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

@Inject
class ManageFolderDisplaySettingsUseCase(private val settingsRepository: SettingsRepository) :
    ManageSettingsUseCase<FolderDisplaySettings> {
    override val settings: Flow<FolderDisplaySettings> = settingsRepository.folderDisplaySettings

    override suspend fun edit(action: (FolderDisplaySettings) -> FolderDisplaySettings) {
        settingsRepository.updateFolderDisplaySettings(action::invoke)
    }
}
