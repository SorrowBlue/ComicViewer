package com.sorrowblue.comicviewer.data.datastore

import androidx.datastore.core.DataStore
import com.sorrowblue.comicviewer.data.datasource.DatastoreDataSource
import com.sorrowblue.comicviewer.domain.entity.settings.DisplaySettings
import com.sorrowblue.comicviewer.domain.entity.settings.FolderDisplaySettings
import com.sorrowblue.comicviewer.domain.entity.settings.FolderSettings
import com.sorrowblue.comicviewer.domain.entity.settings.History
import com.sorrowblue.comicviewer.domain.entity.settings.SecuritySettings
import com.sorrowblue.comicviewer.domain.entity.settings.Settings
import com.sorrowblue.comicviewer.domain.entity.settings.ViewerOperationSettings
import com.sorrowblue.comicviewer.domain.entity.settings.ViewerSettings
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

internal class DatastoreDataSourceImpl @Inject constructor(
    private val historyDataStore: DataStore<History>,
    private val settingsDataStore: DataStore<Settings>,
    private val displaySettingsDataStore: DataStore<DisplaySettings>,
    private val viewerSettingsDataStore: DataStore<ViewerSettings>,
    private val folderDisplaySettingsDataStore: DataStore<FolderDisplaySettings>,
    private val folderSettingsDataStore: DataStore<FolderSettings>,
    private val viewerOperationSettingsDataStore: DataStore<ViewerOperationSettings>,
    private val securitySettingsDataStore: DataStore<SecuritySettings>,
) : DatastoreDataSource {

    override val history: Flow<History> = historyDataStore.data
    override suspend fun updateHistory(transform: suspend (History) -> History) =
        historyDataStore.updateData(transform)

    override val settings = settingsDataStore.data
    override suspend fun updateSettings(transform: suspend (Settings) -> Settings) =
        settingsDataStore.updateData(transform)

    override val displaySettings = displaySettingsDataStore.data
    override suspend fun updateDisplaySettings(transform: suspend (DisplaySettings) -> DisplaySettings) =
        displaySettingsDataStore.updateData(transform)

    override val viewerSettings = viewerSettingsDataStore.data
    override suspend fun updateViewerSettings(transform: suspend (ViewerSettings) -> ViewerSettings) =
        viewerSettingsDataStore.updateData(transform)

    override val folderDisplaySettings = folderDisplaySettingsDataStore.data
    override suspend fun updateFolderDisplaySettings(transform: suspend (FolderDisplaySettings) -> FolderDisplaySettings) =
        folderDisplaySettingsDataStore.updateData(transform)

    override val folderSettings = folderSettingsDataStore.data
    override suspend fun updateFolderSettings(transform: suspend (FolderSettings) -> FolderSettings) =
        folderSettingsDataStore.updateData(transform)

    override val viewerOperationSettings = viewerOperationSettingsDataStore.data
    override suspend fun updateViewerOperationSettings(transform: suspend (ViewerOperationSettings) -> ViewerOperationSettings) =
        viewerOperationSettingsDataStore.updateData(transform)

    override val securitySettings: Flow<SecuritySettings> = securitySettingsDataStore.data

    override suspend fun updateSecuritySettings(transform: suspend (SecuritySettings) -> SecuritySettings) =
        securitySettingsDataStore.updateData(transform)
}
