package com.sorrowblue.comicviewer.feature.library.onedrive

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.autoSaver
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
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
import com.sorrowblue.comicviewer.feature.library.onedrive.data.OneDriveDownloadWorker
import com.sorrowblue.comicviewer.feature.library.onedrive.navigation.OneDriveArgs
import com.sorrowblue.comicviewer.feature.library.onedrive.section.OneDriveDialogUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import logcat.logcat
import org.koin.compose.koinInject

sealed interface OneDriveScreenEvent {

    data object RequireAuthentication : OneDriveScreenEvent
}

@Stable
internal interface OneDriveScreenState {

    val pagingDataFlow: Flow<PagingData<File>>
    val uiState: OneDriveScreenUiState
    val events: SnapshotStateList<OneDriveScreenEvent>

    fun onLogoutClick()
    fun onResume()
    fun onDialogDismissRequest()
    fun onFileClick(
        file: File,
        createFileRequest: ManagedActivityResultLauncher<Intent, ActivityResult>,
        onFolderClick: (Folder) -> Unit,
    )

    fun onProfileImageClick()
    fun onResult(activityResult: ActivityResult)
    fun consumeEvent(event: OneDriveScreenEvent)
}

@Composable
internal fun rememberOneDriveScreenState(
    args: OneDriveArgs,
    savedStateHandle: SavedStateHandle,
    context: Context = LocalContext.current,
    scope: CoroutineScope = rememberCoroutineScope(),
    repository: OneDriveApiRepository = koinInject(),
): OneDriveScreenState {
    return remember {
        OneDriveScreenStateImpl(
            args = args,
            savedStateHandle = savedStateHandle,
            context = context,
            scope = scope,
            repository = repository,
        )
    }
}

@OptIn(SavedStateHandleSaveableApi::class, ExperimentalCoroutinesApi::class)
internal class OneDriveScreenStateImpl(
    args: OneDriveArgs,
    savedStateHandle: SavedStateHandle,
    context: Context,
    private val scope: CoroutineScope,
    private val repository: OneDriveApiRepository,
) : OneDriveScreenState {

    private var book: Book? by savedStateHandle.saveable(
        key = "book",
        stateSaver = autoSaver()
    ) { mutableStateOf(null) }

    override val pagingDataFlow = repository.accountFlow.filterNotNull().flatMapLatest {
        Pager(PagingConfig(20)) {
            OneDrivePagingSource(args.driveId, args.itemId.orEmpty(), repository)
        }.flow
    }.cachedIn(scope)

    override var uiState by savedStateHandle.saveable { mutableStateOf(OneDriveScreenUiState()) }
        private set

    override var events = mutableStateListOf<OneDriveScreenEvent>()
        private set

    init {
        scope.launch {
            repository.initialize()
            repository.accountFlow.collectLatest { account ->
                if (account == null) {
                    delay(1000)
                    events += OneDriveScreenEvent.RequireAuthentication
                } else {
                    uiState = uiState.copy(
                        path = args.itemId.orEmpty(),
                        profileUri = {
                            repository.profileImage()
                        }
                    )
                }
            }
        }
    }

    override fun consumeEvent(event: OneDriveScreenEvent) {
        events.remove(event)
    }

    override fun onResume() {
        repository.loadAccount()
    }

    override fun onLogoutClick() {
        scope.launch {
            repository.logout()
            onDialogDismissRequest()
        }
    }

    override fun onDialogDismissRequest() {
        uiState = uiState.copy(showDialog = false)
    }

    override fun onFileClick(
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

    override fun onProfileImageClick() {
        scope.launch {
            uiState = uiState.copy(
                showDialog = true,
                oneDriveDialogUiState = OneDriveDialogUiState(
                    "",
                    repository.getCurrentUser()?.displayName.orEmpty()
                )
            )
        }
    }

    override fun onResult(activityResult: ActivityResult) {
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
