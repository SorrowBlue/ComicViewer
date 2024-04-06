package com.sorrowblue.comicviewer.feature.history

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
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.file.FileInfoSheetState
import com.sorrowblue.comicviewer.file.FileInfoUiState
import com.sorrowblue.comicviewer.framework.ui.SaveableScreenState
import com.sorrowblue.comicviewer.framework.ui.rememberSaveableScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow

interface HistoryScreenState : SaveableScreenState, FileInfoSheetState {
    val pagingDataFlow: Flow<PagingData<Book>>
    fun onFileInfoClick(file: File)
    fun onExtraPaneCloseClick()
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
internal fun rememberHistoryScreenState(
    navigator: ThreePaneScaffoldNavigator<FileInfoUiState> = rememberSupportingPaneScaffoldNavigator<FileInfoUiState>(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    scope: CoroutineScope = rememberCoroutineScope(),
    viewModel: HistoryViewModel = hiltViewModel(),
): HistoryScreenState = rememberSaveableScreenState {
    HistoryScreenStateImpl(
        savedStateHandle = it,
        navigator = navigator,
        snackbarHostState = snackbarHostState,
        scope = scope,
        viewModel = viewModel
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
private class HistoryScreenStateImpl(
    override val savedStateHandle: SavedStateHandle,
    override val navigator: ThreePaneScaffoldNavigator<FileInfoUiState>,
    override val snackbarHostState: SnackbarHostState,
    override val scope: CoroutineScope,
    viewModel: HistoryViewModel,
) : HistoryScreenState {
    override val pagingDataFlow = viewModel.pagingDataFlow

    override fun onFileInfoClick(file: File) {
        fetchFileInfo(file) {
            navigator.navigateTo(SupportingPaneScaffoldRole.Extra, it)
        }
    }

    override fun onExtraPaneCloseClick() {
        navigator.navigateBack()
    }

    override var fileInfoJob: Job? = null
    override val getFileAttributeUseCase = viewModel.getFileAttributeUseCase
    override val existsReadlaterUseCase = viewModel.existsReadlaterUseCase
    override val deleteReadLaterUseCase = viewModel.deleteReadLaterUseCase
    override val addReadLaterUseCase = viewModel.addReadLaterUseCase
}
