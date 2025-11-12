package com.sorrowblue.comicviewer.file

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.paging.PagingConfig
import androidx.paging.compose.LazyPagingItems
import com.sorrowblue.comicviewer.domain.model.dataOrNull
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.FileAttribute
import com.sorrowblue.comicviewer.domain.model.onSuccess
import com.sorrowblue.comicviewer.domain.usecase.file.GetFileAttributeUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.PagingFolderBookThumbnailsUseCase
import com.sorrowblue.comicviewer.domain.usecase.readlater.AddReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.readlater.DeleteReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.readlater.ExistsReadlaterUseCase
import com.sorrowblue.comicviewer.file.section.SheetActionButtonsUiState
import com.sorrowblue.comicviewer.framework.ui.paging.rememberPagingItems
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

internal data class FileInfoScreenUiState(
    val file: File,
    val attribute: FileAttribute? = null,
    val sheetActionButtonsUiState: SheetActionButtonsUiState = SheetActionButtonsUiState(),
)

@Composable
context(context: FileInfoScreenContext)
internal fun rememberFileInfoScreenState(
    file: File,
    isOpenFolderEnabled: Boolean,
): FileInfoScreenState {
    val coroutineScope = rememberCoroutineScope()
    val state = remember(file, isOpenFolderEnabled) {
        FileInfoScreenStateImpl(
            file = file,
            isOpenFolderEnabled = isOpenFolderEnabled,
            getFileAttributeUseCase = context.getFileAttributeUseCase,
            existsReadlaterUseCase = context.existsReadlaterUseCase,
            addReadLaterUseCase = context.addReadLaterUseCase,
            deleteReadLaterUseCase = context.deleteReadLaterUseCase,
            coroutineScope = coroutineScope,
        )
    }.apply {
        this.coroutineScope = coroutineScope
        this.lazyPagingItems = rememberPagingItems {
            context.pagingFolderBookThumbnailsUseCase(
                PagingFolderBookThumbnailsUseCase.Request(
                    file.bookshelfId,
                    file.path,
                    PagingConfig(10),
                ),
            )
        }
    }
    return state
}

internal interface FileInfoScreenState {
    val uiState: FileInfoScreenUiState
    val lazyPagingItems: LazyPagingItems<BookThumbnail>

    fun onReadLaterClick()
}

private class FileInfoScreenStateImpl(
    val file: File,
    val isOpenFolderEnabled: Boolean,
    val getFileAttributeUseCase: GetFileAttributeUseCase,
    val existsReadlaterUseCase: ExistsReadlaterUseCase,
    val addReadLaterUseCase: AddReadLaterUseCase,
    val deleteReadLaterUseCase: DeleteReadLaterUseCase,
    var coroutineScope: CoroutineScope,
) : FileInfoScreenState {
    private val runningJob = ArrayDeque<Job>()

    override var uiState by mutableStateOf(
        FileInfoScreenUiState(
            file = file,
            sheetActionButtonsUiState = SheetActionButtonsUiState(
                isOpenFolderEnabled = isOpenFolderEnabled,
            ),
        ),
    )
    override lateinit var lazyPagingItems: LazyPagingItems<BookThumbnail>

    init {
        existsReadlaterUseCase(
            ExistsReadlaterUseCase.Request(file.bookshelfId, file.path),
        ).onEach { resource ->
            resource.onSuccess {
                updateFileInfoSheetUiStateFile {
                    copy(
                        sheetActionButtonsUiState = sheetActionButtonsUiState.copy(
                            readLaterChecked = it,
                            readLaterLoading = false,
                        ),
                    )
                }
            }
        }.launchIn(coroutineScope)
            .let(runningJob::add)
        getFileAttributeUseCase(
            GetFileAttributeUseCase.Request(file.bookshelfId, file.path),
        ).onEach {
            updateFileInfoSheetUiStateFile {
                copy(attribute = it.dataOrNull())
            }
        }.launchIn(coroutineScope)
            .let(runningJob::add)
    }

    override fun onReadLaterClick() {
        uiState = uiState.copy(
            sheetActionButtonsUiState = uiState.sheetActionButtonsUiState.copy(
                readLaterLoading = true,
            ),
        )
        coroutineScope.launch {
            delay(300)
            if (uiState.sheetActionButtonsUiState.readLaterChecked) {
                deleteReadLaterUseCase(DeleteReadLaterUseCase.Request(file.bookshelfId, file.path))
            } else {
                addReadLaterUseCase(AddReadLaterUseCase.Request(file.bookshelfId, file.path))
            }
        }
    }

    private fun updateFileInfoSheetUiStateFile(
        update: FileInfoScreenUiState.() -> FileInfoScreenUiState,
    ) {
        uiState = uiState.update()
    }
}
