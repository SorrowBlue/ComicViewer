package com.sorrowblue.comicviewer.feature.library.box

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
import com.sorrowblue.comicviewer.feature.library.box.data.BoxApiRepository
import com.sorrowblue.comicviewer.feature.library.box.data.BoxDownloadWorker
import com.sorrowblue.comicviewer.feature.library.box.section.BoxDialogUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Stable
internal interface BoxScreenState {
    val uiState: BoxScreenUiState
    val pagingDataFlow: Flow<PagingData<File>>
    fun onProfileImageClick()
    fun onDialogDismissRequest()
    fun onLogoutClick()
    fun onResult(activityResult: ActivityResult)
    fun onClickFile(
        file: File,
        createFileRequest: ManagedActivityResultLauncher<Intent, ActivityResult>,
        onFolderClick: (Folder) -> Unit,
    )

    fun consumeEvent(event: BoxScreenUiEvent)

    val event: SnapshotStateList<BoxScreenUiEvent>
}

@Composable
internal fun rememberBoxScreenState(
    args: BoxArgs,
    savedStateHandle: SavedStateHandle,
    context: Context = LocalContext.current,
    scope: CoroutineScope = rememberCoroutineScope(),
    repository: BoxApiRepository = koinInject(),
): BoxScreenState = remember {
    BoxScreenStateImpl(
        args = args,
        savedStateHandle = savedStateHandle,
        context = context,
        scope = scope,
        repository = repository
    )
}

internal sealed interface BoxScreenUiEvent {

    data object RequiredAuth : BoxScreenUiEvent
}

@OptIn(SavedStateHandleSaveableApi::class, ExperimentalCoroutinesApi::class)
private class BoxScreenStateImpl(
    args: BoxArgs,
    savedStateHandle: SavedStateHandle,
    context: Context,
    private val scope: CoroutineScope,
    private val repository: BoxApiRepository,
) : BoxScreenState {

    override var uiState by savedStateHandle.saveable { mutableStateOf(BoxScreenUiState()) }
        private set

    private var book: Book? by savedStateHandle.saveable("", stateSaver = autoSaver()) {
        mutableStateOf(null)
    }

    override var event = mutableStateListOf<BoxScreenUiEvent>()
        private set

    override fun consumeEvent(event: BoxScreenUiEvent) {
        this.event.remove(event)
    }

    init {
        repository.isAuthenticated().onEach { authenticated ->
            if (!authenticated) {
                event += BoxScreenUiEvent.RequiredAuth
            }
        }.launchIn(scope)

        repository.userInfoFlow.filterNotNull().onEach {
            uiState = uiState.copy(
                profileUri = "https://api.box.com/2.0/users/${it.id}/avatar",
                token = repository.accessToken()
            )
        }.launchIn(scope)
    }

    override val pagingDataFlow = repository.userInfoFlow.filterNotNull().flatMapLatest {
        Pager(PagingConfig(10)) { BoxPagingSource(args.path, repository) }.flow
            .cachedIn(scope)
    }

    override fun onProfileImageClick() {
        scope.launch {
            repository.userInfoFlow.first()?.let {
                uiState = uiState.copy(
                    showDialog = true,
                    boxDialogUiState = BoxDialogUiState(it.avatarURL, it.name)
                )
            }
        }
    }

    override fun onDialogDismissRequest() {
        uiState = uiState.copy(showDialog = false)
    }

    override fun onLogoutClick() {
        scope.launch {
            repository.signOut()
        }
    }

    private val workManager = WorkManager.getInstance(context)

    fun enqueueDownload(outputUri: String, file: Book) {
        val request = OneTimeWorkRequestBuilder<BoxDownloadWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setInputData(workDataOf("outputUri" to outputUri, "path" to file.path))
            .setConstraints(Constraints.Builder().setRequiresStorageNotLow(true).build())
            .build()
        workManager.enqueue(request)
    }

    override fun onClickFile(
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

    override fun onResult(activityResult: ActivityResult) {
        if (activityResult.resultCode == Activity.RESULT_OK && activityResult.data?.data != null) {
            enqueueDownload(activityResult.data!!.data!!.toString(), book!!)
        }
    }
}
