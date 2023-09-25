package com.sorrowblue.comicviewer.feature.library.box

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
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
import com.box.sdk.BoxAPIConnection
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.feature.library.box.data.BoxApiRepository
import com.sorrowblue.comicviewer.feature.library.box.navigation.BoxArgs
import com.sorrowblue.comicviewer.feature.library.box.section.BoxDialogUiState
import java.net.URI
import kotlin.random.Random
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
internal class BoxViewModel(
    context: Context,
    savedStateHandle: SavedStateHandle,
    private val repository: BoxApiRepository
) : ViewModel() {

    lateinit var file: Book
    private val _uiState = MutableStateFlow<BoxScreenUiState>(BoxScreenUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val args = BoxArgs(savedStateHandle)
    val path = args.path

    init {
        repository.isAuthenticated().onEach {
            if (it) {
                _uiState.value = BoxScreenUiState.Loaded(path = path)
            } else {
                _uiState.value = BoxScreenUiState.Login(false)
            }
        }.launchIn(viewModelScope)
        repository.userInfoFlow.onEach {
            if (it != null) {
                if (_uiState.value is BoxScreenUiState.Loaded) {
                    _uiState.value =
                        (_uiState.value as BoxScreenUiState.Loaded).copy(
                            profileUri = "https://api.box.com/2.0/users/${it.id}/avatar",
                            token = accessToken()
                        )
                }
            }
        }.launchIn(viewModelScope)
    }

    val pagingDataFlow = repository.userInfoFlow.filterNotNull().flatMapLatest {
        Pager(PagingConfig(20)) { BoxPagingSource(path, repository) }.flow
            .cachedIn(viewModelScope)
    }

    private suspend fun accessToken() = repository.accessToken()

    fun login(context: Context) {
        val state = Random.nextInt(20).toString()
        val url = BoxAPIConnection.getAuthorizationURL(
            "nihdm7dthg9lm7m3b41bpw7jp7b0lb9z",
            URI.create("https://comicviewer.sorrowblue.com/box/oauth2"),
            state,
            null
        )
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(context, Uri.parse(url.toString()))
    }

    fun onProfileImageClick() {
        val uiState = _uiState.value
        if (uiState is BoxScreenUiState.Loaded) {
            viewModelScope.launch {
                repository.userInfoFlow.first()?.let {
                    _uiState.value = uiState.copy(
                        boxDialogUiState = BoxDialogUiState.Show(
                            it.avatarURL,
                            it.name
                        )
                    )
                }
            }
        }
    }

    fun onDialogDismissRequest() {
        val uiState = _uiState.value
        if (uiState is BoxScreenUiState.Loaded) {
            _uiState.value = uiState.copy(boxDialogUiState = BoxDialogUiState.Hide)
        }
    }

    fun logout() {
        viewModelScope.launch {
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

    companion object {

        fun Factory(context: Context, repository: BoxApiRepository) =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(
                    modelClass: Class<T>,
                    extras: CreationExtras
                ): T {
                    val savedStateHandle = extras.createSavedStateHandle()
                    return BoxViewModel(context, savedStateHandle, repository) as T
                }
            }
    }
}
