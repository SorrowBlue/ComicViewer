package com.sorrowblue.comicviewer.favorite

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
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
import com.sorrowblue.comicviewer.framework.ui.EventFlow
import com.sorrowblue.comicviewer.framework.ui.SaveableScreenState
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.rememberCanonicalScaffoldNavigator
import com.sorrowblue.comicviewer.framework.ui.rememberSaveableScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

internal sealed interface FavoriteScreenEvent {
    data class Favorite(val bookshelfId: BookshelfId, val path: String) : FavoriteScreenEvent

    data class File(val file: com.sorrowblue.comicviewer.domain.model.file.File) :
        FavoriteScreenEvent

    data class OpenFolder(val file: com.sorrowblue.comicviewer.domain.model.file.File) :
        FavoriteScreenEvent

    data class Edit(val favoriteId: FavoriteId) : FavoriteScreenEvent

    data object Back : FavoriteScreenEvent
    data object Settings : FavoriteScreenEvent
}

internal interface FavoriteScreenState : SaveableScreenState {
    val navigator: ThreePaneScaffoldNavigator<File.Key>
    val favoriteId: FavoriteId
    val events: EventFlow<FavoriteScreenEvent>
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
    route: Favorite,
    navigator: ThreePaneScaffoldNavigator<File.Key> = rememberCanonicalScaffoldNavigator(),
    lazyGridState: LazyGridState = rememberLazyGridState(),
    scope: CoroutineScope = rememberCoroutineScope(),
    viewModel: FavoriteViewModel = koinViewModel { parametersOf(route) },
): FavoriteScreenState = rememberSaveableScreenState {
    FavoriteScreenStateImpl(
        savedStateHandle = it,
        navigator = navigator,
        lazyGridState = lazyGridState,
        route = route,
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
    override val navigator: ThreePaneScaffoldNavigator<File.Key>,
    override val lazyGridState: LazyGridState,
    private val scope: CoroutineScope,
    private val route: Favorite,
    private val manageFolderDisplaySettingsUseCase: ManageFolderDisplaySettingsUseCase,
    private val getFavoriteUseCase: GetFavoriteUseCase,
    private val deleteFavoriteUseCase: DeleteFavoriteUseCase,
    viewModel: FavoriteViewModel,
) : FavoriteScreenState {

    override val events = EventFlow<FavoriteScreenEvent>()
    override val favoriteId: FavoriteId get() = route.favoriteId
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
            FavoriteTopAppBarAction.Back -> events.tryEmit(FavoriteScreenEvent.Back)
            FavoriteTopAppBarAction.Delete -> delete()
            FavoriteTopAppBarAction.Edit -> events.tryEmit(FavoriteScreenEvent.Edit(route.favoriteId))
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

            FavoriteTopAppBarAction.Settings -> events.tryEmit(FavoriteScreenEvent.Settings)
        }
    }

    override fun onFavoriteContentsAction(action: FavoriteContentsAction) {
        when (action) {
            is FavoriteContentsAction.File -> events.tryEmit(FavoriteScreenEvent.File(action.file))
            is FavoriteContentsAction.FileInfo -> navigateToFileInfo(action.file)
        }
    }

    private fun navigateToFileInfo(file: File) {
        scope.launch {
            navigator.navigateTo(SupportingPaneScaffoldRole.Extra, file.key())
        }
    }

    private fun delete() {
        deleteFavoriteUseCase(DeleteFavoriteUseCase.Request(favoriteId)).launchIn(scope)
        events.tryEmit(FavoriteScreenEvent.Back)
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

            is FileInfoSheetNavigator.Favorite -> navigator.currentDestination?.contentKey?.let {
                events.tryEmit(FavoriteScreenEvent.Favorite(it.bookshelfId, it.path))
            }

            is FileInfoSheetNavigator.OpenFolder ->
                events.tryEmit(FavoriteScreenEvent.OpenFolder(action.file))
        }
    }

    private fun updateFolderDisplaySettings(edit: (FolderDisplaySettings) -> FolderDisplaySettings) {
        scope.launch {
            manageFolderDisplaySettingsUseCase.edit(edit)
        }
    }
}
