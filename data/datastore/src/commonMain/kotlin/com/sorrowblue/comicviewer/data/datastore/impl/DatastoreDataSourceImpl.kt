package com.sorrowblue.comicviewer.data.datastore.impl

import androidx.datastore.core.DataStore
import com.sorrowblue.comicviewer.data.datastore.qualifier.Book
import com.sorrowblue.comicviewer.data.datastore.qualifier.Collection
import com.sorrowblue.comicviewer.data.datastore.qualifier.Display
import com.sorrowblue.comicviewer.data.datastore.qualifier.Folder
import com.sorrowblue.comicviewer.data.datastore.qualifier.FolderDisplay
import com.sorrowblue.comicviewer.data.datastore.qualifier.GlobalSettings
import com.sorrowblue.comicviewer.data.datastore.qualifier.PdfPlugin
import com.sorrowblue.comicviewer.data.datastore.qualifier.Security
import com.sorrowblue.comicviewer.data.datastore.qualifier.Viewer
import com.sorrowblue.comicviewer.data.datastore.qualifier.ViewerOperation
import com.sorrowblue.comicviewer.domain.model.settings.BookSettings
import com.sorrowblue.comicviewer.domain.model.settings.CollectionSettings
import com.sorrowblue.comicviewer.domain.model.settings.DisplaySettings
import com.sorrowblue.comicviewer.domain.model.settings.FolderSettings
import com.sorrowblue.comicviewer.domain.model.settings.SecuritySettings
import com.sorrowblue.comicviewer.domain.model.settings.Settings
import com.sorrowblue.comicviewer.domain.model.settings.ViewerOperationSettings
import com.sorrowblue.comicviewer.domain.model.settings.ViewerSettings
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderDisplaySettings
import com.sorrowblue.comicviewer.domain.model.settings.plugin.PdfPluginSettings
import com.sorrowblue.comicviewer.domain.service.datasource.DatastoreDataSource
import com.sorrowblue.comicviewer.framework.common.scope.DataScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow

@ContributesBinding(DataScope::class)
@SingleIn(DataScope::class)
@Inject
internal class DatastoreDataSourceImpl(
    @param:GlobalSettings private val settingsDataStore: DataStore<Settings>,
    @param:Display private val displaySettingsDataStore: DataStore<DisplaySettings>,
    @param:Viewer private val viewerSettingsDataStore: DataStore<ViewerSettings>,
    @param:Book private val bookSettingsDataStore: DataStore<BookSettings>,
    @param:FolderDisplay private val folderDisplaySettingsDataStore:
    DataStore<FolderDisplaySettings>,
    @param:Folder private val folderSettingsDataStore: DataStore<FolderSettings>,
    @param:ViewerOperation private val viewerOperationSettingsDataStore:
    DataStore<ViewerOperationSettings>,
    @param:Collection private val collectionSettingsDataStore: DataStore<CollectionSettings>,
    @param:Security private val securitySettingsDataStore: DataStore<SecuritySettings>,
    @param:PdfPlugin private val pdfPluginSettingsDataStore: DataStore<PdfPluginSettings>,
) : DatastoreDataSource {
    override val settings = settingsDataStore.data

    override suspend fun updateSettings(transform: suspend (Settings) -> Settings) =
        settingsDataStore.updateData(transform)

    override val displaySettings = displaySettingsDataStore.data

    override suspend fun updateDisplaySettings(
        transform: suspend (DisplaySettings) -> DisplaySettings,
    ) = displaySettingsDataStore.updateData(transform)

    override val viewerSettings = viewerSettingsDataStore.data

    override suspend fun updateViewerSettings(
        transform: suspend (ViewerSettings) -> ViewerSettings,
    ) = viewerSettingsDataStore.updateData(transform)

    override val bookSettings: Flow<BookSettings> = bookSettingsDataStore.data

    override suspend fun updateBookSettings(transform: suspend (BookSettings) -> BookSettings) =
        bookSettingsDataStore.updateData(transform)

    override val folderDisplaySettings = folderDisplaySettingsDataStore.data

    override suspend fun updateFolderDisplaySettings(
        transform: suspend (FolderDisplaySettings) -> FolderDisplaySettings,
    ) = folderDisplaySettingsDataStore.updateData(transform)

    override val folderSettings = folderSettingsDataStore.data

    override suspend fun updateFolderSettings(
        transform: suspend (FolderSettings) -> FolderSettings,
    ) = folderSettingsDataStore.updateData(transform)

    override val viewerOperationSettings = viewerOperationSettingsDataStore.data

    override suspend fun updateViewerOperationSettings(
        transform: suspend (ViewerOperationSettings) -> ViewerOperationSettings,
    ) = viewerOperationSettingsDataStore.updateData(transform)

    override val securitySettings: Flow<SecuritySettings> = securitySettingsDataStore.data

    override suspend fun updateSecuritySettings(
        transform: suspend (SecuritySettings) -> SecuritySettings,
    ) = securitySettingsDataStore.updateData(transform)

    override val collectionSettings: Flow<CollectionSettings> = collectionSettingsDataStore.data

    override suspend fun updateCollectionSettings(
        transform: suspend (CollectionSettings) -> CollectionSettings,
    ) = collectionSettingsDataStore.updateData(transform)

    override val pdfPluginSettings = pdfPluginSettingsDataStore.data

    override suspend fun updatePdfPluginSettings(
        transform: suspend (PdfPluginSettings) -> PdfPluginSettings,
    ) = pdfPluginSettingsDataStore.updateData(transform)
}
