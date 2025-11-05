package com.sorrowblue.comicviewer.feature.bookshelf.info.section

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.compose.LazyPagingItems
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.file.FileThumbnail
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.bookshelf.info.BookshelfInfoScreenContext
import com.sorrowblue.comicviewer.feature.bookshelf.info.notification.ScanType
import com.sorrowblue.comicviewer.file.component.FileThumbnailsCarousel
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.EventEffect
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.ExtraPaneScaffoldDefaults
import com.sorrowblue.comicviewer.framework.ui.layout.PaddingValuesSides
import com.sorrowblue.comicviewer.framework.ui.layout.only
import com.sorrowblue.comicviewer.framework.ui.layout.plus

internal data class BookshelfInfoContentsUiState(
    val bookshelf: Bookshelf,
    val folder: Folder,
    val isScanningFile: Boolean = false,
    val isScanningThumbnail: Boolean = false,
)

@Composable
context(context: BookshelfInfoScreenContext)
internal fun BookshelfInfoContents(
    bookshelfFolder: BookshelfFolder,
    showNotificationPermissionRationale: (ScanType) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val state = rememberBookshelfInfoContentsState(bookshelfFolder)
    BookshelfInfoContents(
        uiState = state.uiState,
        lazyPagingItems = state.lazyPagingItems,
        onScanFileClick = state::onScanFileClick,
        onScanThumbnailClick = state::onScanThumbnailClick,
        contentPadding = contentPadding,
        modifier = modifier
    )

    EventEffect(state.events) {
        when (it) {
            is BookshelfInfoContentsEvent.ShowNotificationPermissionRationale -> showNotificationPermissionRationale(
                it.type
            )
        }
    }
}

@Composable
internal fun BookshelfInfoContents(
    uiState: BookshelfInfoContentsUiState,
    lazyPagingItems: LazyPagingItems<out FileThumbnail>,
    onScanFileClick: () -> Unit,
    onScanThumbnailClick: () -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        FileThumbnailsCarousel(
            lazyPagingItems = lazyPagingItems,
            contentPadding = contentPadding.only(PaddingValuesSides.Horizontal + PaddingValuesSides.Top).plus(
                PaddingValues(horizontal = ExtraPaneScaffoldDefaults.HorizontalPadding)),
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
