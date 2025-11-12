package com.sorrowblue.comicviewer.feature.bookshelf.info.section

import androidx.compose.runtime.Composable
import androidx.paging.compose.LazyPagingItems
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.feature.bookshelf.info.BookshelfInfoScreenContext
import com.sorrowblue.comicviewer.feature.bookshelf.info.notification.ScanType
import com.sorrowblue.comicviewer.framework.ui.EventFlow

internal sealed interface BookshelfInfoContentsEvent {
    data class ShowNotificationPermissionRationale(val type: ScanType) :
        BookshelfInfoContentsEvent
}

internal interface BookshelfInfoContentsState {
    val uiState: BookshelfInfoContentsUiState
    val lazyPagingItems: LazyPagingItems<BookThumbnail>
    val events: EventFlow<BookshelfInfoContentsEvent>

    fun onScanFileClick()

    fun onScanThumbnailClick()
}

@Composable
context(context: BookshelfInfoScreenContext)
internal expect fun rememberBookshelfInfoContentsState(
    bookshelfFolder: BookshelfFolder,
): BookshelfInfoContentsState
