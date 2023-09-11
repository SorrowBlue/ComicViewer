package com.sorrowblue.comicviewer.library.dropbox

import android.content.Context
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
import com.dropbox.core.android.Auth
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.library.dropbox.data.DropBoxApiRepository
import com.sorrowblue.comicviewer.library.dropbox.navigation.DropBoxArgs
import com.sorrowblue.comicviewer.library.dropbox.section.DropBoxDialogUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import logcat.logcat

internal class DropBoxViewModel(
    context: Context,
    savedStateHandle: SavedStateHandle,
    private val repository: DropBoxApiRepository
) : ViewModel(), DefaultLifecycleObserver {

    companion object {
        fun Factory(context: Context, repository: DropBoxApiRepository) =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(
                    modelClass: Class<T>,
                    extras: CreationExtras
                ): T {
                    val savedStateHandle = extras.createSavedStateHandle()
                    return DropBoxViewModel(context, savedStateHandle, repository) as T
                }
            }
    }

    lateinit var file: File
    private val args = DropBoxArgs(savedStateHandle)

    private val _uiState = MutableStateFlow<DropBoxScreenUiState>(DropBoxScreenUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        repository.accountFlow.onEach {
            _uiState.value = if (it == null) {
                DropBoxScreenUiState.Login(false)
            } else {
                DropBoxScreenUiState.Loaded(
                    path = args.path,
                    profileUri = it.profilePhotoUrl.orEmpty(),
                )
            }
        }.launchIn(viewModelScope)
    }

    val pagingDataFlow = repository.accountFlow.flatMapLatest {
        Pager(PagingConfig(20)) {
            DropBoxPagingSource(args.path, repository)
        }.flow
    }.cachedIn(viewModelScope)

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        if (_uiState.value is DropBoxScreenUiState.Login && (_uiState.value as DropBoxScreenUiState.Login).isRunning) {
            Auth.getDbxCredential()?.let {
                logcat { "dropbox 認証した" }
                viewModelScope.launch {
                    repository.storeCredential(it)
                }
            } ?: kotlin.run {
                _uiState.value = DropBoxScreenUiState.Login(false)
                logcat { "dropbox 認証してない" }
            }
        }
    }

    fun login(context: Context) {
        _uiState.value = DropBoxScreenUiState.Login(true)
        Auth.startOAuth2Authentication(context, "uolcvekf83nd74j")
    }

    fun onProfileImageClick() {
        val uiState = _uiState.value
        if (uiState is DropBoxScreenUiState.Loaded) {
            viewModelScope.launch {
                val account = repository.accountFlow.first()
                _uiState.value = uiState.copy(
                    dropBoxDialogUiState = DropBoxDialogUiState.Show(
                        photoUrl = account?.profilePhotoUrl.orEmpty(),
                        name = account?.name?.displayName.orEmpty()
                    )
                )
            }
        }
    }

    fun onDialogDismissRequest() {
        val uiState = _uiState.value
        if (uiState is DropBoxScreenUiState.Loaded) {
            _uiState.value = uiState.copy(dropBoxDialogUiState = DropBoxDialogUiState.Hide)
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.signOut()
        }
    }

    private val workManager = WorkManager.getInstance(context)

    fun enqueueDownload(outputUri: String, file: File) {
        val request = OneTimeWorkRequestBuilder<DropBoxDownloadWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setInputData(workDataOf("outputUri" to outputUri, "path" to file.path))
            .setConstraints(Constraints.Builder().setRequiresStorageNotLow(true).build())
            .build()
        workManager.enqueue(request)
    }
}
