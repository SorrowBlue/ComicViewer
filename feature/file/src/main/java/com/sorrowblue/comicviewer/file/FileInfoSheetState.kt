package com.sorrowblue.comicviewer.file

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.usecase.file.AddReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.DeleteReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.ExistsReadlaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.GetFileAttributeUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch

interface FileInfoSheetState {

    var fileInfoJob: Job?

    @OptIn(ExperimentalMaterial3AdaptiveApi::class)
    val navigator: ThreePaneScaffoldNavigator<FileInfoUiState>

    fun fetchFileInfo(file: File, onGet: (FileInfoUiState) -> Unit) {
        val getRequest = GetFileAttributeUseCase.Request(file.bookshelfId, file.path)
        val isRequest = ExistsReadlaterUseCase.Request(file.bookshelfId, file.path)
        onGet(FileInfoUiState(file, null, false))
        fileInfoJob?.cancel()
        fileInfoJob = scope.launch {
            getFileAttributeUseCase(getRequest)
                .combine(existsReadlaterUseCase(isRequest)) { fileAttribute, existsReadLater ->
                    if (fileAttribute is Resource.Success && existsReadLater is Resource.Success) {
                        onGet(
                            FileInfoUiState(
                                file,
                                fileAttribute.data,
                                existsReadLater.data,
                                false
                            )
                        )
                    } else {
                        Resource.Error(GetFileAttributeUseCase.Error.NotFound)
                    }
                }.launchIn(this)
        }
    }

    @OptIn(ExperimentalMaterial3AdaptiveApi::class)
    fun onReadLaterClick() {
        val fileInfo = navigator.currentDestination?.content ?: return
        val file = fileInfo.file
        scope.launch {
            if (fileInfo.isReadLater) {
                deleteReadLaterUseCase(DeleteReadLaterUseCase.Request(file.bookshelfId, file.path))
                    .first()
            } else {
                addReadLaterUseCase(AddReadLaterUseCase.Request(file.bookshelfId, file.path))
                    .first()
            }
        }
        scope.launch {
            if (fileInfo.isReadLater) {
                snackbarHostState.showSnackbar("「${file.name}」を\"あとで読む\"から削除しました")
            } else {
                snackbarHostState.showSnackbar("「${file.name}」を\"あとで読む\"に追加しました")
            }
        }
    }

    val getFileAttributeUseCase: GetFileAttributeUseCase
    val existsReadlaterUseCase: ExistsReadlaterUseCase
    val deleteReadLaterUseCase: DeleteReadLaterUseCase
    val addReadLaterUseCase: AddReadLaterUseCase
    val snackbarHostState: SnackbarHostState
    val scope: CoroutineScope
}
