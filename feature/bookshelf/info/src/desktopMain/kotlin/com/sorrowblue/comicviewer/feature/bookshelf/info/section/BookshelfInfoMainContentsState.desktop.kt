package com.sorrowblue.comicviewer.feature.bookshelf.info.section

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.paging.PagingData
import com.sorrowblue.cmpdestinations.result.NavResult
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.feature.bookshelf.info.notification.NotificationRequestResult
import com.sorrowblue.comicviewer.feature.bookshelf.info.notification.ScanType
import com.sorrowblue.comicviewer.framework.ui.EventFlow
import comicviewer.feature.bookshelf.info.generated.resources.Res
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_label_scanning_file
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_label_scanning_thumbnails
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import logcat.logcat
import org.jetbrains.compose.resources.getString

@Composable
internal actual fun rememberBookshelfInfoMainContentsState(
    bookshelfFolder: BookshelfFolder,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
    viewModel: BookshelfInfoMainContentsViewModel,
): BookshelfInfoMainContentsState {
    val stateImpl = remember(bookshelfFolder, viewModel) {
        BookshelfInfoMainContentsStateImpl(
            bookshelfFolder = bookshelfFolder,
            snackbarHostState = snackbarHostState,
            scope = coroutineScope,
            pagingDataFlow = viewModel.pagingDataFlow(bookshelfFolder.bookshelf.id)
        )
    }
    return stateImpl
}

private class BookshelfInfoMainContentsStateImpl(
    bookshelfFolder: BookshelfFolder,
    private val snackbarHostState: SnackbarHostState,
    private val scope: CoroutineScope,
    override val pagingDataFlow: Flow<PagingData<BookThumbnail>>,
) : BookshelfInfoMainContentsState {

    private lateinit var currentScanType: ScanType

    override val events = EventFlow<BookshelfInfoMainContentsEvent>()

    override var uiState by mutableStateOf(
        BookshelfInfoMainContentsUiState(
            bookshelf = bookshelfFolder.bookshelf,
            folder = bookshelfFolder.folder
        )
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

    override fun onNotificationRequestResult(result: NavResult<NotificationRequestResult>) {
        logcat { "onNotificationResult(result: $result)" }
        when (result) {
            NavResult.Canceled -> Unit
            is NavResult.Value -> when (result.value) {
                NotificationRequestResult.Ok -> Unit
                NotificationRequestResult.Cancel -> Unit
                NotificationRequestResult.NotAllowed -> when (currentScanType) {
                    ScanType.File -> scanFile()
                    ScanType.Thumbnail -> scanThumbnail()
                }
            }
        }
    }

    private fun scanFile() {
        showSnackbar()
//        ScanFileWorker.enqueueUniqueWork(context, uiState.bookshelf.id)
    }

    private fun scanThumbnail() {
        showSnackbar()
//        RegenerateThumbnailsWorker.enqueueUniqueWork(context, uiState.bookshelf.id)
    }

    private fun showSnackbar() {
        scope.launch {
            snackbarHostState.showSnackbar(
                getString(
                    when (currentScanType) {
                        ScanType.File -> Res.string.bookshelf_info_label_scanning_file
                        ScanType.Thumbnail -> Res.string.bookshelf_info_label_scanning_thumbnails
                    }
                )
            )
        }
    }
}
