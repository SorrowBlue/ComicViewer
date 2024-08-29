package com.sorrowblue.comicviewer.bookshelf.info

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.sorrowblue.comicviewer.bookshelf.BookshelfViewModel
import com.sorrowblue.comicviewer.bookshelf.FileScanRequest
import com.sorrowblue.comicviewer.bookshelf.FileScanWorker
import com.sorrowblue.comicviewer.bookshelf.section.BookshelfRemoveDialogArgs
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.onSuccess
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.RemoveBookshelfUseCase
import com.sorrowblue.comicviewer.feature.bookshelf.R
import com.sorrowblue.comicviewer.feature.bookshelf.destinations.BookshelfRemoveDialogDestination
import com.sorrowblue.comicviewer.feature.bookshelf.destinations.NotificationRequestDialogDestination
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import logcat.logcat

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
internal interface BookshelfInfoSheetState {
    val navigator: ThreePaneScaffoldNavigator<BookshelfFolder>

    fun onRemoveClick()
    fun onCloseClick()
    fun onPermissionResult(isGranted: Boolean)
    fun onScanClick(permissionResultLauncher: ManagedActivityResultLauncher<String, Boolean>)
    fun onNavResult(navResult: NavResult<Boolean>)
    fun onNavResult(
        permissionResultLauncher: ManagedActivityResultLauncher<String, Boolean>,
        navResult: NavResult<NotificationRequestResult>,
    )

    fun onReThumbnailsClick(permissionResultLauncher: ManagedActivityResultLauncher<String, Boolean>)
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
internal fun rememberBookshelfInfoSheetState(
    destinationsNavigator: DestinationsNavigator,
    navigator: ThreePaneScaffoldNavigator<BookshelfFolder>,
    snackbarHostState: SnackbarHostState,
    context: Context = LocalContext.current,
    scope: CoroutineScope = rememberCoroutineScope(),
    viewModel: BookshelfViewModel = hiltViewModel(),
): BookshelfInfoSheetState = remember {
    BookshelfInfoSheetStateImpl(
        context = context,
        scope = scope,
        navigator = navigator,
        snackbarHostState = snackbarHostState,
        destinationsNavigator = destinationsNavigator,
        removeBookshelfUseCase = viewModel.removeBookshelfUseCase
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
private class BookshelfInfoSheetStateImpl(
    override val navigator: ThreePaneScaffoldNavigator<BookshelfFolder>,
    private val destinationsNavigator: DestinationsNavigator,
    private val context: Context,
    private val scope: CoroutineScope,
    private val snackbarHostState: SnackbarHostState,
    private val removeBookshelfUseCase: RemoveBookshelfUseCase,
) : BookshelfInfoSheetState {

    override fun onRemoveClick() {
        val bookshelfFolder = navigator.currentDestination?.content
        destinationsNavigator.navigate(
            BookshelfRemoveDialogDestination(
                BookshelfRemoveDialogArgs(
                    bookshelfFolder!!.bookshelf.displayName
                )
            )
        )
    }

    private var withThumbnails = false

    override fun onScanClick(permissionResultLauncher: ManagedActivityResultLauncher<String, Boolean>) {
        withThumbnails = false
        when {
            ContextCompat.checkSelfPermission(context as ComponentActivity, POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED -> {
                logcat("DEBUG") { "通知権限あり" }
                requestScan()
            }

            ActivityCompat.shouldShowRequestPermissionRationale(context, POST_NOTIFICATIONS) -> {
                logcat("DEBUG") { "ユーザに権限を理由を説明する" }
                destinationsNavigator.navigate(NotificationRequestDialogDestination)
            }

            else -> {
                logcat("DEBUG") { "ユーザに権限を理由を説明しない" }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    permissionResultLauncher.launch(POST_NOTIFICATIONS)
                }
            }
        }
    }

    override fun onReThumbnailsClick(permissionResultLauncher: ManagedActivityResultLauncher<String, Boolean>) {
        withThumbnails = false
        when {
            ContextCompat.checkSelfPermission(context as ComponentActivity, POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED -> {
                logcat("DEBUG") { "通知権限あり" }
                requestScan()
            }

            ActivityCompat.shouldShowRequestPermissionRationale(context, POST_NOTIFICATIONS) -> {
                logcat("DEBUG") { "ユーザに権限を理由を説明する" }
                destinationsNavigator.navigate(NotificationRequestDialogDestination)
            }

            else -> {
                logcat("DEBUG") { "ユーザに権限を理由を説明しない" }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    permissionResultLauncher.launch(POST_NOTIFICATIONS)
                }
            }
        }
    }

    override fun onCloseClick() {
        navigator.navigateBack()
    }

    override fun onNavResult(navResult: NavResult<Boolean>) {
        when (navResult) {
            NavResult.Canceled -> Unit
            is NavResult.Value -> {
                if (navResult.value) {
                    val bookshelf = navigator.currentDestination?.content!!.bookshelf
                    scope.launch {
                        navigator.navigateBack()
                        removeBookshelfUseCase(RemoveBookshelfUseCase.Request(bookshelf.id)).onSuccess {
                            snackbarHostState.showSnackbar(
                                context.getString(
                                    R.string.bookshelf_msg_delete,
                                    bookshelf.displayName
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onNavResult(
        permissionResultLauncher: ManagedActivityResultLauncher<String, Boolean>,
        navResult: NavResult<NotificationRequestResult>,
    ) {
        when (navResult) {
            NavResult.Canceled -> Unit
            is NavResult.Value -> {
                when (navResult.value) {
                    NotificationRequestResult.Ok -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            permissionResultLauncher.launch(POST_NOTIFICATIONS)
                        }
                    }

                    NotificationRequestResult.Cancel -> Unit
                    NotificationRequestResult.NotAllowed -> {
                        requestScan()
                    }
                }
            }
        }
    }

    override fun onPermissionResult(isGranted: Boolean) {
        logcat("DEBUG") { "通知権限リクエスト結果 isGranted=$isGranted" }
        if (isGranted) {
            requestScan()
        } else {
            requestScan()
        }
    }

    private fun requestScan() {
        val bookshelfFolder = navigator.currentDestination?.content
        scope.launch {
            snackbarHostState.showSnackbar("本棚のスキャンを開始します。")
            val constraints = Constraints.Builder().apply {
                // 有効なネットワーク接続が必要
                setRequiredNetworkType(NetworkType.CONNECTED)
                // ユーザーのデバイスの保存容量が少なすぎる場合以外
                setRequiresStorageNotLow(true)
            }.build()
            val myWorkRequest = OneTimeWorkRequest.Builder(FileScanWorker::class.java)
                .setConstraints(constraints)
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .addTag("observable")
                .setInputData(
                    FileScanRequest(
                        bookshelfFolder!!.bookshelf.id,
                        withThumbnails
                    ).toWorkData()
                )
                .build()
            WorkManager.getInstance(context)
                .enqueue(myWorkRequest)
        }
    }
}
