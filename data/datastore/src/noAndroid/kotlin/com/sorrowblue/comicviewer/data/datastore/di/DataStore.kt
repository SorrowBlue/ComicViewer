package com.sorrowblue.comicviewer.data.datastore.di

import androidx.datastore.core.DataStore
import com.sorrowblue.comicviewer.data.datastore.createDataStore
import com.sorrowblue.comicviewer.data.datastore.serializer.BookSettingsSerializer
import com.sorrowblue.comicviewer.data.datastore.serializer.DisplaySettingsSerializer
import com.sorrowblue.comicviewer.data.datastore.serializer.FolderDisplaySettingsSerializer
import com.sorrowblue.comicviewer.data.datastore.serializer.FolderSettingsSerializer
import com.sorrowblue.comicviewer.data.datastore.serializer.SecuritySettingsSerializer
import com.sorrowblue.comicviewer.data.datastore.serializer.SettingsSerializer
import com.sorrowblue.comicviewer.data.datastore.serializer.ViewerOperationSettingsSerializer
import com.sorrowblue.comicviewer.data.datastore.serializer.ViewerSettingsSerializer
import com.sorrowblue.comicviewer.domain.model.settings.BookSettings
import com.sorrowblue.comicviewer.domain.model.settings.DisplaySettings
import com.sorrowblue.comicviewer.domain.model.settings.FolderSettings
import com.sorrowblue.comicviewer.domain.model.settings.SecuritySettings
import com.sorrowblue.comicviewer.domain.model.settings.Settings
import com.sorrowblue.comicviewer.domain.model.settings.ViewerOperationSettings
import com.sorrowblue.comicviewer.domain.model.settings.ViewerSettings
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderDisplaySettings
import org.koin.core.annotation.Singleton

@Singleton
fun createFolderDisplaySettingsDataStore(): DataStore<FolderDisplaySettings> {
    return createDataStore(FolderDisplaySettingsSerializer)
}

@Singleton
fun provideFolderDisplaySettingsDataStore(): DataStore<FolderDisplaySettings> =
    createDataStore(FolderDisplaySettingsSerializer)

@Singleton
fun provideFolderSettingsDataStore(): DataStore<FolderSettings> =
    createDataStore(FolderSettingsSerializer)

@Singleton
fun provideSettingsDataStore(): DataStore<Settings> =
    createDataStore(SettingsSerializer)

@Singleton
fun provideDisplaySettingsDataStore(): DataStore<DisplaySettings> =
    createDataStore(DisplaySettingsSerializer)

@Singleton
fun provideViewerSettingsDataStore(): DataStore<ViewerSettings> =
    createDataStore(ViewerSettingsSerializer)

@Singleton
fun provideBookSettingsDataStore(): DataStore<BookSettings> =
    createDataStore(BookSettingsSerializer)

@Singleton
fun provideViewerOperationSettingsDataStore(): DataStore<ViewerOperationSettings> =
    createDataStore(ViewerOperationSettingsSerializer)

@Singleton
fun provideSecuritySettingsDataStore(): DataStore<SecuritySettings> =
    createDataStore(SecuritySettingsSerializer)
