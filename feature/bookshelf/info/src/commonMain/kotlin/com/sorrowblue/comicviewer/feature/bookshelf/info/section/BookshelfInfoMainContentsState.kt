package com.sorrowblue.comicviewer.feature.bookshelf.info.section

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.feature.bookshelf.info.notification.NotificationRequestResult
import com.sorrowblue.comicviewer.framework.navigation.NavResult
import com.sorrowblue.comicviewer.framework.ui.EventFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import org.koin.compose.viewmodel.koinViewModel

internal interface BookshelfInfoMainContentsState {
    val uiState: BookshelfInfoMainContentsUiState
    val pagingDataFlow: Flow<PagingData<BookThumbnail>>
    val events: EventFlow<BookshelfInfoMainContentsEvent>
    fun onScanFileClick()

    fun onNotificationRequestResult(result: NavResult<NotificationRequestResult>)
    fun onScanThumbnailClick()
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
@Composable
internal expect fun rememberBookshelfInfoMainContentsState(
    bookshelfFolder: BookshelfFolder,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    viewModel: BookshelfInfoMainContentsViewModel = koinViewModel(),
): BookshelfInfoMainContentsState
