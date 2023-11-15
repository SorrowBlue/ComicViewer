package com.sorrowblue.comicviewer.feature.library.onedrive

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.work.Constraints
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.library.onedrive.data.OneDriveApiRepository
import com.sorrowblue.comicviewer.feature.library.onedrive.navigation.OneDriveArgs
import com.sorrowblue.comicviewer.feature.library.onedrive.section.OneDriveDialogUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
internal fun rememberOneDriveScreenState(
    args: OneDriveArgs,
    context: Context = LocalContext.current,
    scope: CoroutineScope = rememberCoroutineScope(),
    repository: OneDriveApiRepository = koinInject(),
) = rememberSaveable(
    saver = mapSaver(
        save = {
            mapOf("uiState" to it.uiState, "book" to it.book)
        },
        restore = {
            OneDriveScreenState(
                args = args,
                context = context,
                scope = scope,
                repository = repository,
                uiState = it["uiState"] as OneDriveScreenUiState,
                book = it["book"] as? Book
            )
        }
    )
) {
    OneDriveScreenState(
        args = args,
        context = context,
        scope = scope,
        repository = repository,
    )
}

@Stable
internal class OneDriveScreenState(
    args: OneDriveArgs,
    uiState: OneDriveScreenUiState = OneDriveScreenUiState.Loading,
    context: Context,
    private val scope: CoroutineScope,
    private val repository: OneDriveApiRepository,
    internal var book: Book? = null,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    val pagingDataFlow = repository.accountFlow.filterNotNull().flatMapLatest {
        Pager(PagingConfig(20)) {
            OneDrivePagingSource(args.driveId, args.itemId.orEmpty(), repository)
        }.flow
    }.cachedIn(scope)

    var uiState by mutableStateOf(uiState)
        private set

    init {
        scope.launch {
            repository.initialize()
            repository.accountFlow.collectLatest { account ->
                this@OneDriveScreenState.uiState = if (account != null) {
                    OneDriveScreenUiState.Loaded(
                        path = args.itemId.orEmpty(),
                        profileUri = {
                            repository.profileImage()
                        }
                    )
                } else {
                    OneDriveScreenUiState.Login()
                }
            }
        }
    }

    fun onResume() {
        repository.loadAccount()
    }

    fun onLoginClick(activity: Activity) {
        scope.launch {
            repository.login(activity)
        }
    }

    fun onLogoutClick() {
        scope.launch {
            repository.logout()
            onDialogDismissRequest()
        }
    }

    fun onDialogDismissRequest() {
        if (uiState is OneDriveScreenUiState.Loaded) {
            uiState = (uiState as OneDriveScreenUiState.Loaded).copy(showDialog = false)
        }
    }

    fun onFileClick(
        file: File,
        createFileRequest: ManagedActivityResultLauncher<Intent, ActivityResult>,
        onFolderClick: (Folder) -> Unit,
    ) {
        when (file) {
            is Book -> {
                val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.putExtra(Intent.EXTRA_TITLE, file.name)
                intent.type = "*/*"
                this.book = file
                createFileRequest.launch(intent)
            }

            is Folder -> {
                onFolderClick(file)
            }
        }
    }

    fun onProfileImageClick() {
        if (uiState is OneDriveScreenUiState.Loaded) {
            scope.launch {
                uiState = (uiState as OneDriveScreenUiState.Loaded).copy(
                    showDialog = true,
                    oneDriveDialogUiState = OneDriveDialogUiState(
                        "",
                        repository.getCurrentUser()?.displayName.orEmpty()
                    )
                )
            }
        }
    }

    fun onResult(activityResult: ActivityResult) {
        if (activityResult.resultCode == Activity.RESULT_OK && activityResult.data?.data != null) {
            enqueueDownload(activityResult.data!!.data!!.toString(), book!!)
        }
    }

    private val workManager = WorkManager.getInstance(context)
    private fun enqueueDownload(outOut: String, file: Book) {
        val request = OneTimeWorkRequestBuilder<OneDriveDownloadWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setInputData(
                workDataOf(
                    "outputUri" to outOut,
                    "driveId" to file.params["driveId"],
                    "itemId" to file.path
                )
            )
            .setConstraints(
                Constraints.Builder()
                    .setRequiresStorageNotLow(true)
                    .build()
            )
            .build()
        workManager.enqueue(request)
    }
}
