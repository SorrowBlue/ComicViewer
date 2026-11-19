/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.data.datastore.impl

import androidx.datastore.core.DataStore
import com.sorrowblue.comicviewer.domain.model.settings.BookSettings
import com.sorrowblue.comicviewer.domain.model.settings.CollectionSettings
import com.sorrowblue.comicviewer.domain.model.settings.DisplaySettings
import com.sorrowblue.comicviewer.domain.model.settings.FolderSettings
import com.sorrowblue.comicviewer.domain.model.settings.SecuritySettings
import com.sorrowblue.comicviewer.domain.model.settings.Settings
import com.sorrowblue.comicviewer.domain.model.settings.ViewerSettings
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderDisplaySettings
import com.sorrowblue.comicviewer.domain.repository.SettingsRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow

@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
internal class SettingsRepositoryImpl(
    private val settingsDataStore: DataStore<Settings>,
    private val displaySettingsDataStore: DataStore<DisplaySettings>,
    private val viewerSettingsDataStore: DataStore<ViewerSettings>,
    private val bookSettingsDataStore: DataStore<BookSettings>,
    private val folderDisplaySettingsDataStore: DataStore<FolderDisplaySettings>,
    private val folderSettingsDataStore: DataStore<FolderSettings>,
    private val collectionSettingsDataStore: DataStore<CollectionSettings>,
    private val securitySettingsDataStore: DataStore<SecuritySettings>,
) : SettingsRepository {
    override val settings = settingsDataStore.data

    override suspend fun updateSettings(transform: suspend (Settings) -> Settings) {
        settingsDataStore.updateData(transform)
    }

    override val displaySettings = displaySettingsDataStore.data

    override suspend fun updateDisplaySettings(
        transform: suspend (DisplaySettings) -> DisplaySettings,
    ) {
        displaySettingsDataStore.updateData(transform)
    }

    override val viewerSettings = viewerSettingsDataStore.data

    override suspend fun updateViewerSettings(
        transform: suspend (ViewerSettings) -> ViewerSettings,
    ) {
        viewerSettingsDataStore.updateData(transform)
    }

    override val bookSettings: Flow<BookSettings> = bookSettingsDataStore.data

    override suspend fun updateBookSettings(transform: suspend (BookSettings) -> BookSettings) {
        bookSettingsDataStore.updateData(transform)
    }

    override val folderDisplaySettings = folderDisplaySettingsDataStore.data

    override suspend fun updateFolderDisplaySettings(
        transform: suspend (FolderDisplaySettings) -> FolderDisplaySettings,
    ) {
        folderDisplaySettingsDataStore.updateData(transform)
    }

    override val folderSettings = folderSettingsDataStore.data

    override suspend fun updateFolderSettings(
        transform: suspend (FolderSettings) -> FolderSettings,
    ) {
        folderSettingsDataStore.updateData(transform)
    }

    override val securitySettings: Flow<SecuritySettings> = securitySettingsDataStore.data

    override suspend fun updateSecuritySettings(
        transform: suspend (SecuritySettings) -> SecuritySettings,
    ) {
        securitySettingsDataStore.updateData(transform)
    }

    override val collectionSettings: Flow<CollectionSettings> = collectionSettingsDataStore.data

    override suspend fun updateCollectionSettings(
        transform: suspend (CollectionSettings) -> CollectionSettings,
    ) {
        collectionSettingsDataStore.updateData(transform)
    }
}
