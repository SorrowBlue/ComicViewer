/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.file

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.BookFolder
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.FileAttribute
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.file.section.FileInfoButtonsUiState
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

internal data class FileInfoScreenUiState(
    val file: File,
    val attribute: FileAttribute? = null,
    val fileInfoButtonsUiState: FileInfoButtonsUiState = FileInfoButtonsUiState(),
)

@Composable
internal fun rememberFileInfoScreenState(
    file: File,
    isOpenFolderEnabled: Boolean,
    viewModel: FileInfoViewModel =
    assistedMetroViewModel<FileInfoViewModel, FileInfoViewModel.Factory> {
        create(file)
    },
): FileInfoScreenState {
    val coroutineScope = rememberCoroutineScope()
    return remember(file, isOpenFolderEnabled) {
        FileInfoScreenStateImpl(
            file = file,
            isOpenFolderEnabled = isOpenFolderEnabled,
            coroutineScope = coroutineScope,
            isReadLaterFlow = viewModel.isReadLaterFlow,
            fileAttributeFlow = viewModel.fileAttributeFlow,
            fileSizeFlow = viewModel.fileSizeFlow,
            updateReadLater = viewModel::updateReadLater,
        )
    }.apply {
        lazyPagingItems = viewModel.pagingFlow?.collectAsLazyPagingItems()
    }
}

internal interface FileInfoScreenState {
    val uiState: FileInfoScreenUiState
    val lazyPagingItems: LazyPagingItems<BookThumbnail>?

    fun onReadLaterClick()
}

private class FileInfoScreenStateImpl(
    private val file: File,
    private val isOpenFolderEnabled: Boolean,
    coroutineScope: CoroutineScope,
    isReadLaterFlow: StateFlow<Boolean>,
    fileAttributeFlow: SharedFlow<FileAttribute?>,
    fileSizeFlow: SharedFlow<Long>,
    private val updateReadLater: () -> Unit,
) : FileInfoScreenState {

    override var uiState by mutableStateOf(
        FileInfoScreenUiState(
            file = file,
            fileInfoButtonsUiState = FileInfoButtonsUiState(
                isOpenFolderEnabled = isOpenFolderEnabled,
            ),
        ),
    )

    override var lazyPagingItems: LazyPagingItems<BookThumbnail>? = null

    init {
        isReadLaterFlow.onEach {
            uiState = uiState.copy(
                fileInfoButtonsUiState = uiState.fileInfoButtonsUiState.copy(
                    readLaterChecked = it,
                    readLaterLoading = false,
                ),
            )
        }.launchIn(coroutineScope)
        fileAttributeFlow.onEach { fileAttribute ->
            uiState = uiState.copy(attribute = fileAttribute)
        }.launchIn(coroutineScope)

        fileSizeFlow.onEach { fileSize ->
            uiState = uiState.copy(
                file = when (val ufile = uiState.file) {
                    is BookFile -> ufile.copy(size = fileSize)
                    is BookFolder -> ufile.copy(size = fileSize)
                    is Folder -> ufile.copy(size = fileSize)
                },
            )
        }.launchIn(coroutineScope)
    }

    override fun onReadLaterClick() {
        uiState = uiState.copy(
            fileInfoButtonsUiState = uiState.fileInfoButtonsUiState.copy(
                readLaterLoading = true,
            ),
        )
        updateReadLater()
    }
}
