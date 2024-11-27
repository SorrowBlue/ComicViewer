package com.sorrowblue.comicviewer.feature.bookshelf.info

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import androidx.activity.ComponentActivity
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
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.PagingData
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
import com.sorrowblue.comicviewer.feature.bookshelf.info.worker.FileScanRequest
import com.sorrowblue.comicviewer.feature.bookshelf.info.worker.FileScanWorker
import com.sorrowblue.comicviewer.feature.bookshelf.info.worker.RegenerateThumbnailsWorker
import com.sorrowblue.comicviewer.feature.bookshelf.notification.NotificationRequestResult
import com.sorrowblue.comicviewer.framework.ui.ScreenStateEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import logcat.logcat

internal sealed interface BookshelfInfoSheetStateEvent {
    data object Back : BookshelfInfoSheetStateEvent
    data object ShowRequestPermissionRationale : BookshelfInfoSheetStateEvent
    data class Edit(val id: BookshelfId) : BookshelfInfoSheetStateEvent
    data class Remove(val bookshelfId: BookshelfId) : BookshelfInfoSheetStateEvent
}

internal interface BookshelfInfoSheetState : ScreenStateEvent<BookshelfInfoSheetStateEvent> {
    val uiState: BookshelfInfoSheetUiState
    val pagingDataFlow: Flow<PagingData<BookThumbnail>>

    fun onAction(action: BookshelfInfoSheetAction)
    fun onRemoveResult(result: NavResult<Boolean>)
    fun onNotificationRequestResult(result: NavResult<NotificationRequestResult>)
}

@Composable
internal fun rememberBookshelfInfoSheetState(
    bookshelfFolder: BookshelfFolder,
    snackbarHostState: SnackbarHostState = LocalSnackbarHostState.current,
    context: Context = LocalContext.current,
    scope: CoroutineScope = rememberCoroutineScope(),
    viewModel: BookshelfInfoSheetViewModel = hiltViewModel(),
): BookshelfInfoSheetState {
    val stateImpl = remember(bookshelfFolder, viewModel) {
        BookshelfInfoSheetStateImpl(
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

private class BookshelfInfoSheetStateImpl(
    bookshelfFolder: BookshelfFolder,
    private val context: Context,
    private val snackbarHostState: SnackbarHostState,
    override val scope: CoroutineScope,
    override val pagingDataFlow: Flow<PagingData<BookThumbnail>>,
) : BookshelfInfoSheetState, NotificationPermissionRequest, IntentLauncher {

    override val activity = context as Activity

    override lateinit var intentLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>
    override lateinit var permissionLauncher: ManagedActivityResultLauncher<String, Boolean>

    override val event = MutableSharedFlow<BookshelfInfoSheetStateEvent>()

    override var uiState by mutableStateOf(
        BookshelfInfoSheetUiState(
            bookshelf = bookshelfFolder.bookshelf,
            folder = bookshelfFolder.folder
        )
    )
        private set

    override fun onAction(action: BookshelfInfoSheetAction) {
        when (action) {
            BookshelfInfoSheetAction.Close -> sendEvent(BookshelfInfoSheetStateEvent.Back)
            BookshelfInfoSheetAction.Edit -> sendEvent(BookshelfInfoSheetStateEvent.Edit(uiState.bookshelf.id))
            BookshelfInfoSheetAction.Remove -> sendEvent(BookshelfInfoSheetStateEvent.Remove(uiState.bookshelf.id))
            BookshelfInfoSheetAction.Scan -> requestScan()
            BookshelfInfoSheetAction.ThumbnailRegeneration -> recreateThumbnails()
        }
    }

    override fun onRemoveResult(result: NavResult<Boolean>) {
        logcat { "onRemoveResult(result: $result)" }
        when (result) {
            NavResult.Canceled -> Unit
            is NavResult.Value -> {
                if (result.value) {
                    scope.launch {
                        sendEvent(BookshelfInfoSheetStateEvent.Back)
                        snackbarHostState.showSnackbar(
                            context.getString(
                                R.string.bookshelf_info_msg_remove,
                                uiState.bookshelf.displayName
                            )
                        )
                    }
                }
            }
        }
    }

    fun onNotificationResult(result: Boolean) {
        logcat { "onNotificationResult(result: $result)" }
        if (result) {
            // 通知権限が許可された
            requestScan()
        } else {
            // 通知権限が許可されていない
            scan()
        }
    }

    override fun onNotificationRequestResult(result: NavResult<NotificationRequestResult>) {
        logcat { "onNotificationResult(result: $result)" }
        when (result) {
            NavResult.Canceled -> Unit
            is NavResult.Value -> when (result.value) {
                NotificationRequestResult.Ok -> launchNotification()
                NotificationRequestResult.Cancel -> Unit
                NotificationRequestResult.NotAllowed -> requestScan()
            }
        }
    }

    private fun recreateThumbnails() {
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

    private fun requestScan() {
        requestPermission(
            action = ::scan,
            showInContextUI = { sendEvent(BookshelfInfoSheetStateEvent.ShowRequestPermissionRationale) }
        )
    }

    private fun scan() {
        scope.launch {
            if (ContextCompat.checkSelfPermission(context as ComponentActivity, POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED
            ) {
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
}
