package com.sorrowblue.comicviewer.feature.library.googledrive

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.sorrowblue.comicviewer.feature.library.googledrive.data.DriveDownloadWorker
import com.sorrowblue.comicviewer.feature.library.googledrive.data.GoogleDriveApiRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.core.component.KoinComponent

internal interface GoogleDriveScreenState {
    val uiState: GoogleDriveScreenUiState
    val pagingDataFlow: Flow<PagingData<File>>
    fun onFileClick(
        file: File,
        onFileClick: (Folder) -> Unit,
    )
}

@Composable
internal fun rememberGoogleDriveScreenState(
    args: GoogleDriveArgs,
    savedStateHandle: SavedStateHandle,
    context: Context = LocalContext.current,
    scope: CoroutineScope = rememberCoroutineScope(),
    repository: GoogleDriveApiRepository = koinInject(),
): GoogleDriveScreenState {
    val stateImpl = remember {
        GoogleDriveScreenStateImpl(
            args = args,
            context = context,
            savedStateHandle = savedStateHandle,
            scope = scope,
            repository = repository,
        )
    }
    stateImpl.resultLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        stateImpl::onResult
    )
    return stateImpl
}

@OptIn(SavedStateHandleSaveableApi::class)
private class GoogleDriveScreenStateImpl(
    args: GoogleDriveArgs,
    context: Context,
    savedStateHandle: SavedStateHandle,
    scope: CoroutineScope,
    private val repository: GoogleDriveApiRepository,
) : GoogleDriveScreenState, KoinComponent {

    private val workManager = WorkManager.getInstance(context)
    private var book by savedStateHandle.saveable { mutableStateOf<Book?>(null) }
    lateinit var resultLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>

    override var uiState by savedStateHandle.saveable { mutableStateOf(GoogleDriveScreenUiState()) }
        private set

    override val pagingDataFlow =
        Pager(PagingConfig(20)) {
            GoogleDrivePagingSource(args.path, repository)
        }.flow.cachedIn(scope)

    init {
        repository.profile.filterNotNull().onEach {
            uiState = uiState.copy(profileUri = it.photos.firstOrNull()?.url.orEmpty())
        }.launchIn(scope)
        scope.launch {
            repository.fetchProfile()
        }
    }

    override fun onFileClick(
        file: File,
        onFileClick: (Folder) -> Unit,
    ) {
        when (file) {
            is Book -> {
                val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    putExtra(Intent.EXTRA_TITLE, file.name)
                    type = "*/*"
                }
                book = file
                resultLauncher.launch(intent)
            }

            is Folder -> onFileClick(file)
        }
    }

    fun onResult(activityResult: ActivityResult) {
        if (activityResult.resultCode == Activity.RESULT_OK && activityResult.data?.data != null) {
            enqueueDownload(activityResult.data!!.data!!.toString(), book!!)
        }
    }

    private fun enqueueDownload(outputUri: String, file: File) {
        val request = OneTimeWorkRequestBuilder<DriveDownloadWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setInputData(workDataOf("outputUri" to outputUri, "fileId" to file.path))
            .setConstraints(Constraints.Builder().setRequiresStorageNotLow(true).build())
            .build()
        workManager.enqueue(request)
    }
}
