package com.sorrowblue.comicviewer.data.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStoreFile
import com.sorrowblue.comicviewer.data.datastore.createDataStore
import com.sorrowblue.comicviewer.data.datastore.serializer.BookSettingsSerializer
import com.sorrowblue.comicviewer.data.datastore.serializer.DisplaySettingsSerializer
import com.sorrowblue.comicviewer.data.datastore.serializer.FolderDisplaySettingsSerializer
import com.sorrowblue.comicviewer.data.datastore.serializer.FolderSettingsSerializer
import com.sorrowblue.comicviewer.data.datastore.serializer.KOkioSerializer
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
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import okio.FileSystem
import okio.Path.Companion.toPath

@Module
@InstallIn(SingletonComponent::class)
internal object SingletonProvidesModule {

    @Singleton
    @Provides
    fun provideFolderDisplaySettingsDataStore(@ApplicationContext context: Context): DataStore<FolderDisplaySettings> =
        context.dataStore(FolderDisplaySettingsSerializer)

    @Singleton
    @Provides
    fun provideFolderSettingsDataStore(@ApplicationContext context: Context): DataStore<FolderSettings> =
        context.dataStore(FolderSettingsSerializer)

    @Singleton
    @Provides
    fun provideSettingsDataStore(@ApplicationContext context: Context): DataStore<Settings> =
        context.dataStore(SettingsSerializer)

    @Singleton
    @Provides
    fun provideDisplaySettingsDataStore(@ApplicationContext context: Context): DataStore<DisplaySettings> =
        context.dataStore(DisplaySettingsSerializer)

    @Singleton
    @Provides
    fun provideViewerSettingsDataStore(@ApplicationContext context: Context): DataStore<ViewerSettings> =
        context.dataStore(ViewerSettingsSerializer)

    @Singleton
    @Provides
    fun provideBookSettingsDataStore(@ApplicationContext context: Context): DataStore<BookSettings> =
        context.dataStore(BookSettingsSerializer)

    @Singleton
    @Provides
    fun provideViewerOperationSettingsDataStore(@ApplicationContext context: Context): DataStore<ViewerOperationSettings> =
        context.dataStore(ViewerOperationSettingsSerializer)

    @Singleton
    @Provides
    fun provideSecuritySettingsDataStore(@ApplicationContext context: Context): DataStore<SecuritySettings> =
        context.dataStore(SecuritySettingsSerializer)
}

@org.koin.core.annotation.Singleton
@FolderDisplay
internal fun provideFolderDisplaySettingsDataStore(@ApplicationContext context: Context): DataStore<FolderDisplaySettings> =
    context.dataStore(FolderDisplaySettingsSerializer)

@org.koin.core.annotation.Singleton
@Folder
internal fun provideFolderSettingsDataStore(@ApplicationContext context: Context): DataStore<FolderSettings> =
    context.dataStore(FolderSettingsSerializer)

@org.koin.core.annotation.Singleton
@com.sorrowblue.comicviewer.data.datastore.di.Settings
internal fun provideSettingsDataStore(@ApplicationContext context: Context): DataStore<Settings> =
    context.dataStore(SettingsSerializer)

@org.koin.core.annotation.Singleton
@Display
internal fun provideDisplaySettingsDataStore(@ApplicationContext context: Context): DataStore<DisplaySettings> =
    context.dataStore(DisplaySettingsSerializer)

@org.koin.core.annotation.Singleton
@Viewer
internal fun provideViewerSettingsDataStore(@ApplicationContext context: Context): DataStore<ViewerSettings> =
    context.dataStore(ViewerSettingsSerializer)

@org.koin.core.annotation.Singleton
@Book
internal fun provideBookSettingsDataStore(@ApplicationContext context: Context): DataStore<BookSettings> =
    context.dataStore(BookSettingsSerializer)

@org.koin.core.annotation.Singleton
@ViewerOperation
internal fun provideViewerOperationSettingsDataStore(@ApplicationContext context: Context): DataStore<ViewerOperationSettings> =
    context.dataStore(ViewerOperationSettingsSerializer)

@org.koin.core.annotation.Singleton
@Security
internal fun provideSecuritySettingsDataStore(@ApplicationContext context: Context): DataStore<SecuritySettings> =
    context.dataStore(SecuritySettingsSerializer)

private fun <T : Any> Context.dataStore(serializer: KOkioSerializer<T>): DataStore<T> {
    val producePath =
        { applicationContext.dataStoreFile(serializer.fileName).absolutePath.toPath() }
    return createDataStore(
        fileSystem = FileSystem.SYSTEM,
        serializer = serializer,
        producePath = producePath
    )
}
