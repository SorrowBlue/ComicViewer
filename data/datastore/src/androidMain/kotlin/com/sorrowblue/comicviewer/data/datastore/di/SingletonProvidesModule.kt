package com.sorrowblue.comicviewer.data.datastore.di

import androidx.datastore.core.DataStore
import com.sorrowblue.comicviewer.data.datastore.qualifier.Book
import com.sorrowblue.comicviewer.data.datastore.qualifier.Display
import com.sorrowblue.comicviewer.data.datastore.qualifier.Folder
import com.sorrowblue.comicviewer.data.datastore.qualifier.FolderDisplay
import com.sorrowblue.comicviewer.data.datastore.qualifier.Security
import com.sorrowblue.comicviewer.data.datastore.qualifier.Viewer
import com.sorrowblue.comicviewer.data.datastore.qualifier.ViewerOperation
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
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.qualifier.qualifier

@Module
@InstallIn(SingletonComponent::class)
internal object SingletonProvidesModule : KoinComponent {

    @Singleton
    @Provides
    fun provideFolderDisplaySettingsDataStore(): DataStore<FolderDisplaySettings> =
        get<DataStore<FolderDisplaySettings>>(qualifier<FolderDisplay>())

    @Singleton
    @Provides
    fun provideFolderSettingsDataStore(): DataStore<FolderSettings> =
        get<DataStore<FolderSettings>>(qualifier<Folder>())

    @Singleton
    @Provides
    fun provideSettingsDataStore(): DataStore<Settings> =
        get<DataStore<Settings>>(qualifier<com.sorrowblue.comicviewer.data.datastore.qualifier.Settings>())

    @Singleton
    @Provides
    fun provideDisplaySettingsDataStore(): DataStore<DisplaySettings> =
        get<DataStore<DisplaySettings>>(qualifier<Display>())

    @Singleton
    @Provides
    fun provideViewerSettingsDataStore(): DataStore<ViewerSettings> =
        get<DataStore<ViewerSettings>>(qualifier<Viewer>())

    @Singleton
    @Provides
    fun provideBookSettingsDataStore(): DataStore<BookSettings> =
        get<DataStore<BookSettings>>(qualifier<Book>())

    @Singleton
    @Provides
    fun provideViewerOperationSettingsDataStore(): DataStore<ViewerOperationSettings> =
        get<DataStore<ViewerOperationSettings>>(qualifier<ViewerOperation>())

    @Singleton
    @Provides
    fun provideSecuritySettingsDataStore(): DataStore<SecuritySettings> =
        get<DataStore<SecuritySettings>>(qualifier<Security>())
}
