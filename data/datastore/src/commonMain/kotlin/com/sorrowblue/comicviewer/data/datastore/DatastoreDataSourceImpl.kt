package com.sorrowblue.comicviewer.data.datastore

import androidx.datastore.core.DataStore
import com.sorrowblue.comicviewer.domain.model.settings.BookSettings
import com.sorrowblue.comicviewer.domain.model.settings.DisplaySettings
import com.sorrowblue.comicviewer.domain.model.settings.FolderSettings
import com.sorrowblue.comicviewer.domain.model.settings.SecuritySettings
import com.sorrowblue.comicviewer.domain.model.settings.Settings
import com.sorrowblue.comicviewer.domain.model.settings.ViewerOperationSettings
import com.sorrowblue.comicviewer.domain.model.settings.ViewerSettings
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderDisplaySettings
import com.sorrowblue.comicviewer.domain.service.datasource.DatastoreDataSource
import di.Inject
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Qualifier
import org.koin.core.annotation.Singleton

@Singleton
internal class DatastoreDataSourceImpl @Inject constructor(
    @Qualifier(com.sorrowblue.comicviewer.data.datastore.di.Settings::class) private val settingsDataStore: DataStore<Settings>,
    @Qualifier(com.sorrowblue.comicviewer.data.datastore.di.Display::class) private val displaySettingsDataStore: DataStore<DisplaySettings>,
    @Qualifier(com.sorrowblue.comicviewer.data.datastore.di.Viewer::class) private val viewerSettingsDataStore: DataStore<ViewerSettings>,
    @Qualifier(com.sorrowblue.comicviewer.data.datastore.di.Book::class) private val bookSettingsDataStore: DataStore<BookSettings>,
    @Qualifier(com.sorrowblue.comicviewer.data.datastore.di.FolderDisplay::class) private val folderDisplaySettingsDataStore: DataStore<FolderDisplaySettings>,
    @Qualifier(com.sorrowblue.comicviewer.data.datastore.di.Folder::class) private val folderSettingsDataStore: DataStore<FolderSettings>,
    @Qualifier(com.sorrowblue.comicviewer.data.datastore.di.ViewerOperation::class) private val viewerOperationSettingsDataStore: DataStore<ViewerOperationSettings>,
    @Qualifier(com.sorrowblue.comicviewer.data.datastore.di.Security::class) private val securitySettingsDataStore: DataStore<SecuritySettings>,
) : DatastoreDataSource {

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
