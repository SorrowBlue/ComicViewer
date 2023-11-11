package com.sorrowblue.comicviewer.feature.library.dropbox

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
import androidx.compose.runtime.saveable.Saver
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
import com.sorrowblue.comicviewer.feature.library.dropbox.data.DropBoxApiRepository
import com.sorrowblue.comicviewer.feature.library.dropbox.navigation.DropBoxArgs
import com.sorrowblue.comicviewer.feature.library.dropbox.section.DropBoxDialogUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import logcat.logcat
import org.koin.compose.koinInject
import org.koin.core.component.KoinComponent

@Stable
internal class DropBoxScreenState(
    args: DropBoxArgs,
    context: Context,
    private val scope: CoroutineScope,
    private val repository: DropBoxApiRepository,
    restoreUiState: DropBoxScreenUiState = DropBoxScreenUiState.Loading,
) : KoinComponent {
    init {
        repository.accountFlow.onEach {
            uiState = if (it == null) {
                DropBoxScreenUiState.Login(false)
            } else {
                DropBoxScreenUiState.Loaded(
                    path = args.path,
                    profileUri = it.profilePhotoUrl.orEmpty(),
                )
            }
        }.launchIn(scope)
    }

    var uiState: DropBoxScreenUiState by mutableStateOf(restoreUiState)
        private set

    @OptIn(ExperimentalCoroutinesApi::class)
    val pagingDataFlow = repository.accountFlow.flatMapLatest {
        Pager(PagingConfig(20)) {
            DropBoxPagingSource(args.path, repository)
        }.flow
    }.cachedIn(scope)

    fun onSignInClick() {
        uiState = DropBoxScreenUiState.Login(true)
        repository.startSignIn()
    }

    fun onProfileImageClick() {
        if (uiState is DropBoxScreenUiState.Loaded) {
            scope.launch {
                val account = repository.accountFlow.first()
                uiState = (uiState as DropBoxScreenUiState.Loaded).copy(
                    dropBoxDialogUiState = DropBoxDialogUiState.Show(
                        photoUrl = account?.profilePhotoUrl.orEmpty(),
                        name = account?.name?.displayName.orEmpty()
                    )
                )
            }
        }
    }

    fun onDialogDismissRequest() {
        uiState = DropBoxScreenUiState.Loaded(dropBoxDialogUiState = DropBoxDialogUiState.Hide)
    }

    fun onLogoutClick() {
        scope.launch {
            repository.signOut()
        }
    }

    fun onResume() {
        logcat { "dropbox onResumes" }
        if (uiState is DropBoxScreenUiState.Login) {
            scope.launch {
                if (!repository.dbxCredential()) {
                    uiState = DropBoxScreenUiState.Login(false)
                }
            }
        }
    }

    fun onResult(result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK && result.data?.data != null) {
            enqueueDownload(result.data!!.data!!.toString(), file!!)
        }
    }

    private var file: File? = null
    private val workManager = WorkManager.getInstance(context)

    private fun enqueueDownload(outputUri: String, file: File) {
        val request = OneTimeWorkRequestBuilder<DropBoxDownloadWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setInputData(workDataOf("outputUri" to outputUri, "path" to file.path))
            .setConstraints(Constraints.Builder().setRequiresStorageNotLow(true).build())
            .build()
        workManager.enqueue(request)
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
                this.file = file
                createFileRequest.launch(intent)
            }

            is Folder -> {
                onFolderClick(file)
            }
        }
    }
}

@Composable
internal fun rememberDropBoxScreenState(
    args: DropBoxArgs,
    context: Context = LocalContext.current,
    scope: CoroutineScope = rememberCoroutineScope(),
    repository: DropBoxApiRepository = koinInject(),
) = rememberSaveable(
    saver = Saver(
        save = { it.uiState },
        restore = {
            DropBoxScreenState(
                restoreUiState = it,
                args = args,
                scope = scope,
                context = context,
                repository = repository
            )
        }
    )
) {
    DropBoxScreenState(
        args = args,
        scope = scope,
        context = context,
        repository = repository
    )
}
