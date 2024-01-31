package com.sorrowblue.comicviewer.feature.library.dropbox

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
import com.sorrowblue.comicviewer.feature.library.dropbox.data.DropBoxApiRepository
import com.sorrowblue.comicviewer.feature.library.dropbox.section.DropBoxDialogUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.core.component.KoinComponent

internal sealed interface DropBoxScreenEvent {
    data object RequireAuthentication : DropBoxScreenEvent
}

@Stable
internal interface DropBoxScreenState {
    val uiState: DropBoxScreenUiState
    val events: SnapshotStateList<DropBoxScreenEvent>
    val pagingDataFlow: Flow<PagingData<File>>
    fun consumeEvent(event: DropBoxScreenEvent)
    fun onProfileImageClick()
    fun onDialogDismissRequest()
    fun onLogoutClick()
    fun onResume()
    fun onResult(result: ActivityResult)
    fun onFileClick(
        file: File,
        createFileRequest: ManagedActivityResultLauncher<Intent, ActivityResult>,
        onFolderClick: (Folder) -> Unit,
    )
}

@Composable
internal fun rememberDropBoxScreenState(
    args: DropBoxArgs,
    savedStateHandle: SavedStateHandle,
    context: Context = LocalContext.current,
    scope: CoroutineScope = rememberCoroutineScope(),
    repository: DropBoxApiRepository = koinInject(),
): DropBoxScreenState {
    return remember {
        DropBoxScreenStateImpl(
            args = args,
            savedStateHandle = savedStateHandle,
            context = context,
            scope = scope,
            repository = repository
        )
    }
}

@OptIn(SavedStateHandleSaveableApi::class)
private class DropBoxScreenStateImpl(
    args: DropBoxArgs,
    savedStateHandle: SavedStateHandle,
    context: Context,
    private val scope: CoroutineScope,
    private val repository: DropBoxApiRepository,
) : DropBoxScreenState, KoinComponent {

    init {
        repository.accountFlow.onEach {
            if (it == null) {
                events += DropBoxScreenEvent.RequireAuthentication
            } else {
                uiState = uiState.copy(
                    path = args.path,
                    profileUri = it.profilePhotoUrl.orEmpty(),
                )
            }
        }.launchIn(scope)
    }

    private var book: Book? by savedStateHandle.saveable(
        key = "book",
        stateSaver = autoSaver()
    ) { mutableStateOf(null) }

    override var uiState by savedStateHandle.saveable { mutableStateOf(DropBoxScreenUiState()) }
        private set

    override var events = mutableStateListOf<DropBoxScreenEvent>()
        private set

    override fun consumeEvent(event: DropBoxScreenEvent) {
        events.remove(event)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val pagingDataFlow = repository.accountFlow.flatMapLatest {
        Pager(PagingConfig(20)) {
            DropBoxPagingSource(args.path, repository)
        }.flow
    }.cachedIn(scope)

    override fun onProfileImageClick() {
        scope.launch {
            val account = repository.accountFlow.first()
            uiState = uiState.copy(
                dropBoxDialogUiState = DropBoxDialogUiState.Show(
                    photoUrl = account?.profilePhotoUrl.orEmpty(),
                    name = account?.name?.displayName.orEmpty()
                )
            )
        }
    }

    override fun onDialogDismissRequest() {
        uiState = uiState.copy(dropBoxDialogUiState = DropBoxDialogUiState.Hide)
    }

    override fun onLogoutClick() {
        scope.launch {
            repository.signOut()
        }
    }

    override fun onResume() {
        scope.launch {
            repository.refresh()
        }
    }

    override fun onResult(result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK && result.data?.data != null) {
            enqueueDownload(result.data!!.data!!.toString(), book!!)
        }
    }

    private val workManager = WorkManager.getInstance(context)

    private fun enqueueDownload(outputUri: String, file: File) {
        val request = OneTimeWorkRequestBuilder<DropBoxDownloadWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setInputData(workDataOf("outputUri" to outputUri, "path" to file.path))
            .setConstraints(Constraints.Builder().setRequiresStorageNotLow(true).build())
            .build()
        workManager.enqueue(request)
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
}
