package com.sorrowblue.comicviewer.data.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.sorrowblue.comicviewer.data.datastore.BookShelfSettingsSerializer
import com.sorrowblue.comicviewer.data.datastore.BookshelfDisplaySettingsSerializer
import com.sorrowblue.comicviewer.data.datastore.DisplaySettingsSerializer
import com.sorrowblue.comicviewer.data.datastore.HistorySerializer
import com.sorrowblue.comicviewer.data.datastore.SettingsSerializer
import com.sorrowblue.comicviewer.data.datastore.ViewerOperationSettingsSerializer
import com.sorrowblue.comicviewer.data.datastore.ViewerSettingsSerializer
import com.sorrowblue.comicviewer.domain.model.DisplaySettings
import com.sorrowblue.comicviewer.domain.model.History
import com.sorrowblue.comicviewer.domain.model.ViewerOperationSettings
import com.sorrowblue.comicviewer.domain.model.ViewerSettings
import com.sorrowblue.comicviewer.domain.model.settings.BookshelfDisplaySettings
import com.sorrowblue.comicviewer.domain.model.settings.BookshelfSettings
import com.sorrowblue.comicviewer.domain.model.settings.Settings
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object SingletonProvidesModule {

    private val Context.historyDataStore: DataStore<History> by dataStore(
        fileName = "history.pb",
        serializer = HistorySerializer
    )

    private val Context.bookshelfDisplaySettingsDataStore: DataStore<BookshelfDisplaySettings> by dataStore(
        fileName = "bookshelfDisplaySettings.pb",
        serializer = BookshelfDisplaySettingsSerializer
    )

    private val Context.bookshelfSettingsDataStore: DataStore<BookshelfSettings> by dataStore(
        fileName = "bookShelfSettings2.pb",
        serializer = BookShelfSettingsSerializer
    )

    private val Context.settingsDataStore: DataStore<Settings> by dataStore(
        fileName = "settings.pb",
        serializer = SettingsSerializer
    )

    private val Context.displaySettingsDataStore: DataStore<DisplaySettings> by dataStore(
        fileName = "displaySettings.pb",
        serializer = DisplaySettingsSerializer
    )

    private val Context.viewerSettingsDataStore: DataStore<ViewerSettings> by dataStore(
        fileName = "viewerSettings.pb",
        serializer = ViewerSettingsSerializer
    )

    private val Context.viewerOperationSettingsDataStore: DataStore<ViewerOperationSettings> by dataStore(
        fileName = "viewerOperationSettings.pb",
        serializer = ViewerOperationSettingsSerializer
    )

    @Singleton
    @Provides
    fun provideHistoryDataStore(@ApplicationContext context: Context): DataStore<History> =
        context.historyDataStore

    @Singleton
    @Provides
    fun provideBookShelfSettingsDataStore(@ApplicationContext context: Context): DataStore<BookshelfDisplaySettings> =
        context.bookshelfDisplaySettingsDataStore

    @Singleton
    @Provides
    fun provideBookShelfSettings2DataStore(@ApplicationContext context: Context): DataStore<BookshelfSettings> =
        context.bookshelfSettingsDataStore

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
    fun provideViewerOperationSettingsDataStore(@ApplicationContext context: Context): DataStore<ViewerOperationSettings> =
        context.viewerOperationSettingsDataStore
}

