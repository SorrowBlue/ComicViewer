/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.data.datastore.di

import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.core.okio.OkioStorage
import com.sorrowblue.comicviewer.data.datastore.serializer.OkioKSerializer
import com.sorrowblue.comicviewer.data.datastore.serializer.okioKSerializer
import com.sorrowblue.comicviewer.domain.model.common.IoDispatcher
import com.sorrowblue.comicviewer.domain.model.settings.BookSettings
import com.sorrowblue.comicviewer.domain.model.settings.CollectionSettings
import com.sorrowblue.comicviewer.domain.model.settings.DisplaySettings
import com.sorrowblue.comicviewer.domain.model.settings.FolderSettings
import com.sorrowblue.comicviewer.domain.model.settings.SecuritySettings
import com.sorrowblue.comicviewer.domain.model.settings.Settings
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderDisplaySettings
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.filesDir
import io.github.vinceglb.filekit.path
import io.github.vinceglb.filekit.resolve
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.SupervisorJob
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.SYSTEM

/**
 * Provides DataStore instances and serializers for application settings.
 */
@ContributesTo(AppScope::class)
@BindingContainer
object DataStoreModule {

    @SingleIn(AppScope::class)
    @Provides
    private fun <T> provideDataStore(
        serializer: OkioKSerializer<T>,
        @IoDispatcher dispatcher: CoroutineDispatcher,
    ): DataStore<T> = DataStore.Builder(
        storage = OkioStorage(
            fileSystem = FileSystem.SYSTEM,
            producePath = {
                FileKit.filesDir
                    .resolve("datastore")
                    .resolve(serializer.fileName)
                    .path.toPath()
            },
            serializer = serializer,
        ),
        context = dispatcher + SupervisorJob(),
    ).setCorruptionHandler(ReplaceFileCorruptionHandler { serializer.defaultValue })
        .build()

    @Provides
    private fun provideSettingsSerializer(): OkioKSerializer<Settings> =
        okioKSerializer("settings.pb", Settings())

    @Provides
    private fun provideDisplaySettingsSerializer(): OkioKSerializer<DisplaySettings> =
        okioKSerializer("displaySettings.pb", DisplaySettings())

    @Provides
    private fun provideBookSettingsSerializer(): OkioKSerializer<BookSettings> =
        okioKSerializer("bookSettings.pb", BookSettings())

    @Provides
    private fun provideFolderDisplaySettingsSerializer(): OkioKSerializer<FolderDisplaySettings> =
        okioKSerializer("folder_display_settings.pb", FolderDisplaySettings())

    @Provides
    private fun provideFolderSettingsSerializer(): OkioKSerializer<FolderSettings> =
        okioKSerializer("folder_settings.pb", FolderSettings())

    @Provides
    private fun provideSecuritySettingsSerializer(): OkioKSerializer<SecuritySettings> =
        okioKSerializer("securitySettings.pb", SecuritySettings())

    @Provides
    private fun provideCollectionSettingsSerializer(): OkioKSerializer<CollectionSettings> =
        okioKSerializer("collectionSettings.pb", CollectionSettings())
}
