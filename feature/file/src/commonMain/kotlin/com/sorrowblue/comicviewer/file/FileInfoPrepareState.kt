package com.sorrowblue.comicviewer.file

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.usecase.file.GetFileUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

internal sealed interface FileInfoPrepareUiState {
    data object Loading : FileInfoPrepareUiState

    data class Success(val file: File, val isOpenFolderEnabled: Boolean) : FileInfoPrepareUiState

    data object Error : FileInfoPrepareUiState
}

internal interface FileInfoPrepareState {
    val uiState: FileInfoPrepareUiState
}

@Composable
context(context: FileInfoScreenContext)
internal fun rememberFileInfoPrepareState(
    fileKey: File.Key,
    isOpenFolderEnabled: Boolean,
): FileInfoPrepareState {
    val coroutineScope = rememberCoroutineScope()
    return remember(fileKey, isOpenFolderEnabled) {
        FileInfoPrepareStateImpl(
            fileKey = fileKey,
            isOpenFolderEnabled = isOpenFolderEnabled,
            getFileUseCase = context.getFileUseCase,
            coroutineScope = coroutineScope,
        )
    }
}

private class FileInfoPrepareStateImpl(
    fileKey: File.Key,
    private val isOpenFolderEnabled: Boolean,
    getFileUseCase: GetFileUseCase,
    coroutineScope: CoroutineScope,
) : FileInfoPrepareState {
    override var uiState by mutableStateOf<FileInfoPrepareUiState>(FileInfoPrepareUiState.Loading)

    init {
        getFileUseCase(GetFileUseCase.Request(fileKey.bookshelfId, fileKey.path))
            .onEach {
                uiState = when (it) {
                    is Resource.Success<File> -> {
                        FileInfoPrepareUiState.Success(it.data, isOpenFolderEnabled)
                    }

                    is Resource.Error<*> -> {
                        FileInfoPrepareUiState.Error
                    }
                }
            }.launchIn(coroutineScope)
    }
}
