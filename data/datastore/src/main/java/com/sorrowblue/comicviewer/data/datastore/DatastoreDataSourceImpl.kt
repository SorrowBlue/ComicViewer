package com.sorrowblue.comicviewer.data.datastore

import androidx.datastore.core.DataStore
import com.sorrowblue.comicviewer.data.datasource.DatastoreDataSource
import com.sorrowblue.comicviewer.domain.model.DisplaySettings
import com.sorrowblue.comicviewer.domain.model.History
import com.sorrowblue.comicviewer.domain.model.ViewerOperationSettings
import com.sorrowblue.comicviewer.domain.model.ViewerSettings
import com.sorrowblue.comicviewer.domain.model.settings.BookshelfDisplaySettings
import com.sorrowblue.comicviewer.domain.model.settings.BookshelfSettings
import com.sorrowblue.comicviewer.domain.model.settings.Settings
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

internal class DatastoreDataSourceImpl @Inject constructor(
    private val historyDataStore: DataStore<History>,
    private val settingsDataStore: DataStore<Settings>,
    private val displaySettingsDataStore: DataStore<DisplaySettings>,
    private val viewerSettingsDataStore: DataStore<ViewerSettings>,
    private val bookshelfDisplaySettingsDataStore: DataStore<BookshelfDisplaySettings>,
    private val bookshelfSettingsDataStore: DataStore<BookshelfSettings>,
    private val viewerOperationSettingsDataStore: DataStore<ViewerOperationSettings>,
) : DatastoreDataSource {

    override val history: Flow<History> = historyDataStore.data
    override suspend fun updateHistory(transform: suspend (History) -> History) =
        historyDataStore.updateData(transform)

    override val settings = settingsDataStore.data
    override suspend fun updateSettings(transform: suspend (Settings) -> Settings) =
        settingsDataStore.updateData(transform)

    override val displaySettings = displaySettingsDataStore.data
    override suspend fun updateDisplaySettings(transform: suspend (DisplaySettings) -> DisplaySettings) =
        displaySettingsDataStore.updateData(transform)

    override val viewerSettings = viewerSettingsDataStore.data
    override suspend fun updateViewerSettings(transform: suspend (ViewerSettings) -> ViewerSettings) =
        viewerSettingsDataStore.updateData(transform)

    override val bookshelfDisplaySettings = bookshelfDisplaySettingsDataStore.data
    override suspend fun updateBookshelfDisplaySettings(transform: suspend (BookshelfDisplaySettings) -> BookshelfDisplaySettings) =
        bookshelfDisplaySettingsDataStore.updateData(transform)

    override val bookshelfSettings = bookshelfSettingsDataStore.data
    override suspend fun updateBookshelfSettings(transform: suspend (BookshelfSettings) -> BookshelfSettings) =
        bookshelfSettingsDataStore.updateData(transform)

    override val viewerOperationSettings = viewerOperationSettingsDataStore.data
    override suspend fun updateViewerOperationSettings(transform: suspend (ViewerOperationSettings) -> ViewerOperationSettings) =
        viewerOperationSettingsDataStore.updateData(transform)
}
