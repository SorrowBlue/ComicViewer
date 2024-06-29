package com.sorrowblue.comicviewer.feature.library.dropbox

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.getSystemService
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.feature.library.dropbox.data.DropBoxApiRepository
import com.sorrowblue.comicviewer.feature.library.dropbox.section.DropBoxDialogUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
    fun onFileClick(file: File, onFolderClick: (Folder) -> Unit)

    fun enqueueDownload(file: File)
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
    private val context: Context,
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

    override fun enqueueDownload(file: File) {
        val manager = context.getSystemService<DownloadManager>()!!
        scope.launch(Dispatchers.IO) {
            val link = repository.downloadLink(file.path)
            val request = DownloadManager.Request(link.toUri())
            request.setTitle(file.name)
            request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                "comicviewer/${file.name}"
            )
            manager.enqueue(request)
        }
    }

    override fun onFileClick(
        file: File,
        onFolderClick: (Folder) -> Unit,
    ) {
        when (file) {
            is Book -> {
                enqueueDownload(file)
            }

            is Folder -> {
                onFolderClick(file)
            }
        }
    }
}
