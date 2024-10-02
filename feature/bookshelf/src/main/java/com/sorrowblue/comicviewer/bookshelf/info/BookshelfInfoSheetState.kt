package com.sorrowblue.comicviewer.bookshelf.info

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.PagingData
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.ramcosta.composedestinations.result.NavResult
import com.sorrowblue.comicviewer.bookshelf.FileScanRequest
import com.sorrowblue.comicviewer.bookshelf.FileScanWorker
import com.sorrowblue.comicviewer.bookshelf.section.NotificationRequestResult
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.feature.bookshelf.R
import com.sorrowblue.comicviewer.framework.ui.ScreenStateEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import logcat.logcat

internal sealed interface BookshelfInfoSheetStateEvent {
    data object ShowRequestPermissionRationale : BookshelfInfoSheetStateEvent
    data class Edit(val id: BookshelfId) : BookshelfInfoSheetStateEvent
    data class Remove(val bookshelf: Bookshelf) : BookshelfInfoSheetStateEvent
}

internal interface BookshelfInfoSheetState : ScreenStateEvent<BookshelfInfoSheetStateEvent> {
    val uiState: BookshelfInfoSheetUiState
    val pagingDataFlow: Flow<PagingData<BookThumbnail>>

    fun onAction(action: BookshelfInfoSheetAction)
    fun onNotificationPermissionResult(result: Boolean)
    fun onRemoveResult(result: NavResult<Boolean>)
    fun onNotificationResult(result: NavResult<NotificationRequestResult>)
}

@Composable
internal fun rememberBookshelfInfoSheetState(
    bookshelfFolder: BookshelfFolder,
    navigator: ThreePaneScaffoldNavigator<BookshelfFolder>,
    snackbarHostState: SnackbarHostState,
    context: Context = LocalContext.current,
    scope: CoroutineScope = rememberCoroutineScope(),
    viewModel: BookshelfInfoSheetViewModel = hiltViewModel(),
): BookshelfInfoSheetState {
    val stateImpl = remember(bookshelfFolder, viewModel) {
        BookshelfInfoSheetStateImpl(
            bookshelfFolder = bookshelfFolder,
            navigator = navigator,
            context = context,
            snackbarHostState = snackbarHostState,
            scope = scope,
            pagingDataFlow = viewModel.pagingDataFlow(bookshelfFolder.bookshelf.id)
        )
    }
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
        stateImpl::onNotificationResult
    )
    val settingsLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { }
    stateImpl.launcher = launcher
    stateImpl.settingsLauncher = settingsLauncher
    return stateImpl
}

private class BookshelfInfoSheetStateImpl(
    bookshelfFolder: BookshelfFolder,
    private val navigator: ThreePaneScaffoldNavigator<BookshelfFolder>,
    private val context: Context,
    private val snackbarHostState: SnackbarHostState,
    override val scope: CoroutineScope,
    override val pagingDataFlow: Flow<PagingData<BookThumbnail>>,
) : BookshelfInfoSheetState {

    lateinit var launcher: ManagedActivityResultLauncher<String, Boolean>
    lateinit var settingsLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>

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
            BookshelfInfoSheetAction.Close -> navigator.navigateBack()
            BookshelfInfoSheetAction.Edit -> sendEvent(BookshelfInfoSheetStateEvent.Edit(uiState.bookshelf.id))
            BookshelfInfoSheetAction.Remove -> sendEvent(BookshelfInfoSheetStateEvent.Remove(uiState.bookshelf))
            BookshelfInfoSheetAction.Scan -> requestScan()
            BookshelfInfoSheetAction.ThumbnailRegeneration -> TODO()
        }
    }

    override fun onNotificationPermissionResult(result: Boolean) {
        logcat { "通知権限リクエスト結果 isGranted=$result" }
        if (result) {
            requestScan()
        } else {
            scan()
        }
    }

    override fun onRemoveResult(result: NavResult<Boolean>) {
        logcat { "onRemoveResult(result: $result)" }
        when (result) {
            NavResult.Canceled -> Unit
            is NavResult.Value -> {
                if (result.value) {
                    navigator.navigateBack()
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            context.getString(
                                R.string.bookshelf_msg_delete,
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
        } else {
            // 通知権限が許可されていない
            scan()
        }
    }

    override fun onNotificationResult(result: NavResult<NotificationRequestResult>) {
        logcat { "onNotificationResult(result: $result)" }
        when (result) {
            NavResult.Canceled -> Unit
            is NavResult.Value -> {
                when (result.value) {
                    NotificationRequestResult.Ok ->
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            launcher.launch(POST_NOTIFICATIONS)
                        }

                    NotificationRequestResult.Cancel -> Unit
                    NotificationRequestResult.NotAllowed -> requestScan()
                }
            }
        }
    }

    private fun requestScan() {
        when {
            ContextCompat.checkSelfPermission(context as ComponentActivity, POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED -> {
                logcat("DEBUG") { "通知権限あり" }
                scan()
            }

            ActivityCompat.shouldShowRequestPermissionRationale(context, POST_NOTIFICATIONS) -> {
                logcat("DEBUG") { "ユーザに権限を理由を説明する" }
                sendEvent(BookshelfInfoSheetStateEvent.ShowRequestPermissionRationale)
            }

            else -> {
                logcat("DEBUG") { "ユーザに権限を理由を説明しない" }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    launcher.launch(POST_NOTIFICATIONS)
                }
            }
        }
    }

    init {
        WorkManager.getInstance(context).getWorkInfosForUniqueWorkFlow("scan").onEach { workInfos ->
            val workInfo =
                workInfos.firstOrNull { FileScanRequest.fromWorkData(it.progress)?.bookshelfId == uiState.bookshelf.id }
            val iProgressScan =
                workInfo?.state == WorkInfo.State.ENQUEUED || workInfo?.state == WorkInfo.State.RUNNING
            uiState = uiState.copy(isProgressScan = iProgressScan)
        }.launchIn(scope)
    }

    private fun scan() {
        logcat { "scan()" }
        scope.launch {
            if (ContextCompat.checkSelfPermission(context as ComponentActivity, POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED
            ) {
                snackbarHostState.showSnackbar("本棚のスキャンを開始します。")
            } else {
                val result = snackbarHostState.showSnackbar(
                    "本棚のスキャンを開始します。\n通知を許可すると進捗が確認できます。",
                    actionLabel = "設定",
                    duration = SnackbarDuration.Long
                )
                when (result) {
                    SnackbarResult.Dismissed -> Unit
                    SnackbarResult.ActionPerformed -> {
                        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                        }
                        settingsLauncher.launch(intent)
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
