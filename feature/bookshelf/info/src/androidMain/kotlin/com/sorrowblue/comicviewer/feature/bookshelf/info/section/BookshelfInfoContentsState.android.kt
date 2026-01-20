package com.sorrowblue.comicviewer.feature.bookshelf.info.section

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.paging.PagingConfig
import androidx.paging.compose.LazyPagingItems
import androidx.work.WorkManager
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.domain.usecase.file.PagingBookshelfBookUseCase
import com.sorrowblue.comicviewer.feature.bookshelf.info.BookshelfInfoScreenContext
import com.sorrowblue.comicviewer.feature.bookshelf.info.IntentLauncher
import com.sorrowblue.comicviewer.feature.bookshelf.info.NotificationPermissionRequest
import com.sorrowblue.comicviewer.feature.bookshelf.info.notification.ScanType
import com.sorrowblue.comicviewer.feature.bookshelf.info.worker.FileScanWorker
import com.sorrowblue.comicviewer.feature.bookshelf.info.worker.ThumbnailScanWorker
import com.sorrowblue.comicviewer.framework.ui.AppState
import com.sorrowblue.comicviewer.framework.ui.EventFlow
import com.sorrowblue.comicviewer.framework.ui.LocalAppState
import com.sorrowblue.comicviewer.framework.ui.paging.rememberPagingItems
import comicviewer.feature.bookshelf.info.generated.resources.Res
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_label_notification_settings
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_label_scanning_file
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_label_scanning_file_no_notification
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_label_scanning_thumbnails
import comicviewer.feature.bookshelf.info.generated.resources.bookshelf_info_label_scanning_thumbnails_no_notification
import kotlinx.coroutines.launch
import logcat.logcat
import org.jetbrains.compose.resources.getString

@Composable
context(context: BookshelfInfoScreenContext)
internal actual fun rememberBookshelfInfoContentsState(
    bookshelfFolder: BookshelfFolder,
): BookshelfInfoContentsState {
    @SuppressLint("ContextCastToActivity")
    val activity = LocalContext.current as Activity
    val appState = LocalAppState.current
    val stateImpl = remember(bookshelfFolder) {
        BookshelfInfoMainContentsStateImpl(
            bookshelfFolder = bookshelfFolder,
            activity = activity,
            appState = appState,
            workManager = context.workManager,
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

    stateImpl.permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
        stateImpl::onNotificationResult,
    )
    stateImpl.intentLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { }
    return stateImpl
}

private class BookshelfInfoMainContentsStateImpl(
    bookshelfFolder: BookshelfFolder,
    override val activity: Activity,
    private val appState: AppState,
    private val workManager: WorkManager,
) : BookshelfInfoContentsState,
    NotificationPermissionRequest,
    IntentLauncher {
    override lateinit var lazyPagingItems: LazyPagingItems<BookThumbnail>

    private lateinit var currentScanType: ScanType

    override lateinit var intentLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>
    override lateinit var permissionLauncher: ManagedActivityResultLauncher<String, Boolean>

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
        requestPermission(
            action = ::scanFile,
            showInContextUI = {
                events.tryEmit(
                    BookshelfInfoContentsEvent.ShowNotificationPermissionRationale(
                        ScanType.File,
                    ),
                )
            },
        )
    }

    override fun onScanThumbnailClick() {
        currentScanType = ScanType.Thumbnail
        requestPermission(
            action = ::scanThumbnail,
            showInContextUI = {
                events.tryEmit(
                    BookshelfInfoContentsEvent.ShowNotificationPermissionRationale(
                        ScanType.Thumbnail,
                    ),
                )
            },
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

    private fun scanFile() {
        showSnackbar()
        FileScanWorker.enqueueUniqueWork(workManager, uiState.bookshelf.id)
    }

    private fun scanThumbnail() {
        showSnackbar()
        ThumbnailScanWorker.enqueueUniqueWork(activity, uiState.bookshelf.id)
    }

    private fun showSnackbar() {
        appState.coroutineScope.launch {
            if (checkNotificationPermission()) {
                appState.snackbarHostState.showSnackbar(
                    getString(
                        when (currentScanType) {
                            ScanType.File -> Res.string.bookshelf_info_label_scanning_file
                            ScanType.Thumbnail -> Res.string.bookshelf_info_label_scanning_thumbnails
                        },
                    ),
                )
            } else {
                val result = appState.snackbarHostState.showSnackbar(
                    message = getString(
                        when (currentScanType) {
                            ScanType.File -> Res.string.bookshelf_info_label_scanning_file_no_notification
                            ScanType.Thumbnail -> Res.string.bookshelf_info_label_scanning_thumbnails_no_notification
                        },
                    ),
                    actionLabel = getString(Res.string.bookshelf_info_label_notification_settings),
                    duration = SnackbarDuration.Long,
                )
                when (result) {
                    SnackbarResult.Dismissed -> Unit
                    SnackbarResult.ActionPerformed -> {
                        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                            putExtra(Settings.EXTRA_APP_PACKAGE, activity.packageName)
                        }
                        launchIntent(intent)
                    }
                }
            }
        }
    }
}

private const val PageSize = 4
