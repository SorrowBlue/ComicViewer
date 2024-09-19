package com.sorrowblue.comicviewer.file

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.FileAttribute
import com.sorrowblue.comicviewer.domain.usecase.file.GetFileAttributeUseCase
import com.sorrowblue.comicviewer.domain.usecase.readlater.AddReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.readlater.DeleteReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.readlater.ExistsReadlaterUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch

interface FileInfoSheetState {

    var fileInfoJob: Job?

    val navigator: ThreePaneScaffoldNavigator<FileInfoUiState>

    fun fetchFileInfo(file: File, onGet: (FileInfoUiState) -> Unit = {}) {
        val getRequest = GetFileAttributeUseCase.Request(file.bookshelfId, file.path)
        onGet(
            FileInfoUiState(file, null, false).also {
                navigator.navigateTo(SupportingPaneScaffoldRole.Extra, it)
            }
        )

        fileInfoJob?.cancel()
        fileInfoJob = scope.launch {
            val erluc: Flow<Resource<Boolean, Unit>> =
                existsReadlaterUseCase(ExistsReadlaterUseCase.Request(file.bookshelfId, file.path))
            val gfauc: Flow<Resource<FileAttribute, GetFileAttributeUseCase.Error>> =
                getFileAttributeUseCase(getRequest)
            gfauc.combine(erluc) { fileAttribute, existsReadlater ->
                if (fileAttribute is Resource.Success && existsReadlater is Resource.Success) {
                    onGet(
                        FileInfoUiState(
                            file,
                            fileAttribute.data,
                            existsReadlater.data,
                            false
                        ).also {
                            navigator.navigateTo(SupportingPaneScaffoldRole.Extra, it)
                        }
                    )
                }
            }.launchIn(this)
        }
    }

    fun onReadLaterClick() {
        val fileInfo = navigator.currentDestination?.contentKey ?: return
        val file = fileInfo.file
        scope.launch {
            if (fileInfo.isReadLater) {
                deleteReadLaterUseCase(DeleteReadLaterUseCase.Request(file.bookshelfId, file.path))
            } else {
                addReadLaterUseCase(AddReadLaterUseCase.Request(file.bookshelfId, file.path))
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
