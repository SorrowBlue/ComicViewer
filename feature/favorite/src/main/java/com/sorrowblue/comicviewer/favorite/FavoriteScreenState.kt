package com.sorrowblue.comicviewer.favorite

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.settings.FolderDisplaySettings
import com.sorrowblue.comicviewer.domain.usecase.favorite.GetFavoriteUseCase
import com.sorrowblue.comicviewer.favorite.navigation.FavoriteArgs
import com.sorrowblue.comicviewer.file.component.FileContentType
import com.sorrowblue.comicviewer.file.component.toFileContentLayout
import com.sorrowblue.comicviewer.framework.ui.calculateStandardPaneScaffoldDirective
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
internal interface FavoriteScreenState {
    val favoriteId: FavoriteId
    val uiState: FavoriteScreenUiState
    val pagingDataFlow: Flow<PagingData<File>>
    val navigator: ThreePaneScaffoldNavigator
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
    savedStateHandle: SavedStateHandle,
    navigator: ThreePaneScaffoldNavigator = rememberSupportingPaneScaffoldNavigator(
        calculateStandardPaneScaffoldDirective(currentWindowAdaptiveInfo())
    ),
    lazyGridState: LazyGridState = rememberLazyGridState(),
    args: FavoriteArgs,
    scope: CoroutineScope = rememberCoroutineScope(),
    viewModel: FavoriteViewModel = hiltViewModel(),
): FavoriteScreenState = remember {
    FavoriteScreenStateImpl(
        savedStateHandle = savedStateHandle,
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
    savedStateHandle: SavedStateHandle,
    override val navigator: ThreePaneScaffoldNavigator,
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
        viewModel.displaySettings.map(FolderDisplaySettings::toFileContentLayout)
            .distinctUntilChanged().onEach {
                uiState = uiState.copy(
                    favoriteAppBarUiState = uiState.favoriteAppBarUiState.copy(fileContentType = it),
                    fileContentType = it
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
        if (uiState.fileContentType is FileContentType.Grid) {
            viewModel.updateGridSize()
        }
    }

    override fun onFileInfoClick(file: File) {
        uiState = uiState.copy(file = file)
        navigator.navigateTo(SupportingPaneScaffoldRole.Extra)
    }

    override fun toggleFileListType() {
        viewModel.updateDisplay(
            when (uiState.fileContentType) {
                is FileContentType.Grid -> FolderDisplaySettings.Display.LIST
                FileContentType.List -> FolderDisplaySettings.Display.GRID
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
