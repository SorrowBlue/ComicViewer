package com.sorrowblue.comicviewer.data.datastore

import androidx.datastore.core.DataStore
import com.sorrowblue.comicviewer.domain.model.settings.BookSettings
import com.sorrowblue.comicviewer.domain.model.settings.DisplaySettings
import com.sorrowblue.comicviewer.domain.model.settings.FolderSettings
import com.sorrowblue.comicviewer.domain.model.settings.OneTimeFlag
import com.sorrowblue.comicviewer.domain.model.settings.SecuritySettings
import com.sorrowblue.comicviewer.domain.model.settings.Settings
import com.sorrowblue.comicviewer.domain.model.settings.ViewerOperationSettings
import com.sorrowblue.comicviewer.domain.model.settings.ViewerSettings
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderDisplaySettings
import com.sorrowblue.comicviewer.domain.service.datasource.DatastoreDataSource
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

internal class DatastoreDataSourceImpl @Inject constructor(
    private val settingsDataStore: DataStore<Settings>,
    private val displaySettingsDataStore: DataStore<DisplaySettings>,
    private val viewerSettingsDataStore: DataStore<ViewerSettings>,
    private val bookSettingsDataStore: DataStore<BookSettings>,
    private val folderDisplaySettingsDataStore: DataStore<FolderDisplaySettings>,
    private val folderSettingsDataStore: DataStore<FolderSettings>,
    private val viewerOperationSettingsDataStore: DataStore<ViewerOperationSettings>,
    private val securitySettingsDataStore: DataStore<SecuritySettings>,
    private val oneTimeFlagDataStore: DataStore<OneTimeFlag>,
) : DatastoreDataSource {

    override val oneTimeFlag: Flow<OneTimeFlag> = oneTimeFlagDataStore.data

    override suspend fun updateOneTimeFlag(transform: suspend (OneTimeFlag) -> OneTimeFlag) {
        oneTimeFlagDataStore.updateData(transform)
    }

    override val settings = settingsDataStore.data
    override suspend fun updateSettings(transform: suspend (Settings) -> Settings) =
        settingsDataStore.updateData(transform)

    override val displaySettings = displaySettingsDataStore.data
    override suspend fun updateDisplaySettings(transform: suspend (DisplaySettings) -> DisplaySettings) =
        displaySettingsDataStore.updateData(transform)

    override val viewerSettings = viewerSettingsDataStore.data
    override suspend fun updateViewerSettings(transform: suspend (ViewerSettings) -> ViewerSettings) =
        viewerSettingsDataStore.updateData(transform)

    override val bookSettings: Flow<BookSettings> = bookSettingsDataStore.data
    override suspend fun updateBookSettings(transform: suspend (BookSettings) -> BookSettings) =
        bookSettingsDataStore.updateData(transform)

    override val folderDisplaySettings = folderDisplaySettingsDataStore.data
    override suspend fun updateFolderDisplaySettings(
        transform: suspend (FolderDisplaySettings) -> FolderDisplaySettings,
    ) =
        folderDisplaySettingsDataStore.updateData(transform)

    override val folderSettings = folderSettingsDataStore.data
    override suspend fun updateFolderSettings(transform: suspend (FolderSettings) -> FolderSettings) =
        folderSettingsDataStore.updateData(transform)

    override val viewerOperationSettings = viewerOperationSettingsDataStore.data
    override suspend fun updateViewerOperationSettings(
        transform: suspend (ViewerOperationSettings) -> ViewerOperationSettings,
    ) =
        viewerOperationSettingsDataStore.updateData(transform)

    override val securitySettings: Flow<SecuritySettings> = securitySettingsDataStore.data

    override suspend fun updateSecuritySettings(transform: suspend (SecuritySettings) -> SecuritySettings) =
        securitySettingsDataStore.updateData(transform)
}
