package com.sorrowblue.comicviewer.file

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.domain.model.dataOrNull
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.domain.model.onSuccess
import com.sorrowblue.comicviewer.domain.usecase.file.GetFileAttributeUseCase
import com.sorrowblue.comicviewer.domain.usecase.readlater.AddReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.readlater.DeleteReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.readlater.ExistsReadlaterUseCase
import com.sorrowblue.comicviewer.framework.ui.ScreenStateEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

sealed interface FileInfoSheetStateEvent {
    data class Favorite(val file: File) : FileInfoSheetStateEvent
    data class OpenFolder(val file: File) : FileInfoSheetStateEvent
    data object Close : FileInfoSheetStateEvent
}

internal interface FileInfoSheetState : ScreenStateEvent<FileInfoSheetStateEvent> {
    val uiState: FileInfoUiState
    val lazyPagingItems: LazyPagingItems<BookThumbnail>?
    fun onAction(action: FileInfoSheetAction)
}

@Composable
internal fun rememberFileInfoSheetState(
    file: File,
    isOpenFolderEnabled: Boolean = false,
    scope: CoroutineScope = rememberCoroutineScope(),
    viewModel: FileInfoSheetViewModel = hiltViewModel(),
): FileInfoSheetState {
    val lazyPagingItems = when (file) {
        is Book -> null
        is Folder -> {
            viewModel.pagingDataFlow(file.bookshelfId, file.path).collectAsLazyPagingItems()
        }
    }
    return remember(file) {
        FileInfoSheetStateImpl(
            file = file,
            isOpenFolderEnabled = isOpenFolderEnabled,
            lazyPagingItems = lazyPagingItems,
            scope = scope,
            getFileAttributeUseCase = viewModel.getFileAttributeUseCase,
            existsReadlaterUseCase = viewModel.existsReadlaterUseCase,
            deleteReadLaterUseCase = viewModel.deleteReadLaterUseCase,
            addReadLaterUseCase = viewModel.addReadLaterUseCase,
        )
    }
}

private class FileInfoSheetStateImpl(
    getFileAttributeUseCase: GetFileAttributeUseCase,
    existsReadlaterUseCase: ExistsReadlaterUseCase,
    isOpenFolderEnabled: Boolean,
    private val file: File,
    private val deleteReadLaterUseCase: DeleteReadLaterUseCase,
    private val addReadLaterUseCase: AddReadLaterUseCase,
    override val scope: CoroutineScope,
    override val lazyPagingItems: LazyPagingItems<BookThumbnail>?,
) : FileInfoSheetState {

    override val event: SharedFlow<FileInfoSheetStateEvent> = MutableSharedFlow()

    override var uiState: FileInfoUiState by mutableStateOf(
        FileInfoUiState(file = file, isOpenFolderEnabled = isOpenFolderEnabled)
    )
    private val runningJob = ArrayDeque<Job>()

    init {
        uiState = uiState.copy(
            file = file,
            attribute = null,
            readLaterUiState = uiState.readLaterUiState.copy(loading = true)
        )
        runningJob.forEach(Job::cancel)
        runningJob.clear()
        existsReadlaterUseCase(
            ExistsReadlaterUseCase.Request(
                file.bookshelfId,
                file.path
            )
        ).onEach { resource ->
            resource.onSuccess {
                uiState = uiState.copy(
                    readLaterUiState = uiState.readLaterUiState.copy(
                        checked = it,
                        loading = false
                    )
                )
            }
        }.launchIn(scope).let(runningJob::add)
        getFileAttributeUseCase(
            GetFileAttributeUseCase.Request(
                file.bookshelfId,
                file.path
            )
        ).onEach {
            uiState = uiState.copy(attribute = it.dataOrNull())
        }.launchIn(scope).let(runningJob::add)
    }

    override fun onAction(action: FileInfoSheetAction) {
        when (action) {
            FileInfoSheetAction.Close -> sendEvent(FileInfoSheetStateEvent.Close)
            FileInfoSheetAction.Favorite -> sendEvent(FileInfoSheetStateEvent.Favorite(file))
            FileInfoSheetAction.OpenFolder -> sendEvent(FileInfoSheetStateEvent.OpenFolder(file))
            FileInfoSheetAction.ReadLater -> updateReadLater()
        }
    }

    fun updateReadLater() {
        uiState = uiState.copy(readLaterUiState = uiState.readLaterUiState.copy(loading = true))
        scope.launch {
            delay(250)
            if (uiState.readLaterUiState.checked) {
                deleteReadLaterUseCase(DeleteReadLaterUseCase.Request(file.bookshelfId, file.path))
            } else {
                addReadLaterUseCase(AddReadLaterUseCase.Request(file.bookshelfId, file.path))
            }
        }
    }
}
