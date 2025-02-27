package com.sorrowblue.comicviewer.feature.bookshelf.info.section

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.sorrowblue.cmpdestinations.result.NavResultReceiver
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.file.FileThumbnail
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.bookshelf.info.notification.NotificationRequest
import com.sorrowblue.comicviewer.feature.bookshelf.info.notification.NotificationRequestResult
import com.sorrowblue.comicviewer.feature.bookshelf.info.notification.ScanType
import com.sorrowblue.comicviewer.file.component.FileThumbnailsCarousel
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.EventEffect
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.ExtraPaneScaffoldDefaults
import com.sorrowblue.comicviewer.framework.ui.layout.PaddingValuesSides
import com.sorrowblue.comicviewer.framework.ui.layout.only
import com.sorrowblue.comicviewer.framework.ui.layout.plus
import com.sorrowblue.comicviewer.framework.ui.paging.LazyPagingItems
import com.sorrowblue.comicviewer.framework.ui.paging.collectAsLazyPagingItems

internal sealed interface BookshelfInfoMainContentsEvent {
    data class ShowNotificationPermissionRationale(val type: ScanType) :
        BookshelfInfoMainContentsEvent
}

data class BookshelfInfoMainContentsUiState(
    val bookshelf: Bookshelf,
    val folder: Folder,
    val isScanningFile: Boolean = false,
    val isScanningThumbnail: Boolean = false,
)

@Composable
internal fun BookshelfInfoMainContents(
    bookshelfFolder: BookshelfFolder,
    showNotificationPermissionRationale: (ScanType) -> Unit,
    snackbarHostState: SnackbarHostState,
    contentPadding: PaddingValues,
    notificationNavResultReceiver: NavResultReceiver<NotificationRequest, NotificationRequestResult>,
    modifier: Modifier = Modifier,
    state: BookshelfInfoMainContentsState = rememberBookshelfInfoMainContentsState(
        bookshelfFolder = bookshelfFolder,
        snackbarHostState = snackbarHostState
    ),
) {
    val lazyPagingItems = state.pagingDataFlow.collectAsLazyPagingItems()
    BookshelfInfoMainContents(
        uiState = state.uiState,
        lazyPagingItems = lazyPagingItems,
        onScanFileClick = state::onScanFileClick,
        onScanThumbnailClick = state::onScanThumbnailClick,
        contentPadding = contentPadding,
        modifier = modifier
    )

    notificationNavResultReceiver.onNavResult(state::onNotificationRequestResult)

    EventEffect(state.events) {
        when (it) {
            is BookshelfInfoMainContentsEvent.ShowNotificationPermissionRationale -> showNotificationPermissionRationale(
                it.type
            )
        }
    }
}

@Composable
internal fun BookshelfInfoMainContents(
    uiState: BookshelfInfoMainContentsUiState,
    lazyPagingItems: LazyPagingItems<out FileThumbnail>,
    onScanFileClick: () -> Unit,
    onScanThumbnailClick: () -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        FileThumbnailsCarousel(
            lazyPagingItems = lazyPagingItems,
            contentPadding = contentPadding.only(PaddingValuesSides.Horizontal + PaddingValuesSides.Top) +
                PaddingValues(horizontal = ExtraPaneScaffoldDefaults.HorizontalPadding),
            modifier = Modifier.fillMaxWidth()
        )
        BookshelfInfoActionChips(
            isScanningFile = uiState.isScanningFile,
            isScanningThumbnail = uiState.isScanningThumbnail,
            onScanFileClick = onScanFileClick,
            onScanThumbnailClick = onScanThumbnailClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding.only(PaddingValuesSides.Horizontal))
                .padding(top = ComicTheme.dimension.targetSpacing)
                .padding(horizontal = ExtraPaneScaffoldDefaults.HorizontalPadding)
        )
        BookshelfInfo(
            bookshelf = uiState.bookshelf,
            folder = uiState.folder,
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding.only(PaddingValuesSides.Horizontal))
        )
    }
}

@Composable
internal fun LoadingContents(modifier: Modifier = Modifier) {
    Box(contentAlignment = Alignment.Center, modifier = modifier) {
        CircularProgressIndicator()
    }
}

@Composable
internal fun ErrorContents(modifier: Modifier = Modifier) {
    Box(contentAlignment = Alignment.Center, modifier = modifier) {
        Text("Error")
    }
}
