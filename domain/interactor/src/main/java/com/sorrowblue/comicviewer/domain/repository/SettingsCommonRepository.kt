package com.sorrowblue.comicviewer.domain.repository

import com.sorrowblue.comicviewer.domain.model.DisplaySettings
import com.sorrowblue.comicviewer.domain.model.History
import com.sorrowblue.comicviewer.domain.model.ViewerOperationSettings
import com.sorrowblue.comicviewer.domain.model.ViewerSettings
import com.sorrowblue.comicviewer.domain.model.settings.BookshelfDisplaySettings
import com.sorrowblue.comicviewer.domain.model.settings.BookshelfSettings
import com.sorrowblue.comicviewer.domain.model.settings.Settings
import kotlinx.coroutines.flow.Flow

interface SettingsCommonRepository {

    val history: Flow<History>
    suspend fun updateHistory(transform: suspend (History) -> History)

    val displaySettings: Flow<DisplaySettings>
    suspend fun updateDisplaySettings(transform: suspend (DisplaySettings) -> DisplaySettings)

    val viewerSettings: Flow<ViewerSettings>
    suspend fun updateViewerSettings(transform: suspend (ViewerSettings) -> ViewerSettings)

    val viewerOperationSettings: Flow<ViewerOperationSettings>
    suspend fun updateViewerOperationSettings(transform: suspend (ViewerOperationSettings) -> ViewerOperationSettings)

    val bookshelfDisplaySettings: Flow<BookshelfDisplaySettings>
    suspend fun updateBookshelfSettings(transform: suspend (BookshelfDisplaySettings) -> BookshelfDisplaySettings)

    val bookshelfSettings: Flow<BookshelfSettings>
    suspend fun updateBookshelfSettings2(transform: suspend (BookshelfSettings) -> BookshelfSettings)
    val settings: Flow<Settings>
    suspend fun updateSettings(transform: suspend (Settings) -> Settings)
}
