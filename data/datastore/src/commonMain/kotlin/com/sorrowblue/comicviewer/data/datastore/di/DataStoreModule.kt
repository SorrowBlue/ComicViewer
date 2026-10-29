/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.data.datastore.di

import androidx.datastore.core.DataStore
import com.sorrowblue.comicviewer.data.datastore.DataStoreMaker
import com.sorrowblue.comicviewer.data.datastore.qualifier.Book
import com.sorrowblue.comicviewer.data.datastore.qualifier.Collection
import com.sorrowblue.comicviewer.data.datastore.qualifier.Display
import com.sorrowblue.comicviewer.data.datastore.qualifier.Folder
import com.sorrowblue.comicviewer.data.datastore.qualifier.FolderDisplay
import com.sorrowblue.comicviewer.data.datastore.qualifier.GlobalSettings
import com.sorrowblue.comicviewer.data.datastore.qualifier.Security
import com.sorrowblue.comicviewer.data.datastore.qualifier.Viewer
import com.sorrowblue.comicviewer.data.datastore.serializer.BookSettingsSerializer
import com.sorrowblue.comicviewer.data.datastore.serializer.CollectionSettingsSerializer
import com.sorrowblue.comicviewer.data.datastore.serializer.DisplaySettingsSerializer
import com.sorrowblue.comicviewer.data.datastore.serializer.FolderDisplaySettingsSerializer
import com.sorrowblue.comicviewer.data.datastore.serializer.FolderSettingsSerializer
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
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@ContributesTo(AppScope::class)
@BindingContainer
object DataStoreModule {
    @Book
    @SingleIn(AppScope::class)
    @Provides
    private fun DataStoreMaker.bookSettingsDataStore(): DataStore<BookSettings> =
        createDataStore(BookSettingsSerializer)

    @Display
    @SingleIn(AppScope::class)
    @Provides
    private fun DataStoreMaker.provideDisplaySettingsDataStore(): DataStore<DisplaySettings> =
        createDataStore(DisplaySettingsSerializer)

    @FolderDisplay
    @SingleIn(AppScope::class)
    @Provides
    private fun DataStoreMaker.folderDisplaySettingsDataStore(): DataStore<FolderDisplaySettings> =
        createDataStore(FolderDisplaySettingsSerializer)

    @Folder
    @SingleIn(AppScope::class)
    @Provides
    private fun DataStoreMaker.folderSettingsDataStore(): DataStore<FolderSettings> =
        createDataStore(FolderSettingsSerializer)

    @Security
    @SingleIn(AppScope::class)
    @Provides
    private fun DataStoreMaker.securitySettingsDataStore(): DataStore<SecuritySettings> =
        createDataStore(SecuritySettingsSerializer)

    @GlobalSettings
    @SingleIn(AppScope::class)
    @Provides
    private fun DataStoreMaker.provideSettingsDataStore(): DataStore<Settings> =
        createDataStore(SettingsSerializer)

    @Viewer
    @SingleIn(AppScope::class)
    @Provides
    private fun DataStoreMaker.viewerSettingsDataStore(): DataStore<ViewerSettings> =
        createDataStore(ViewerSettingsSerializer)

    @Collection
    @SingleIn(AppScope::class)
    @Provides
    private fun DataStoreMaker.collectionSettingsDataStore(): DataStore<CollectionSettings> =
        createDataStore(CollectionSettingsSerializer)
}
