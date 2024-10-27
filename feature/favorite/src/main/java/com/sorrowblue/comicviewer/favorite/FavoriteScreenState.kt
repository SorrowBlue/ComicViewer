package com.sorrowblue.comicviewer.favorite

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
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
import com.sorrowblue.comicviewer.domain.model.dataOrNull
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.settings.folder.FileListDisplay
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderDisplaySettings
import com.sorrowblue.comicviewer.domain.model.settings.folder.GridColumnSize
import com.sorrowblue.comicviewer.domain.usecase.favorite.DeleteFavoriteUseCase
import com.sorrowblue.comicviewer.domain.usecase.favorite.GetFavoriteUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderDisplaySettingsUseCase
import com.sorrowblue.comicviewer.favorite.section.FavoriteTopAppBarAction
import com.sorrowblue.comicviewer.file.FileInfoSheetNavigator
import com.sorrowblue.comicviewer.framework.ui.SaveableScreenState
import com.sorrowblue.comicviewer.framework.ui.ScreenStateEvent
import com.sorrowblue.comicviewer.framework.ui.rememberSaveableScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
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
    ScreenStateEvent<FavoriteScreenEvent> {
    val navigator: ThreePaneScaffoldNavigator<File>
    val favoriteId: FavoriteId
    val uiState: FavoriteScreenUiState
    val pagingDataFlow: Flow<PagingData<File>>
    val lazyGridState: LazyGridState
    fun onNavClick()
    fun onFileInfoSheetAction(action: FileInfoSheetNavigator)
    fun onFavoriteTopAppBarAction(action: FavoriteTopAppBarAction)
    fun onFavoriteContentsAction(action: FavoriteContentsAction)
}

@Composable
internal fun rememberFavoriteScreenState(
    args: FavoriteArgs,
    navigator: ThreePaneScaffoldNavigator<File> = rememberSupportingPaneScaffoldNavigator<File>(),
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
        getFavoriteUseCase = viewModel.getFavoriteUseCase,
        deleteFavoriteUseCase = viewModel.deleteFavoriteUseCase,
        manageFolderDisplaySettingsUseCase = viewModel.displaySettingsUseCase,
        viewModel = viewModel
    )
}

@OptIn(SavedStateHandleSaveableApi::class)
@Stable
private class FavoriteScreenStateImpl(
    override val savedStateHandle: SavedStateHandle,
    override val navigator: ThreePaneScaffoldNavigator<File>,
    override val lazyGridState: LazyGridState,
    override val scope: CoroutineScope,
    private val args: FavoriteArgs,
    private val manageFolderDisplaySettingsUseCase: ManageFolderDisplaySettingsUseCase,
    private val getFavoriteUseCase: GetFavoriteUseCase,
    private val deleteFavoriteUseCase: DeleteFavoriteUseCase,
    viewModel: FavoriteViewModel,
) : FavoriteScreenState {

    override val event = MutableSharedFlow<FavoriteScreenEvent>()
    override val favoriteId: FavoriteId get() = args.favoriteId
    override val pagingDataFlow = viewModel.pagingDataFlow

    override var uiState by savedStateHandle.saveable { mutableStateOf(FavoriteScreenUiState()) }
        private set

    init {
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
            getFavoriteUseCase(GetFavoriteUseCase.Request(favoriteId))
                .collectLatest {
                    if (it.dataOrNull() != null) {
                        uiState =
                            uiState.copy(
                                favoriteAppBarUiState = uiState.favoriteAppBarUiState.copy(title = it.dataOrNull()!!.name)
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
        scope.launch {
            navigator.navigateTo(SupportingPaneScaffoldRole.Extra, file)
        }
    }

    private fun delete() {
        deleteFavoriteUseCase(DeleteFavoriteUseCase.Request(favoriteId)).launchIn(scope)
        sendEvent(FavoriteScreenEvent.Back)
    }

    override fun onNavClick() {
        if (lazyGridState.canScrollBackward) {
            scope.launch {
                lazyGridState.scrollToItem(0)
            }
        }
    }

    override fun onFileInfoSheetAction(action: FileInfoSheetNavigator) {
        when (action) {
            FileInfoSheetNavigator.Back -> scope.launch {
                navigator.navigateBack()
            }

            is FileInfoSheetNavigator.Favorite -> sendEvent(
                FavoriteScreenEvent.Favorite(navigator.currentDestination!!.contentKey!!)
            )

            is FileInfoSheetNavigator.OpenFolder -> sendEvent(
                FavoriteScreenEvent.OpenFolder(navigator.currentDestination!!.contentKey!!)
            )
        }
    }

    private fun updateFolderDisplaySettings(edit: (FolderDisplaySettings) -> FolderDisplaySettings) {
        scope.launch {
            manageFolderDisplaySettingsUseCase.edit(edit)
        }
    }
}
