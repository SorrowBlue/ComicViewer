package com.sorrowblue.comicviewer.feature.readlater

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.paging.PagingData
import com.ramcosta.composedestinations.annotation.Destination
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.usecase.file.AddReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.DeleteReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.ExistsReadlaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.GetFileAttributeUseCase
import com.sorrowblue.comicviewer.file.FileInfoSheetState
import com.sorrowblue.comicviewer.file.FileInfoUiState
import com.sorrowblue.comicviewer.framework.ui.SaveableScreenState
import com.sorrowblue.comicviewer.framework.ui.rememberSaveableScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

internal interface ReadLaterScreenState : SaveableScreenState, FileInfoSheetState {
    val pagingDataFlow: Flow<PagingData<File>>
    val lazyGridState: LazyGridState
    fun onFileInfoClick(file: File)
    fun onExtraPaneCloseClick()
    fun onClearAllClick()
    fun onNavClick()
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Destination
@Composable
internal fun rememberReadLaterScreenState(
    navigator: ThreePaneScaffoldNavigator<FileInfoUiState> = rememberSupportingPaneScaffoldNavigator<FileInfoUiState>(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    lazyGridState: LazyGridState = rememberLazyGridState(),
    scope: CoroutineScope = rememberCoroutineScope(),
    viewModel: ReadLaterViewModel = hiltViewModel(),
): ReadLaterScreenState = rememberSaveableScreenState {
    ReadLaterScreenStateImpl(
        savedStateHandle = it,
        snackbarHostState = snackbarHostState,
        navigator = navigator,
        lazyGridState = lazyGridState,
        scope = scope,
        viewModel = viewModel
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
private class ReadLaterScreenStateImpl(
    override val savedStateHandle: SavedStateHandle,
    override val snackbarHostState: SnackbarHostState,
    override val navigator: ThreePaneScaffoldNavigator<FileInfoUiState>,
    override val lazyGridState: LazyGridState,
    override val scope: CoroutineScope,
    private val viewModel: ReadLaterViewModel,
) : ReadLaterScreenState {

    override val addReadLaterUseCase: AddReadLaterUseCase = viewModel.addReadLaterUseCase
    override val deleteReadLaterUseCase: DeleteReadLaterUseCase = viewModel.deleteReadLaterUseCase
    override val pagingDataFlow = viewModel.pagingDataFlow

    override val existsReadlaterUseCase: ExistsReadlaterUseCase = viewModel.existsReadlaterUseCase

    override val getFileAttributeUseCase: GetFileAttributeUseCase =
        viewModel.getFileAttributeUseCase

    override var fileInfoJob: Job? = null

    override fun onFileInfoClick(file: File) {
        fetchFileInfo(file) {
            navigator.navigateTo(SupportingPaneScaffoldRole.Extra, it)
        }
    }

    override fun onExtraPaneCloseClick() {
        navigator.navigateBack()
    }

    override fun onClearAllClick() {
        viewModel.clearAll()
    }

    override fun onNavClick() {
        if (lazyGridState.canScrollBackward) {
            scope.launch {
                lazyGridState.scrollToItem(0)
            }
        }
    }
}
