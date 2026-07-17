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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.model.dataOrNull
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.usecase.file.GetFileUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactoryKey
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn

internal sealed interface FileInfoPrepareUiState {
    data object Loading : FileInfoPrepareUiState

    data class Success(val file: File, val isOpenFolderEnabled: Boolean) : FileInfoPrepareUiState

    data object Error : FileInfoPrepareUiState
}

internal interface FileInfoPrepareState {
    val uiState: FileInfoPrepareUiState
}

@AssistedInject
internal class FileInfoPrepareViewModel(
    @Assisted fileKey: File.Key,
    getFileUseCase: GetFileUseCase,
) : ViewModel() {

    val fileFlow = getFileUseCase(GetFileUseCase.Request(fileKey.bookshelfId, fileKey.path))
        .map { it.dataOrNull() }.shareIn(viewModelScope, SharingStarted.Eagerly, 1)

    @AssistedFactory
    @ManualViewModelAssistedFactoryKey
    @ContributesIntoMap(AppScope::class)
    interface Factory : ManualViewModelAssistedFactory {
        fun create(fileKey: File.Key): FileInfoPrepareViewModel
    }
}

@Composable
internal fun rememberFileInfoPrepareState(
    fileKey: File.Key,
    isOpenFolderEnabled: Boolean,
    viewModel: FileInfoPrepareViewModel =
    assistedMetroViewModel<FileInfoPrepareViewModel, FileInfoPrepareViewModel.Factory> {
        create(fileKey = fileKey)
    },
): FileInfoPrepareState {
    val coroutineScope = rememberCoroutineScope()
    return remember(isOpenFolderEnabled) {
        FileInfoPrepareStateImpl(
            coroutineScope = coroutineScope,
            isOpenFolderEnabled = isOpenFolderEnabled,
            fileFlow = viewModel.fileFlow,
        )
    }
}

private class FileInfoPrepareStateImpl(
    coroutineScope: CoroutineScope,
    isOpenFolderEnabled: Boolean,
    fileFlow: SharedFlow<File?>,
) : FileInfoPrepareState {
    override var uiState by mutableStateOf<FileInfoPrepareUiState>(FileInfoPrepareUiState.Loading)

    init {
        fileFlow.map { file ->
            uiState = if (file != null) {
                FileInfoPrepareUiState.Success(file, isOpenFolderEnabled)
            } else {
                FileInfoPrepareUiState.Error
            }
        }.launchIn(coroutineScope)
    }
}
