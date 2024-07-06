package com.sorrowblue.comicviewer.favorite

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.fold
import com.sorrowblue.comicviewer.domain.model.settings.FolderDisplaySettings
import com.sorrowblue.comicviewer.domain.usecase.favorite.GetFavoriteUseCase
import com.sorrowblue.comicviewer.file.FileInfoUiState
import com.sorrowblue.comicviewer.framework.ui.SaveableScreenState
import com.sorrowblue.comicviewer.framework.ui.rememberSaveableScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
internal interface FavoriteScreenState : SaveableScreenState {
    val favoriteId: FavoriteId
    val uiState: FavoriteScreenUiState
    val pagingDataFlow: Flow<PagingData<File>>
    val navigator: ThreePaneScaffoldNavigator<FileInfoUiState>
    val lazyGridState: LazyGridState
    fun onReadLaterClick(file: File)
    fun onExtraPaneCloseClick()
    fun toggleFileListType()
    fun delete(onBackClick: () -> Unit)
    fun toggleGridSize()
    fun onFileInfoClick(file: File)
    fun onNavClick()
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
internal fun rememberFavoriteScreenState(
    args: FavoriteArgs,
    navigator: ThreePaneScaffoldNavigator<FileInfoUiState> = rememberSupportingPaneScaffoldNavigator<FileInfoUiState>(),
    lazyGridState: LazyGridState = rememberLazyGridState(),
    scope: CoroutineScope = rememberCoroutineScope(),
    viewModel: FavoriteViewModel = hiltViewModel(),
): FavoriteScreenState = rememberSaveableScreenState {
    FavoriteScreenStateImpl(
        savedStateHandle = it,
        navigator = navigator,
        lazyGridState = lazyGridState,
        args = args,
        scope = scope,
        viewModel = viewModel
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class, SavedStateHandleSaveableApi::class)
@Stable
private class FavoriteScreenStateImpl(
    override val savedStateHandle: SavedStateHandle,
    override val navigator: ThreePaneScaffoldNavigator<FileInfoUiState>,
    override val lazyGridState: LazyGridState,
    private val args: FavoriteArgs,
    private val scope: CoroutineScope,
    private val viewModel: FavoriteViewModel,
) : FavoriteScreenState {

    override val favoriteId: FavoriteId get() = args.favoriteId
    override val pagingDataFlow = viewModel.pagingDataFlow(args.favoriteId)
    override var uiState by savedStateHandle.saveable { mutableStateOf(FavoriteScreenUiState()) }
        private set

    init {

        viewModel.displaySettings.distinctUntilChanged().onEach {
            uiState = uiState.copy(
                display = it.display,
                columnSize = it.columnSize,
                favoriteAppBarUiState = uiState.favoriteAppBarUiState.copy(
                    display = it.display,
                    columnSize = it.columnSize,
                ),
                isThumbnailEnabled = it.isEnabledThumbnail
            )
        }.launchIn(scope)
        scope.launch {
            viewModel.getFavoriteUseCase.execute(GetFavoriteUseCase.Request(favoriteId))
                .collectLatest {
                    if (it.dataOrNull != null) {
                        uiState =
                            uiState.copy(
                                favoriteAppBarUiState = uiState.favoriteAppBarUiState.copy(title = it.dataOrNull!!.name)
                            )
                    }
                }
        }
    }

    override fun delete(onBackClick: () -> Unit) {
        viewModel.delete(favoriteId, onBackClick)
    }

    override fun toggleGridSize() {
        if (uiState.favoriteAppBarUiState.display == FolderDisplaySettings.Display.Grid) {
            viewModel.updateGridSize()
        }
    }

    private var fileInfoJob: Job? = null

    override fun onFileInfoClick(file: File) {
        fileInfoJob?.cancel()
        fileInfoJob = scope.launch {
            viewModel.fileInfo(file).onEach { resource ->
                resource.fold({
                    navigator.navigateTo(SupportingPaneScaffoldRole.Extra, it)
                }, {
                })
            }.launchIn(scope)
        }
    }

    override fun toggleFileListType() {
        viewModel.updateDisplay(
            when (uiState.favoriteAppBarUiState.display) {
                FolderDisplaySettings.Display.Grid -> FolderDisplaySettings.Display.List
                FolderDisplaySettings.Display.List -> FolderDisplaySettings.Display.Grid
            }
        )
    }

    override fun onExtraPaneCloseClick() {
        navigator.navigateBack()
    }

    override fun onReadLaterClick(file: File) {
        viewModel.addToReadLater(file)
    }

    override fun onNavClick() {
        if (lazyGridState.canScrollBackward) {
            scope.launch {
                lazyGridState.scrollToItem(0)
            }
        }
    }
}
