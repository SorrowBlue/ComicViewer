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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import com.ramcosta.composedestinations.result.NavResult
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingBookshelfBookUseCase
import com.sorrowblue.comicviewer.feature.bookshelf.info.IntentLauncher
import com.sorrowblue.comicviewer.feature.bookshelf.info.NotificationPermissionRequest
import com.sorrowblue.comicviewer.feature.bookshelf.info.worker.FileScanRequest
import com.sorrowblue.comicviewer.feature.bookshelf.info.worker.FileScanWorker
import com.sorrowblue.comicviewer.feature.bookshelf.info.worker.RegenerateThumbnailsWorker
import com.sorrowblue.comicviewer.feature.bookshelf.notification.NotificationRequestResult
import com.sorrowblue.comicviewer.framework.ui.EventFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import logcat.logcat

@Composable
internal fun rememberBookshelfInfoMainContentsState(
    bookshelfFolder: BookshelfFolder,
    snackbarHostState: SnackbarHostState,
    context: Context = LocalContext.current,
    scope: CoroutineScope = rememberCoroutineScope(),
    viewModel: BookshelfInfoMainContentsViewModel = hiltViewModel(),
): BookshelfInfoMainContentsState {
    val stateImpl = remember(bookshelfFolder, viewModel) {
        BookshelfInfoMainContentsStateImpl(
            bookshelfFolder = bookshelfFolder,
            context = context,
            snackbarHostState = snackbarHostState,
            scope = scope,
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

internal interface BookshelfInfoMainContentsState {
    val uiState: BookshelfInfoMainContentsUiState
    val pagingDataFlow: Flow<PagingData<BookThumbnail>>

    val events: EventFlow<BookshelfInfoMainContentsEvent>

    fun onScanFileClick()
    fun onNotificationRequestResult(result: NavResult<NotificationRequestResult>)
    fun onScanThumbnailClick()
}

private class BookshelfInfoMainContentsStateImpl(
    bookshelfFolder: BookshelfFolder,
    private val context: Context,
    private val snackbarHostState: SnackbarHostState,
    private val scope: CoroutineScope,
    override val pagingDataFlow: Flow<PagingData<BookThumbnail>>,
) : BookshelfInfoMainContentsState, NotificationPermissionRequest, IntentLauncher {

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
        requestType = NotificationRequestType.ScanFile
        requestPermission(
            action = ::scanFile,
            showInContextUI = { events.tryEmit(BookshelfInfoMainContentsEvent.ShowNotificationPermissionRationale) }
        )
    }

    override fun onScanThumbnailClick() {
        requestType = NotificationRequestType.ScanThumbnail
        requestPermission(
            action = ::scanThumbnail,
            showInContextUI = { events.tryEmit(BookshelfInfoMainContentsEvent.ShowNotificationPermissionRationale) }
        )
    }

    private var requestType: NotificationRequestType? = null

    fun onNotificationResult(result: Boolean) {
        logcat { "onNotificationResult(result: $result) requestType: $requestType" }
        if (result) {
            // 通知権限が許可された
            when (requestType ?: return) {
                NotificationRequestType.ScanFile -> onScanFileClick()
                NotificationRequestType.ScanThumbnail -> onScanThumbnailClick()
            }
        } else {
            // 通知権限が許可されていない
            when (requestType ?: return) {
                NotificationRequestType.ScanFile -> scanFile()
                NotificationRequestType.ScanThumbnail -> scanThumbnail()
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
                NotificationRequestResult.NotAllowed -> when (requestType ?: return) {
                    NotificationRequestType.ScanFile -> onScanFileClick()
                    NotificationRequestType.ScanThumbnail -> onScanThumbnailClick()
                }
            }
        }
    }

    private fun scanFile() {
        scope.launch {
            if (checkNotificationPermission()) {
                snackbarHostState.showSnackbar("本棚のスキャンを開始します。")
            } else {
                val result = snackbarHostState.showSnackbar(
                    "本棚のスキャンを開始します。\n通知を許可すると進捗が確認できます。",
                    actionLabel = "通知設定",
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
        val constraints = Constraints.Builder().apply {
            // 有効なネットワーク接続が必要
            setRequiredNetworkType(NetworkType.CONNECTED)
            // ユーザーのデバイスの保存容量が少なすぎる場合以外
            setRequiresStorageNotLow(true)
        }.build()
        val myWorkRequest = OneTimeWorkRequest.Builder(FileScanWorker::class.java)
            .setConstraints(constraints)
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setInputData(FileScanRequest(uiState.bookshelf.id).toWorkData())
            .build()
        WorkManager.getInstance(context)
            .enqueueUniqueWork("scan", ExistingWorkPolicy.KEEP, myWorkRequest)
    }

    private fun scanThumbnail() {
        scope.launch {
            if (checkNotificationPermission()) {
                snackbarHostState.showSnackbar("サムネイルのスキャンを開始します。")
            } else {
                val result = snackbarHostState.showSnackbar(
                    "サムネイルのスキャンを開始します。\n通知を許可すると進捗が確認できます。",
                    actionLabel = "通知設定",
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
        val constraints = Constraints.Builder().apply {
            // 有効なネットワーク接続が必要
            setRequiredNetworkType(NetworkType.CONNECTED)
            // ユーザーのデバイスの保存容量が少なすぎる場合以外
            setRequiresStorageNotLow(true)
        }.build()
        val myWorkRequest = OneTimeWorkRequest.Builder(RegenerateThumbnailsWorker::class.java)
            .setConstraints(constraints)
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setInputData(FileScanRequest(uiState.bookshelf.id).toWorkData())
            .build()
        WorkManager.getInstance(context)
            .enqueueUniqueWork("scan2", ExistingWorkPolicy.KEEP, myWorkRequest)
    }

    private enum class NotificationRequestType {
        ScanFile,
        ScanThumbnail,
    }
}

@HiltViewModel
internal class BookshelfInfoMainContentsViewModel @Inject constructor(
    private val pagingBookshelfBookUseCase: PagingBookshelfBookUseCase,
) : ViewModel() {

    private var bookshelfThumbnailPagingDataFlow: Pair<BookshelfId, Flow<PagingData<BookThumbnail>>>? =
        null

    fun pagingDataFlow(id: BookshelfId) =
        if (bookshelfThumbnailPagingDataFlow?.first == id) {
            bookshelfThumbnailPagingDataFlow?.second!!
        } else {
            pagingBookshelfBookUseCase(
                PagingBookshelfBookUseCase.Request(id, PagingConfig(10))
            ).cachedIn(viewModelScope).also {
                bookshelfThumbnailPagingDataFlow = id to it
            }
        }
}
