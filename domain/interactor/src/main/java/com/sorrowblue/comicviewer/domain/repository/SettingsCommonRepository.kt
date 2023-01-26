package com.sorrowblue.comicviewer.domain.repository

import com.sorrowblue.comicviewer.domain.entity.settings.DisplaySettings
import com.sorrowblue.comicviewer.domain.entity.settings.History
import com.sorrowblue.comicviewer.domain.entity.settings.ViewerOperationSettings
import com.sorrowblue.comicviewer.domain.entity.settings.ViewerSettings
import com.sorrowblue.comicviewer.domain.entity.settings.BookshelfDisplaySettings
import com.sorrowblue.comicviewer.domain.entity.settings.BookshelfSettings
import com.sorrowblue.comicviewer.domain.entity.settings.SecuritySettings
import com.sorrowblue.comicviewer.domain.entity.settings.Settings
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

    val securitySettings: Flow<SecuritySettings>
    suspend fun updateSecuritySettings(transform: suspend (SecuritySettings) -> SecuritySettings)
}
