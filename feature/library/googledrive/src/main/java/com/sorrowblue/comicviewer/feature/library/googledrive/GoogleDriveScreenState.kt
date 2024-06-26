package com.sorrowblue.comicviewer.feature.library.googledrive

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
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
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
import com.sorrowblue.comicviewer.feature.library.googledrive.data.AuthStatus
import com.sorrowblue.comicviewer.feature.library.googledrive.data.DriveDownloadWorker
import com.sorrowblue.comicviewer.feature.library.googledrive.data.GoogleAuthorizationRepository
import com.sorrowblue.comicviewer.feature.library.googledrive.data.GoogleDriveApiRepository
import com.sorrowblue.comicviewer.feature.library.googledrive.section.GoogleAccountDialogUiState
import com.sorrowblue.comicviewer.framework.notification.ChannelID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.core.component.KoinComponent

@Composable
internal fun rememberGoogleDriveScreenState(
    args: GoogleDriveArgs,
    savedStateHandle: SavedStateHandle,
    context: Context = LocalContext.current,
    scope: CoroutineScope = rememberCoroutineScope(),
    repository: GoogleDriveApiRepository = koinInject(),
    authRepository: GoogleAuthorizationRepository = koinInject(),
) = remember {
    GoogleDriveScreenState(
        args = args,
        savedStateHandle = savedStateHandle,
        context = context,
        scope = scope,
        repository = repository,
        authRepository = authRepository
    )
}

sealed interface GoogleDriveScreenEvent {

    data object RequireAuthentication : GoogleDriveScreenEvent
}

@OptIn(SavedStateHandleSaveableApi::class)
@Stable
internal class GoogleDriveScreenState(
    args: GoogleDriveArgs,
    savedStateHandle: SavedStateHandle,
    context: Context,
    private val scope: CoroutineScope,
    private val repository: GoogleDriveApiRepository,
    private val authRepository: GoogleAuthorizationRepository,
) : KoinComponent {

    private val path = args.path

    var uiState by savedStateHandle.saveable { mutableStateOf(GoogleDriveScreenUiState()) }
        private set

    private var book: Book? by savedStateHandle.saveable(
        key = "book",
        stateSaver = autoSaver()
    ) { mutableStateOf(null) }

    var events = mutableStateListOf<GoogleDriveScreenEvent>()
        private set

    fun consumeEvent(event: GoogleDriveScreenEvent) {
        events.remove(event)
    }

    val pagingDataFlow =
        Pager(PagingConfig(20)) { GoogleDrivePagingSource(path, repository) }.flow.cachedIn(scope)

    fun onResult(activityResult: ActivityResult) {
        if (activityResult.resultCode == Activity.RESULT_OK && activityResult.data?.data != null) {
            enqueueDownload(activityResult.data!!.data!!.toString(), book!!)
        }
    }

    init {
        authRepository.state.filter { it == AuthStatus.Uncertified }.onEach {
            events += GoogleDriveScreenEvent.RequireAuthentication
        }.launchIn(scope)

        val notificationManager = NotificationManagerCompat.from(context)
        val name = context.getString(R.string.googledrive_name_download_status)
        val descriptionText = context.getString(R.string.googledrive_desc_notify_download_status)
        val channel = NotificationChannelCompat.Builder(
            ChannelID.DOWNLOAD.id,
            NotificationManagerCompat.IMPORTANCE_LOW
        ).setName(name).setDescription(descriptionText)
            .build()
        notificationManager.createNotificationChannel(channel)
    }

    private val workManager = WorkManager.getInstance(context)
    private fun enqueueDownload(outputUri: String, file: File) {
        val request = OneTimeWorkRequestBuilder<DriveDownloadWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setInputData(workDataOf("outputUri" to outputUri, "fileId" to file.path))
            .setConstraints(Constraints.Builder().setRequiresStorageNotLow(true).build())
            .build()
        workManager.enqueue(request)
    }

    fun onProfileImageClick() {
        scope.launch {
            repository.profile()?.let {
                uiState = uiState.copy(
                    googleAccountDialogUiState = GoogleAccountDialogUiState.Show(
                        photoUrl = it.photos.firstOrNull()?.url.orEmpty(),
                        name = it.names.firstOrNull()?.displayName.orEmpty()
                    )
                )
            }
        }
    }

    fun onDialogDismissRequest() {
        uiState = uiState.copy(googleAccountDialogUiState = GoogleAccountDialogUiState.Hide)
    }

    fun onFileClick(
        file: File,
        createFileRequest: ManagedActivityResultLauncher<Intent, ActivityResult>,
        onFileClick: (Folder) -> Unit,
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

            is Folder -> onFileClick(file)
        }
    }

    fun onLogoutClick() {
        scope.launch {
            authRepository.signout()
            onDialogDismissRequest()
        }
    }
}
