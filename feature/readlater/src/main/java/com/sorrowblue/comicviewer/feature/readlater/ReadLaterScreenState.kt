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
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.usecase.file.GetFileAttributeUseCase
import com.sorrowblue.comicviewer.domain.usecase.readlater.AddReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.readlater.DeleteAllReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.readlater.DeleteReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.readlater.ExistsReadlaterUseCase
import com.sorrowblue.comicviewer.feature.readlater.section.ReadLaterTopAppBarAction
import com.sorrowblue.comicviewer.file.FileInfoSheetAction
import com.sorrowblue.comicviewer.file.FileInfoSheetState
import com.sorrowblue.comicviewer.file.FileInfoUiState
import com.sorrowblue.comicviewer.framework.ui.SaveableScreenState
import com.sorrowblue.comicviewer.framework.ui.ScreenStateEvent
import com.sorrowblue.comicviewer.framework.ui.rememberSaveableScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

internal sealed interface ReadLaterScreenEvent {

    data class Favorite(val file: com.sorrowblue.comicviewer.domain.model.file.File) :
        ReadLaterScreenEvent

    data class File(val file: com.sorrowblue.comicviewer.domain.model.file.File) :
        ReadLaterScreenEvent

    data object Settings : ReadLaterScreenEvent
}

internal interface ReadLaterScreenState :
    SaveableScreenState,
    FileInfoSheetState,
    ScreenStateEvent<ReadLaterScreenEvent> {
    val pagingDataFlow: Flow<PagingData<File>>
    val lazyGridState: LazyGridState
    fun onNavClick()
    fun onTopAppBarAction(action: ReadLaterTopAppBarAction)
    fun onFileInfoSheetAction(action: FileInfoSheetAction)
    fun onContentsAction(action: ReadLaterContentsAction)
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
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
        viewModel = viewModel,
        addReadLaterUseCase = viewModel.addReadLaterUseCase,
        deleteReadLaterUseCase = viewModel.deleteReadLaterUseCase,
        deleteAllReadLaterUseCase = viewModel.deleteAllReadLaterUseCase,
        existsReadlaterUseCase = viewModel.existsReadlaterUseCase,
        getFileAttributeUseCase = viewModel.getFileAttributeUseCase,
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
private class ReadLaterScreenStateImpl(
    viewModel: ReadLaterViewModel,
    override val savedStateHandle: SavedStateHandle,
    override val snackbarHostState: SnackbarHostState,
    override val navigator: ThreePaneScaffoldNavigator<FileInfoUiState>,
    override val lazyGridState: LazyGridState,
    override val scope: CoroutineScope,
    override val getFileAttributeUseCase: GetFileAttributeUseCase,
    override val existsReadlaterUseCase: ExistsReadlaterUseCase,
    override val deleteReadLaterUseCase: DeleteReadLaterUseCase,
    override val addReadLaterUseCase: AddReadLaterUseCase,
    private val deleteAllReadLaterUseCase: DeleteAllReadLaterUseCase,
) : ReadLaterScreenState {

    override val event = MutableSharedFlow<ReadLaterScreenEvent>()
    override val pagingDataFlow = viewModel.pagingDataFlow
    override var fileInfoJob: Job? = null

    init {
        navigator.currentDestination?.content?.let {
            navigateToFileInfo(it.file)
        }
    }

    override fun onTopAppBarAction(action: ReadLaterTopAppBarAction) {
        when (action) {
            ReadLaterTopAppBarAction.ClearAll -> scope.launch {
                deleteAllReadLaterUseCase(DeleteAllReadLaterUseCase.Request).first()
            }

            ReadLaterTopAppBarAction.Settings -> sendEvent(ReadLaterScreenEvent.Settings)
        }
    }

    override fun onFileInfoSheetAction(action: FileInfoSheetAction) {
        when (action) {
            FileInfoSheetAction.Close -> navigator.navigateBack()
            FileInfoSheetAction.Favorite -> navigator.currentDestination?.content?.file?.let {
                sendEvent(ReadLaterScreenEvent.Favorite(it))
            }

            FileInfoSheetAction.OpenFolder -> TODO("Not yet implemented")
            FileInfoSheetAction.ReadLater -> onReadLaterClick()
        }
    }

    override fun onContentsAction(action: ReadLaterContentsAction) {
        when (action) {
            is ReadLaterContentsAction.File -> sendEvent(ReadLaterScreenEvent.File(action.file))
            is ReadLaterContentsAction.FileInfo -> navigateToFileInfo(action.file)
        }
    }

    override fun onNavClick() {
        if (lazyGridState.canScrollBackward) {
            scope.launch {
                lazyGridState.scrollToItem(0)
            }
        }
    }

    private fun navigateToFileInfo(file: File) {
        fetchFileInfo(file) {
            navigator.navigateTo(SupportingPaneScaffoldRole.Extra, it)
        }
    }
}
