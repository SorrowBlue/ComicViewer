package com.sorrowblue.comicviewer.feature.bookshelf.info.section

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.paging.PagingConfig
import androidx.paging.compose.LazyPagingItems
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.RegenerateThumbnailsUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.ScanBookshelfUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.PagingBookshelfBookUseCase
import com.sorrowblue.comicviewer.feature.bookshelf.info.BookshelfInfoScreenContext
import com.sorrowblue.comicviewer.feature.bookshelf.info.notification.ScanType
import com.sorrowblue.comicviewer.framework.notification.DesktopNotification
import com.sorrowblue.comicviewer.framework.ui.AppState
import com.sorrowblue.comicviewer.framework.ui.EventFlow
import com.sorrowblue.comicviewer.framework.ui.LocalAppState
import com.sorrowblue.comicviewer.framework.ui.paging.rememberPagingItems
import comicviewer.feature.bookshelf.info.generated.resources.Res
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_label_scanning_file
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_label_scanning_thumbnails
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_notification_title_file_scan_completed
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_notification_title_thumbnail_scan_completed
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

@Composable
context(context: BookshelfInfoScreenContext)
internal actual fun rememberBookshelfInfoContentsState(
    bookshelfFolder: BookshelfFolder,
): BookshelfInfoContentsState {
    val appState = LocalAppState.current
    return remember(bookshelfFolder) {
        BookshelfInfoContentsStateImpl(
            bookshelfFolder = bookshelfFolder,
            appState = appState,
            regenerateThumbnailsUseCase = context.regenerateThumbnailsUseCase,
            scanBookshelfUseCase = context.scanBookshelfUseCase,
        )
    }.apply {
        lazyPagingItems = rememberPagingItems {
            context.pagingBookshelfBookUseCase(
                PagingBookshelfBookUseCase.Request(
                    bookshelfFolder.bookshelf.id,
                    PagingConfig(PageSize),
                ),
            )
        }
    }
}

private class BookshelfInfoContentsStateImpl(
    private val bookshelfFolder: BookshelfFolder,
    private val appState: AppState,
    private val scanBookshelfUseCase: ScanBookshelfUseCase,
    private val regenerateThumbnailsUseCase: RegenerateThumbnailsUseCase,
) : BookshelfInfoContentsState {
    override lateinit var lazyPagingItems: LazyPagingItems<BookThumbnail>

    private lateinit var currentScanType: ScanType

    override val events = EventFlow<BookshelfInfoContentsEvent>()

    override var uiState by mutableStateOf(
        BookshelfInfoContentsUiState(
            bookshelf = bookshelfFolder.bookshelf,
            folder = bookshelfFolder.folder,
        ),
    )
        private set

    override fun onScanFileClick() {
        currentScanType = ScanType.File
        scanFile()
    }

    override fun onScanThumbnailClick() {
        currentScanType = ScanType.Thumbnail
        scanThumbnail()
    }

    private fun scanFile() {
        showSnackbar()
        appState.coroutineScope.launch {
            scanBookshelfUseCase.invoke(
                ScanBookshelfUseCase.Request(bookshelfId = uiState.bookshelf.id) { _, _ -> },
            )
            DesktopNotification().notify(
                getString(Res.string.bookshelf_info_notification_title_file_scan_completed),
                bookshelfFolder.bookshelf.displayName,
            )
        }
    }

    private fun scanThumbnail() {
        showSnackbar()
        appState.coroutineScope.launch {
            regenerateThumbnailsUseCase.invoke(
                RegenerateThumbnailsUseCase.Request(
                    bookshelfId = uiState.bookshelf.id,
                ) { bookshelf, progress, max ->
                },
            )
            DesktopNotification().notify(
                getString(Res.string.bookshelf_info_notification_title_thumbnail_scan_completed),
                bookshelfFolder.bookshelf.displayName,
            )
        }
    }

    private fun showSnackbar() {
        appState.coroutineScope.launch {
            appState.snackbarHostState.showSnackbar(
                message = getString(
                    when (currentScanType) {
                        ScanType.File -> Res.string.bookshelf_info_label_scanning_file
                        ScanType.Thumbnail -> Res.string.bookshelf_info_label_scanning_thumbnails
                    },
                ),
            )
        }
    }
}

private const val PageSize = 4
