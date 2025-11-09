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
import com.sorrowblue.comicviewer.framework.ui.EventFlow
import com.sorrowblue.comicviewer.framework.ui.GlobalSnackbarState
import com.sorrowblue.comicviewer.framework.ui.LocalGlobalSnackbarState
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.LocalCoroutineScope
import com.sorrowblue.comicviewer.framework.ui.paging.rememberPagingItems
import comicviewer.feature.bookshelf.info.generated.resources.Res
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_label_scanning_file
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_label_scanning_thumbnails
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString

@Composable
context(context: BookshelfInfoScreenContext)
internal actual fun rememberBookshelfInfoContentsState(
    bookshelfFolder: BookshelfFolder,
    coroutineScope: CoroutineScope,
): BookshelfInfoContentsState {
    val globalSnackbarState = LocalGlobalSnackbarState.current
    val appCoroutineScope = LocalCoroutineScope.current
    return remember(bookshelfFolder) {
        BookshelfInfoContentsStateImpl(
            bookshelfFolder = bookshelfFolder,
            globalSnackbarState = globalSnackbarState,
            appCoroutineScope = appCoroutineScope,
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
    bookshelfFolder: BookshelfFolder,
    private val globalSnackbarState: GlobalSnackbarState,
    private val appCoroutineScope: CoroutineScope,
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
        appCoroutineScope.launch {
            scanBookshelfUseCase.invoke(
                ScanBookshelfUseCase.Request(
                    bookshelfId = uiState.bookshelf.id,
                ) { bookshelf, file ->
                },
            )
        }
    }

    private fun scanThumbnail() {
        showSnackbar()
        appCoroutineScope.launch {
            regenerateThumbnailsUseCase.invoke(
                RegenerateThumbnailsUseCase.Request(
                    bookshelfId = uiState.bookshelf.id,
                ) { bookshelf, progress, max ->
                },
            )
        }
    }

    private fun showSnackbar() {
        appCoroutineScope.launch {
            globalSnackbarState.showSnackbar(
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
