package com.sorrowblue.comicviewer.feature.bookshelf.info.section

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.paging.PagingData
import com.sorrowblue.cmpdestinations.result.NavResult
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.feature.bookshelf.info.IntentLauncher
import com.sorrowblue.comicviewer.feature.bookshelf.info.NotificationPermissionRequest
import com.sorrowblue.comicviewer.feature.bookshelf.info.notification.NotificationRequestResult
import com.sorrowblue.comicviewer.feature.bookshelf.info.notification.ScanType
import com.sorrowblue.comicviewer.feature.bookshelf.info.worker.RegenerateThumbnailsWorker
import com.sorrowblue.comicviewer.feature.bookshelf.info.worker.ScanFileWorker
import com.sorrowblue.comicviewer.framework.ui.EventFlow
import comicviewer.feature.bookshelf.info.generated.resources.Res
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_label_notification_settings
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_label_scanning_file
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_label_scanning_file_no_notification
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_label_scanning_thumbnails
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_label_scanning_thumbnails_no_notification
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
    val context = LocalContext.current
    val stateImpl = remember(bookshelfFolder, viewModel) {
        BookshelfInfoMainContentsStateImpl(
            bookshelfFolder = bookshelfFolder,
            context = context,
            snackbarHostState = snackbarHostState,
            scope = coroutineScope,
            pagingDataFlow = viewModel.pagingDataFlow(bookshelfFolder.bookshelf.id)
        )
    }

    stateImpl.permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
        stateImpl::onNotificationResult
    )
    stateImpl.intentLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { }
    return stateImpl
}

private class BookshelfInfoMainContentsStateImpl(
    bookshelfFolder: BookshelfFolder,
    private val context: Context,
    private val snackbarHostState: SnackbarHostState,
    private val scope: CoroutineScope,
    override val pagingDataFlow: Flow<PagingData<BookThumbnail>>,
) : BookshelfInfoMainContentsState, NotificationPermissionRequest, IntentLauncher {

    private lateinit var currentScanType: ScanType

    override val activity = context as Activity

    override lateinit var intentLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>
    override lateinit var permissionLauncher: ManagedActivityResultLauncher<String, Boolean>

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
        requestPermission(
            action = ::scanFile,
            showInContextUI = {
                events.tryEmit(
                    BookshelfInfoMainContentsEvent.ShowNotificationPermissionRationale(
                        ScanType.File
                    )
                )
            }
        )
    }

    override fun onScanThumbnailClick() {
        currentScanType = ScanType.Thumbnail
        requestPermission(
            action = ::scanThumbnail,
            showInContextUI = {
                events.tryEmit(
                    BookshelfInfoMainContentsEvent.ShowNotificationPermissionRationale(
                        ScanType.Thumbnail
                    )
                )
            }
        )
    }

    fun onNotificationResult(result: Boolean) {
        logcat { "onNotificationResult(result: $result) notificationRequestType: $currentScanType" }
        if (result) {
            when (currentScanType) {
                ScanType.File -> onScanFileClick()
                ScanType.Thumbnail -> onScanThumbnailClick()
            }
        } else {
            when (currentScanType) {
                ScanType.File -> scanFile()
                ScanType.Thumbnail -> scanThumbnail()
            }
        }
    }

    override fun onNotificationRequestResult(result: NavResult<NotificationRequestResult>) {
        logcat { "onNotificationResult(result: $result)" }
        when (result) {
            NavResult.Canceled -> Unit
            is NavResult.Value -> when (result.value) {
                NotificationRequestResult.Ok -> launchNotification()
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
        ScanFileWorker.enqueueUniqueWork(context, uiState.bookshelf.id)
    }

    private fun scanThumbnail() {
        showSnackbar()
        RegenerateThumbnailsWorker.enqueueUniqueWork(context, uiState.bookshelf.id)
    }

    private fun showSnackbar() {
        scope.launch {
            if (checkNotificationPermission()) {
                snackbarHostState.showSnackbar(
                    getString(
                        when (currentScanType) {
                            ScanType.File -> Res.string.bookshelf_info_label_scanning_file
                            ScanType.Thumbnail -> Res.string.bookshelf_info_label_scanning_thumbnails
                        }
                    )
                )
            } else {
                val result = snackbarHostState.showSnackbar(
                    message = getString(
                        when (currentScanType) {
                            ScanType.File -> Res.string.bookshelf_info_label_scanning_file_no_notification
                            ScanType.Thumbnail -> Res.string.bookshelf_info_label_scanning_thumbnails_no_notification
                        }
                    ),
                    actionLabel = getString(Res.string.bookshelf_info_label_notification_settings),
                    duration = SnackbarDuration.Long
                )
                when (result) {
                    SnackbarResult.Dismissed -> Unit
                    SnackbarResult.ActionPerformed -> {
                        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                        }
                        launchIntent(intent)
                    }
                }
            }
        }
    }
}
