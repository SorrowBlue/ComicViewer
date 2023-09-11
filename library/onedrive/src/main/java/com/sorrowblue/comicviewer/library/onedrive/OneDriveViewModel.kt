package com.sorrowblue.comicviewer.library.onedrive

import android.app.Activity
import android.content.Context
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
import com.sorrowblue.comicviewer.library.onedrive.data.AuthenticationProvider
import com.sorrowblue.comicviewer.library.onedrive.data.OneDriveApiRepository
import com.sorrowblue.comicviewer.library.onedrive.navigation.OneDriveArgs
import com.sorrowblue.comicviewer.library.onedrive.section.OneDriveDialogUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.future.await
import kotlinx.coroutines.launch
import logcat.logcat

internal class OneDriveViewModel(
    context: Context,
    private val provider: AuthenticationProvider,
    private val repository: OneDriveApiRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    lateinit var file: Book
    private val args = OneDriveArgs(savedStateHandle)

    private val _uiState = MutableStateFlow<OneDriveScreenUiState>(OneDriveScreenUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            provider.initialize()
            repository.currentUserFlow.onEach {
                if (it != null) {
                    _uiState.value = OneDriveScreenUiState.Loaded(
                        path = args.itemId.orEmpty(),
                        profileUri = repository.profileImage()
                    )
                } else {
                    _uiState.value = OneDriveScreenUiState.Login()
                }
            }.launchIn(viewModelScope)
        }
    }

    fun signOut() {
        viewModelScope.launch {
            provider.signOut()
        }
    }

    fun onDialogDismissRequest() {
        val uiState = _uiState.value
        if (uiState is OneDriveScreenUiState.Loaded) {
            viewModelScope.launch {
                _uiState.value = uiState.copy(oneDriveDialogUiState = OneDriveDialogUiState.Hide)
            }
        }
    }

    fun onProfileImageClick() {
        val uiState = _uiState.value
        if (uiState is OneDriveScreenUiState.Loaded) {
            viewModelScope.launch {
                _uiState.value = uiState.copy(
                    oneDriveDialogUiState = OneDriveDialogUiState.Show(
                        "",
                        repository.getCurrentUser()?.displayName.orEmpty()
                    )
                )
            }
        }
    }

    val pagingDataFlow = repository.currentUserFlow.filterNotNull().flatMapLatest {
        Pager(PagingConfig(20)) {
            OneDrivePagingSource(args.driveId, args.itemId.orEmpty(), repository)
        }.flow
    }.cachedIn(viewModelScope)

    fun signIn(activity: Activity) {
        viewModelScope.launch {
            kotlin.runCatching {
                provider.signIn(activity).await()
            }.onSuccess {
                logcat { "success signin. id=${it.account.id}" }
                if (it.account.idToken != null) {

                }
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    private val workManager = WorkManager.getInstance(context)
    fun enqueueDownload(outOut: String, file: Book) {
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

    companion object {

        fun Factory(context: Context) =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(
                    modelClass: Class<T>,
                    extras: CreationExtras
                ): T {
                    val savedStateHandle = extras.createSavedStateHandle()
                    return OneDriveViewModel(
                        context,
                        AuthenticationProvider.getInstance(context),
                        OneDriveApiRepository.getInstance(context),
                        savedStateHandle
                    ) as T
                }
            }
    }
}
