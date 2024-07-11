package com.sorrowblue.comicviewer.data.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.sorrowblue.comicviewer.data.datastore.serializer.BookSettingsSerializer
import com.sorrowblue.comicviewer.data.datastore.serializer.DisplaySettingsSerializer
import com.sorrowblue.comicviewer.data.datastore.serializer.FolderDisplaySettingsSerializer
import com.sorrowblue.comicviewer.data.datastore.serializer.FolderSettingsSerializer
import com.sorrowblue.comicviewer.data.datastore.serializer.OneTimeFlagSerializer
import com.sorrowblue.comicviewer.data.datastore.serializer.SecuritySettingsSerializer
import com.sorrowblue.comicviewer.data.datastore.serializer.SettingsSerializer
import com.sorrowblue.comicviewer.data.datastore.serializer.ViewerOperationSettingsSerializer
import com.sorrowblue.comicviewer.data.datastore.serializer.ViewerSettingsSerializer
import com.sorrowblue.comicviewer.domain.model.settings.BookSettings
import com.sorrowblue.comicviewer.domain.model.settings.DisplaySettings
import com.sorrowblue.comicviewer.domain.model.settings.FolderSettings
import com.sorrowblue.comicviewer.domain.model.settings.OneTimeFlag
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
import kotlinx.serialization.ExperimentalSerializationApi

@OptIn(ExperimentalSerializationApi::class)
@Module
@InstallIn(SingletonComponent::class)
internal object SingletonProvidesModule {

    private val Context.folderDisplaySettingsDataStore: DataStore<FolderDisplaySettings> by dataStore(
        fileName = "folder_display_settings.pb",
        serializer = FolderDisplaySettingsSerializer()
    )

    private val Context.folderSettingsDataStore: DataStore<FolderSettings> by dataStore(
        fileName = "folder_settings.pb",
        serializer = FolderSettingsSerializer()
    )

    private val Context.settingsDataStore: DataStore<Settings> by dataStore(
        fileName = "settings.pb",
        serializer = SettingsSerializer()
    )

    private val Context.displaySettingsDataStore: DataStore<DisplaySettings> by dataStore(
        fileName = "displaySettings.pb",
        serializer = DisplaySettingsSerializer()
    )

    private val Context.viewerSettingsDataStore: DataStore<ViewerSettings> by dataStore(
        fileName = "viewerSettings.pb",
        serializer = ViewerSettingsSerializer()
    )

    private val Context.bookSettingsDataStore: DataStore<BookSettings> by dataStore(
        fileName = "bookSettings.pb",
        serializer = BookSettingsSerializer()
    )

    private val Context.viewerOperationSettingsDataStore: DataStore<ViewerOperationSettings> by dataStore(
        fileName = "viewerOperationSettings.pb",
        serializer = ViewerOperationSettingsSerializer()
    )

    private val Context.securitySettingsDataStore: DataStore<SecuritySettings> by dataStore(
        fileName = "securitySettings.pb",
        serializer = SecuritySettingsSerializer()
    )

    private val Context.oneTimeFlagDataStore: DataStore<OneTimeFlag> by dataStore(
        fileName = "oneTimeFlag.pb",
        serializer = OneTimeFlagSerializer()
    )

    @Singleton
    @Provides
    fun provideFolderDisplaySettingsDataStore(@ApplicationContext context: Context): DataStore<FolderDisplaySettings> =
        context.folderDisplaySettingsDataStore

    @Singleton
    @Provides
    fun provideFolderSettingsDataStore(@ApplicationContext context: Context): DataStore<FolderSettings> =
        context.folderSettingsDataStore

    @Singleton
    @Provides
    fun provideSettingsDataStore(@ApplicationContext context: Context): DataStore<Settings> =
        context.settingsDataStore

    @Singleton
    @Provides
    fun provideDisplaySettingsDataStore(@ApplicationContext context: Context): DataStore<DisplaySettings> =
        context.displaySettingsDataStore

    @Singleton
    @Provides
    fun provideViewerSettingsDataStore(@ApplicationContext context: Context): DataStore<ViewerSettings> =
        context.viewerSettingsDataStore

    @Singleton
    @Provides
    fun provideBookSettingsDataStore(@ApplicationContext context: Context): DataStore<BookSettings> =
        context.bookSettingsDataStore

    @Singleton
    @Provides
    fun provideViewerOperationSettingsDataStore(
        @ApplicationContext context: Context,
    ): DataStore<ViewerOperationSettings> =
        context.viewerOperationSettingsDataStore

    @Singleton
    @Provides
    fun provideSecuritySettingsDataStore(@ApplicationContext context: Context): DataStore<SecuritySettings> =
        context.securitySettingsDataStore

    @Singleton
    @Provides
    fun provideOneTimeFlagDataStore(@ApplicationContext context: Context): DataStore<OneTimeFlag> =
        context.oneTimeFlagDataStore
}
