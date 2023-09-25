package com.sorrowblue.comicviewer.domain.repository

import com.sorrowblue.comicviewer.domain.model.settings.DisplaySettings
import com.sorrowblue.comicviewer.domain.model.settings.FolderDisplaySettings
import com.sorrowblue.comicviewer.domain.model.settings.FolderSettings
import com.sorrowblue.comicviewer.domain.model.settings.History
import com.sorrowblue.comicviewer.domain.model.settings.OneTimeFlag
import com.sorrowblue.comicviewer.domain.model.settings.SecuritySettings
import com.sorrowblue.comicviewer.domain.model.settings.Settings
import com.sorrowblue.comicviewer.domain.model.settings.ViewerOperationSettings
import com.sorrowblue.comicviewer.domain.model.settings.ViewerSettings
import kotlinx.coroutines.flow.Flow

interface SettingsCommonRepository {

    val oneTimeFlag: Flow<OneTimeFlag>
    suspend fun updateOneTimeFlag(transform: suspend (OneTimeFlag) -> OneTimeFlag)

    val history: Flow<History>
    suspend fun updateHistory(transform: suspend (History) -> History)

    val displaySettings: Flow<DisplaySettings>
    suspend fun updateDisplaySettings(transform: suspend (DisplaySettings) -> DisplaySettings)

    val viewerSettings: Flow<ViewerSettings>
    suspend fun updateViewerSettings(transform: suspend (ViewerSettings) -> ViewerSettings)

    val viewerOperationSettings: Flow<ViewerOperationSettings>
    suspend fun updateViewerOperationSettings(transform: suspend (ViewerOperationSettings) -> ViewerOperationSettings)

    val folderDisplaySettings: Flow<FolderDisplaySettings>
    suspend fun updateFolderDisplaySettings(transform: suspend (FolderDisplaySettings) -> FolderDisplaySettings)

    val folderSettings: Flow<FolderSettings>
    suspend fun updateFolderSettings(transform: suspend (FolderSettings) -> FolderSettings)

    val settings: Flow<Settings>
    suspend fun updateSettings(transform: suspend (Settings) -> Settings)

    val securitySettings: Flow<SecuritySettings>
    suspend fun updateSecuritySettings(transform: suspend (SecuritySettings) -> SecuritySettings)
}
