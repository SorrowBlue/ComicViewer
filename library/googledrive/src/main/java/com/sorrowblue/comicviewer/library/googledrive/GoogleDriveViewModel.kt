package com.sorrowblue.comicviewer.library.googledrive

import android.content.Context
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.work.Constraints
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.sorrowblue.comicviewer.domain.entity.file.Book
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.framework.notification.ChannelID
import com.sorrowblue.comicviewer.library.googledrive.navigation.GoogleDriveArgs
import com.sorrowblue.comicviewer.library.googledrive.section.GoogleAccountDialogUiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

internal class GoogleDriveViewModel(
    context: Context,
    private val repository: GoogleDriveApiRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel(), DefaultLifecycleObserver {

    companion object {

        fun Factory(context: Context) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val savedStateHandle = extras.createSavedStateHandle()
                return GoogleDriveViewModel(
                    context,
                    GoogleDriveApiRepository(context),
                    savedStateHandle
                ) as T
            }
        }
    }

    lateinit var file: Book
    private val _uiState = MutableStateFlow(GoogleDriveScreenUiState())
    val uiState = _uiState.asStateFlow()

    private val args = GoogleDriveArgs(savedStateHandle)

    private val path = args.path ?: "root"

    init {
        repository.googleSignInAccount.onEach {
            _uiState.value = _uiState.value.copy(
                isAuthenticated = it != null,
                profileUri = it?.photoUrl?.toString().orEmpty()
            )
        }.launchIn(viewModelScope)

        val notificationManager = NotificationManagerCompat.from(context)
        val name = "ダウンロード状況"
        val descriptionText = "ダウンロード状況を表示します"
        val channel = NotificationChannelCompat.Builder(
            ChannelID.DOWNLOAD.id,
            NotificationManagerCompat.IMPORTANCE_LOW
        ).setName(name).setDescription(descriptionText)
            .build()
        notificationManager.createNotificationChannel(channel)
    }

    private val workManager = WorkManager.getInstance(context)
    fun enqueueDownload(outputUri: String, file: File) {
        val request = OneTimeWorkRequestBuilder<DriveDownloadWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setInputData(workDataOf("outputUri" to outputUri, "fileId" to file.path))
            .setConstraints(Constraints.Builder().setRequiresStorageNotLow(true).build())
            .build()
        workManager.enqueue(request)
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        refreshAccount()
    }

    fun refreshAccount() {
        repository.updateAccount()
    }

    fun onProfileImageClick() {
        val account = repository.googleSignInAccount.value ?: return
        _uiState.value =
            _uiState.value.copy(
                googleAccountDialogUiState = GoogleAccountDialogUiState.Show(
                    photoUrl = account.photoUrl?.toString().orEmpty(),
                    name = account.displayName.orEmpty()
                )
            )
    }

    fun onDialogDismissRequest() {
        _uiState.value =
            _uiState.value.copy(googleAccountDialogUiState = GoogleAccountDialogUiState.Hide)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val pagingDataFlow = repository.driverServiceFlow.flatMapLatest {
        Pager(PagingConfig(20)) { GoogleDrivePagingSource(it, path) }.flow
    }.cachedIn(viewModelScope)
}
