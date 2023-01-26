package com.sorrowblue.comicviewer.data.datasource

import com.sorrowblue.comicviewer.domain.entity.settings.BookshelfDisplaySettings
import com.sorrowblue.comicviewer.domain.entity.settings.BookshelfSettings
import com.sorrowblue.comicviewer.domain.entity.settings.DisplaySettings
import com.sorrowblue.comicviewer.domain.entity.settings.History
import com.sorrowblue.comicviewer.domain.entity.settings.SecuritySettings
import com.sorrowblue.comicviewer.domain.entity.settings.Settings
import com.sorrowblue.comicviewer.domain.entity.settings.ViewerOperationSettings
import com.sorrowblue.comicviewer.domain.entity.settings.ViewerSettings
import kotlinx.coroutines.flow.Flow

interface DatastoreDataSource {

    val history: Flow<History>
    suspend fun updateHistory(transform: suspend (History) -> History): History

    val settings: Flow<Settings>
    suspend fun updateSettings(transform: suspend (Settings) -> Settings): Settings

    val displaySettings: Flow<DisplaySettings>
    suspend fun updateDisplaySettings(transform: suspend (DisplaySettings) -> DisplaySettings): DisplaySettings

    val viewerSettings: Flow<ViewerSettings>
    suspend fun updateViewerSettings(transform: suspend (ViewerSettings) -> ViewerSettings): ViewerSettings

    val bookshelfDisplaySettings: Flow<BookshelfDisplaySettings>
    suspend fun updateBookshelfDisplaySettings(transform: suspend (BookshelfDisplaySettings) -> BookshelfDisplaySettings): BookshelfDisplaySettings

    val bookshelfSettings: Flow<BookshelfSettings>
    suspend fun updateBookshelfSettings(transform: suspend (BookshelfSettings) -> BookshelfSettings): BookshelfSettings

    val viewerOperationSettings: Flow<ViewerOperationSettings>
    suspend fun updateViewerOperationSettings(transform: suspend (ViewerOperationSettings) -> ViewerOperationSettings): ViewerOperationSettings

    val securitySettings: Flow<SecuritySettings>
    suspend fun updateSecuritySettings(transform: suspend (SecuritySettings) -> SecuritySettings): SecuritySettings
}
