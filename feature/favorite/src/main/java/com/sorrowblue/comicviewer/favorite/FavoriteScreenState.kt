package com.sorrowblue.comicviewer.favorite

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
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
import com.sorrowblue.comicviewer.domain.model.settings.folder.FileListDisplay
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderDisplaySettings
import com.sorrowblue.comicviewer.domain.model.settings.folder.GridColumnSize
import com.sorrowblue.comicviewer.domain.usecase.favorite.DeleteFavoriteUseCase
import com.sorrowblue.comicviewer.domain.usecase.favorite.GetFavoriteUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.AddReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.DeleteReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.ExistsReadlaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.GetFileAttributeUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderDisplaySettingsUseCase
import com.sorrowblue.comicviewer.favorite.section.FavoriteTopAppBarAction
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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

internal sealed interface FavoriteScreenEvent {
    data class Favorite(val file: com.sorrowblue.comicviewer.domain.model.file.File) :
        FavoriteScreenEvent

    data class File(val file: com.sorrowblue.comicviewer.domain.model.file.File) :
        FavoriteScreenEvent

    data class OpenFolder(val file: com.sorrowblue.comicviewer.domain.model.file.File) :
        FavoriteScreenEvent

    data class Edit(val favoriteId: FavoriteId) : FavoriteScreenEvent

    data object Back : FavoriteScreenEvent
    data object Settings : FavoriteScreenEvent
}

internal interface FavoriteScreenState :
    SaveableScreenState,
    FileInfoSheetState,
    ScreenStateEvent<FavoriteScreenEvent> {
    val favoriteId: FavoriteId
    val uiState: FavoriteScreenUiState
    val pagingDataFlow: Flow<PagingData<File>>
    val lazyGridState: LazyGridState
    fun onNavClick()
    fun onFileInfoSheetAction(action: FileInfoSheetAction)
    fun onFavoriteTopAppBarAction(action: FavoriteTopAppBarAction)
    fun onFavoriteContentsAction(action: FavoriteContentsAction)
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
internal fun rememberFavoriteScreenState(
    args: FavoriteArgs,
    navigator: ThreePaneScaffoldNavigator<FileInfoUiState> = rememberSupportingPaneScaffoldNavigator<FileInfoUiState>(),
    lazyGridState: LazyGridState = rememberLazyGridState(),
    scope: CoroutineScope = rememberCoroutineScope(),
    viewModel: FavoriteViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
): FavoriteScreenState = rememberSaveableScreenState {
    FavoriteScreenStateImpl(
        savedStateHandle = it,
        navigator = navigator,
        lazyGridState = lazyGridState,
        args = args,
        scope = scope,
        snackbarHostState = snackbarHostState,
        addReadLaterUseCase = viewModel.addReadLaterUseCase,
        deleteReadLaterUseCase = viewModel.deleteReadLaterUseCase,
        existsReadlaterUseCase = viewModel.existsReadlaterUseCase,
        getFileAttributeUseCase = viewModel.getFileAttributeUseCase,
        getFavoriteUseCase = viewModel.getFavoriteUseCase,
        deleteFavoriteUseCase = viewModel.deleteFavoriteUseCase,
        manageFolderDisplaySettingsUseCase = viewModel.displaySettingsUseCase,
        viewModel = viewModel
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class, SavedStateHandleSaveableApi::class)
@Stable
private class FavoriteScreenStateImpl(
    override val savedStateHandle: SavedStateHandle,
    override val navigator: ThreePaneScaffoldNavigator<FileInfoUiState>,
    override val lazyGridState: LazyGridState,
    override val scope: CoroutineScope,
    override val getFileAttributeUseCase: GetFileAttributeUseCase,
    override val existsReadlaterUseCase: ExistsReadlaterUseCase,
    override val deleteReadLaterUseCase: DeleteReadLaterUseCase,
    override val addReadLaterUseCase: AddReadLaterUseCase,
    override val snackbarHostState: SnackbarHostState,
    private val args: FavoriteArgs,
    private val manageFolderDisplaySettingsUseCase: ManageFolderDisplaySettingsUseCase,
    private val getFavoriteUseCase: GetFavoriteUseCase,
    private val deleteFavoriteUseCase: DeleteFavoriteUseCase,
    viewModel: FavoriteViewModel,
) : FavoriteScreenState {

    override val event = MutableSharedFlow<FavoriteScreenEvent>()
    override val favoriteId: FavoriteId get() = args.favoriteId
    override val pagingDataFlow = viewModel.pagingDataFlow
    override var fileInfoJob: Job? = null

    override var uiState by savedStateHandle.saveable { mutableStateOf(FavoriteScreenUiState()) }
        private set

    init {
        navigator.currentDestination?.content?.let {
            navigateToFileInfo(it.file)
        }
        manageFolderDisplaySettingsUseCase.settings.distinctUntilChanged().onEach {
            uiState = uiState.copy(
                favoriteAppBarUiState = uiState.favoriteAppBarUiState.copy(
                    fileListDisplay = it.fileListDisplay,
                    gridColumnSize = it.gridColumnSize,
                ),
                fileLazyVerticalGridUiState = uiState.fileLazyVerticalGridUiState.copy(
                    fileListDisplay = it.fileListDisplay,
                    columnSize = it.gridColumnSize,
                    imageScale = it.imageScale,
                    imageFilterQuality = it.imageFilterQuality,
                    fontSize = it.fontSize,
                    showThumbnails = it.showThumbnails
                )
            )
        }.launchIn(scope)
        scope.launch {
            getFavoriteUseCase.execute(GetFavoriteUseCase.Request(favoriteId))
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

    override fun onFavoriteTopAppBarAction(action: FavoriteTopAppBarAction) {
        when (action) {
            FavoriteTopAppBarAction.Back -> sendEvent(FavoriteScreenEvent.Back)
            FavoriteTopAppBarAction.Delete -> delete()
            FavoriteTopAppBarAction.Edit -> sendEvent(FavoriteScreenEvent.Edit(args.favoriteId))
            FavoriteTopAppBarAction.FileListDisplay ->
                updateFolderDisplaySettings {
                    it.copy(
                        fileListDisplay = when (uiState.favoriteAppBarUiState.fileListDisplay) {
                            FileListDisplay.Grid -> FileListDisplay.List
                            FileListDisplay.List -> FileListDisplay.Grid
                        }
                    )
                }

            FavoriteTopAppBarAction.GridSize ->
                if (uiState.favoriteAppBarUiState.fileListDisplay == FileListDisplay.Grid) {
                    updateFolderDisplaySettings {
                        it.copy(
                            gridColumnSize = when (it.gridColumnSize) {
                                GridColumnSize.Medium -> GridColumnSize.Large
                                GridColumnSize.Large -> GridColumnSize.Medium
                            }
                        )
                    }
                }

            FavoriteTopAppBarAction.Settings -> sendEvent(FavoriteScreenEvent.Settings)
        }
    }

    override fun onFavoriteContentsAction(action: FavoriteContentsAction) {
        when (action) {
            is FavoriteContentsAction.File -> sendEvent(FavoriteScreenEvent.File(action.file))
            is FavoriteContentsAction.FileInfo -> navigateToFileInfo(action.file)
        }
    }

    private fun navigateToFileInfo(file: File) {
        fetchFileInfo(file) {
            navigator.navigateTo(
                SupportingPaneScaffoldRole.Extra,
                it.copy(isOpenFolderEnabled = true)
            )
        }
    }

    private fun delete() {
        scope.launch {
            deleteFavoriteUseCase.execute(DeleteFavoriteUseCase.Request(favoriteId))
                .collect()
            sendEvent(FavoriteScreenEvent.Back)
        }
    }

    override fun onNavClick() {
        if (lazyGridState.canScrollBackward) {
            scope.launch {
                lazyGridState.scrollToItem(0)
            }
        }
    }

    override fun onFileInfoSheetAction(action: FileInfoSheetAction) {
        when (action) {
            FileInfoSheetAction.Close -> navigator.navigateBack()
            FileInfoSheetAction.Favorite -> sendEvent(
                FavoriteScreenEvent.Favorite(navigator.currentDestination!!.content!!.file)
            )

            FileInfoSheetAction.OpenFolder -> sendEvent(
                FavoriteScreenEvent.OpenFolder(navigator.currentDestination!!.content!!.file)
            )

            FileInfoSheetAction.ReadLater -> onReadLaterClick()
        }
    }

    private fun updateFolderDisplaySettings(edit: (FolderDisplaySettings) -> FolderDisplaySettings) {
        scope.launch {
            manageFolderDisplaySettingsUseCase.edit(edit)
        }
    }
}
