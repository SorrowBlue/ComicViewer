package com.sorrowblue.comicviewer.data.datastore.di

import androidx.datastore.core.DataStore
import com.sorrowblue.comicviewer.data.datastore.DataStoreMaker
import com.sorrowblue.comicviewer.data.datastore.qualifier.Book
import com.sorrowblue.comicviewer.data.datastore.qualifier.Collection
import com.sorrowblue.comicviewer.data.datastore.qualifier.Display
import com.sorrowblue.comicviewer.data.datastore.qualifier.Folder
import com.sorrowblue.comicviewer.data.datastore.qualifier.FolderDisplay
import com.sorrowblue.comicviewer.data.datastore.qualifier.GlobalSettings
import com.sorrowblue.comicviewer.data.datastore.qualifier.PdfPlugin
import com.sorrowblue.comicviewer.data.datastore.qualifier.Security
import com.sorrowblue.comicviewer.data.datastore.qualifier.Viewer
import com.sorrowblue.comicviewer.data.datastore.serializer.BookSettingsSerializer
import com.sorrowblue.comicviewer.data.datastore.serializer.CollectionSettingsSerializer
import com.sorrowblue.comicviewer.data.datastore.serializer.DisplaySettingsSerializer
import com.sorrowblue.comicviewer.data.datastore.serializer.FolderDisplaySettingsSerializer
import com.sorrowblue.comicviewer.data.datastore.serializer.FolderSettingsSerializer
import com.sorrowblue.comicviewer.data.datastore.serializer.PdfPluginSettingsSerializer
import com.sorrowblue.comicviewer.data.datastore.serializer.SecuritySettingsSerializer
import com.sorrowblue.comicviewer.data.datastore.serializer.SettingsSerializer
import com.sorrowblue.comicviewer.data.datastore.serializer.ViewerSettingsSerializer
import com.sorrowblue.comicviewer.domain.model.settings.BookSettings
import com.sorrowblue.comicviewer.domain.model.settings.CollectionSettings
import com.sorrowblue.comicviewer.domain.model.settings.DisplaySettings
import com.sorrowblue.comicviewer.domain.model.settings.FolderSettings
import com.sorrowblue.comicviewer.domain.model.settings.SecuritySettings
import com.sorrowblue.comicviewer.domain.model.settings.Settings
import com.sorrowblue.comicviewer.domain.model.settings.ViewerSettings
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderDisplaySettings
import com.sorrowblue.comicviewer.domain.model.settings.plugin.PdfPluginSettings
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@ContributesTo(AppScope::class)
interface DataStoreModule {
    @Book
    @SingleIn(AppScope::class)
    @Provides
    private fun bookSettingsDataStore(dataStoreMaker: DataStoreMaker): DataStore<BookSettings> =
        dataStoreMaker.createDataStore(BookSettingsSerializer)

    @Display
    @SingleIn(AppScope::class)
    @Provides
    private fun provideDisplaySettingsDataStore(
        dataStoreMaker: DataStoreMaker,
    ): DataStore<DisplaySettings> = dataStoreMaker.createDataStore(DisplaySettingsSerializer)

    @FolderDisplay
    @SingleIn(AppScope::class)
    @Provides
    private fun folderDisplaySettingsDataStore(
        dataStoreMaker: DataStoreMaker,
    ): DataStore<FolderDisplaySettings> =
        dataStoreMaker.createDataStore(FolderDisplaySettingsSerializer)

    @Folder
    @SingleIn(AppScope::class)
    @Provides
    private fun folderSettingsDataStore(dataStoreMaker: DataStoreMaker): DataStore<FolderSettings> =
        dataStoreMaker.createDataStore(FolderSettingsSerializer)

    @Security
    @SingleIn(AppScope::class)
    @Provides
    private fun securitySettingsDataStore(
        dataStoreMaker: DataStoreMaker,
    ): DataStore<SecuritySettings> = dataStoreMaker.createDataStore(SecuritySettingsSerializer)

    @GlobalSettings
    @SingleIn(AppScope::class)
    @Provides
    private fun provideSettingsDataStore(dataStoreMaker: DataStoreMaker): DataStore<Settings> =
        dataStoreMaker.createDataStore(SettingsSerializer)

    @Viewer
    @SingleIn(AppScope::class)
    @Provides
    private fun viewerSettingsDataStore(dataStoreMaker: DataStoreMaker): DataStore<ViewerSettings> =
        dataStoreMaker.createDataStore(ViewerSettingsSerializer)

    @Collection
    @SingleIn(AppScope::class)
    @Provides
    private fun collectionSettingsDataStore(
        dataStoreMaker: DataStoreMaker,
    ): DataStore<CollectionSettings> = dataStoreMaker.createDataStore(CollectionSettingsSerializer)

    @PdfPlugin
    @SingleIn(AppScope::class)
    @Provides
    private fun pdfPluginSettingsDataStore(
        dataStoreMaker: DataStoreMaker,
    ): DataStore<PdfPluginSettings> = dataStoreMaker.createDataStore(PdfPluginSettingsSerializer)
}
