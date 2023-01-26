package com.sorrowblue.comicviewer.data.reporitory

import com.sorrowblue.comicviewer.data.datasource.DatastoreDataSource
import com.sorrowblue.comicviewer.domain.entity.settings.DisplaySettings
import com.sorrowblue.comicviewer.domain.entity.settings.History
import com.sorrowblue.comicviewer.domain.entity.settings.ViewerOperationSettings
import com.sorrowblue.comicviewer.domain.entity.settings.ViewerSettings
import com.sorrowblue.comicviewer.domain.entity.settings.BookshelfDisplaySettings
import com.sorrowblue.comicviewer.domain.entity.settings.BookshelfSettings
import com.sorrowblue.comicviewer.domain.entity.settings.SecuritySettings
import com.sorrowblue.comicviewer.domain.entity.settings.Settings
import com.sorrowblue.comicviewer.domain.repository.SettingsCommonRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

internal class SettingsCommonRepositoryImpl @Inject constructor(
    private val datastoreDataSource: DatastoreDataSource,
) : SettingsCommonRepository {

    override val history: Flow<History> = datastoreDataSource.history
    override suspend fun updateHistory(transform: suspend (History) -> History) {
        datastoreDataSource.updateHistory(transform)
    }

    override val settings: Flow<Settings> = datastoreDataSource.settings
    override suspend fun updateSettings(transform: suspend (Settings) -> Settings) {
        datastoreDataSource.updateSettings(transform)
    }

    override val displaySettings: Flow<DisplaySettings> = datastoreDataSource.displaySettings
    override suspend fun updateDisplaySettings(transform: suspend (DisplaySettings) -> DisplaySettings) {
        datastoreDataSource.updateDisplaySettings(transform)
    }

    override val viewerSettings: Flow<ViewerSettings> = datastoreDataSource.viewerSettings
    override suspend fun updateViewerSettings(transform: suspend (ViewerSettings) -> ViewerSettings) {
        datastoreDataSource.updateViewerSettings(transform)
    }

    override val bookshelfDisplaySettings: Flow<BookshelfDisplaySettings> =
        datastoreDataSource.bookshelfDisplaySettings

    override suspend fun updateBookshelfSettings(transform: suspend (BookshelfDisplaySettings) -> BookshelfDisplaySettings) {
        datastoreDataSource.updateBookshelfDisplaySettings(transform)
    }

    override val bookshelfSettings: Flow<BookshelfSettings> = datastoreDataSource.bookshelfSettings

    override suspend fun updateBookshelfSettings2(transform: suspend (BookshelfSettings) -> BookshelfSettings) {
        datastoreDataSource.updateBookshelfSettings(transform)
    }

    override val viewerOperationSettings: Flow<ViewerOperationSettings> =
        datastoreDataSource.viewerOperationSettings

    override suspend fun updateViewerOperationSettings(transform: suspend (ViewerOperationSettings) -> ViewerOperationSettings) {
        datastoreDataSource.updateViewerOperationSettings(transform)
    }

    override val securitySettings: Flow<SecuritySettings> = datastoreDataSource.securitySettings

    override suspend fun updateSecuritySettings(transform: suspend (SecuritySettings) -> SecuritySettings) {
        datastoreDataSource.updateSecuritySettings(transform)
    }
}
