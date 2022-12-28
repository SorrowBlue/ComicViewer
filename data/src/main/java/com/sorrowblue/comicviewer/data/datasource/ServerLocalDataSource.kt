package com.sorrowblue.comicviewer.data.datasource

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.data.common.ServerFileModelFolder
import com.sorrowblue.comicviewer.data.common.ServerModelId
import com.sorrowblue.comicviewer.data.common.ServerModel
import com.sorrowblue.comicviewer.domain.model.DisplaySettings
import com.sorrowblue.comicviewer.domain.model.History
import com.sorrowblue.comicviewer.domain.model.ViewerOperationSettings
import com.sorrowblue.comicviewer.domain.model.ViewerSettings
import com.sorrowblue.comicviewer.domain.model.settings.BookshelfDisplaySettings
import com.sorrowblue.comicviewer.domain.model.settings.BookshelfSettings
import com.sorrowblue.comicviewer.domain.model.settings.Settings
import kotlinx.coroutines.flow.Flow

interface ServerLocalDataSource {

    suspend fun create(serverModel: ServerModel): ServerModel

    suspend fun delete(serverModel: ServerModel): Int

    suspend fun get(serverModelId: ServerModelId): ServerModel?

    fun pagingSource(pagingConfig: PagingConfig): Flow<PagingData<ServerFileModelFolder>>
}

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
}
