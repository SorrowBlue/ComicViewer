package com.sorrowblue.comicviewer.folder

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.autoSaver
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.onSuccess
import com.sorrowblue.comicviewer.domain.model.settings.folder.FileListDisplay
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import com.sorrowblue.comicviewer.domain.usecase.file.AddReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.DeleteReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.GetFileUseCase
import com.sorrowblue.comicviewer.file.FileInfoSheetState
import com.sorrowblue.comicviewer.file.FileInfoUiState
import com.sorrowblue.comicviewer.folder.section.SortItem
import com.sorrowblue.comicviewer.folder.section.SortOrder
import com.sorrowblue.comicviewer.folder.section.SortSheetState
import com.sorrowblue.comicviewer.folder.section.SortSheetUiState
import com.sorrowblue.comicviewer.framework.ui.SaveableScreenState
import com.sorrowblue.comicviewer.framework.ui.rememberSaveableScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Stable
internal interface FolderScreenState : SaveableScreenState, FileInfoSheetState, SortSheetState {

    fun onFileInfoClick(file: File)
    fun onExtraPaneCloseClick()
    fun onGridSizeChange()
    fun toggleFileListType()
    val lazyGridState: LazyGridState
    val uiState: FolderScreenUiState
    var isScrollableTop: Boolean
    val pullRefreshState: PullToRefreshState
    var isSkipFirstRefresh: Boolean
    var restorePath: String?
    val pagingDataFlow: Flow<PagingData<File>>
    val bookshelfId: BookshelfId
    val path: String
    val sort: StateFlow<SortType>
    val showHidden: StateFlow<Boolean>
    fun onNavClick()
    fun onHideFileClick()
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class)
@Composable
internal fun rememberFolderScreenState(
    args: FolderArgs,
    navigator: ThreePaneScaffoldNavigator<FileInfoUiState> = rememberSupportingPaneScaffoldNavigator<FileInfoUiState>(),
    pullRefreshState: PullToRefreshState = rememberPullToRefreshState(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    viewModel: FolderViewModel = hiltViewModel(),
    scope: CoroutineScope = rememberCoroutineScope(),
    lazyGridState: LazyGridState = rememberLazyGridState(),
): FolderScreenState = rememberSaveableScreenState {
    FolderScreenStateImpl(
        savedStateHandle = it,
        navigator = navigator,
        lazyGridState = lazyGridState,
        snackbarHostState = snackbarHostState,
        args = args,
        pullRefreshState = pullRefreshState,
        viewModel = viewModel,
        scope = scope
    )
}

@OptIn(
    ExperimentalMaterial3AdaptiveApi::class,
    SavedStateHandleSaveableApi::class,
    ExperimentalMaterial3Api::class
)
private class FolderScreenStateImpl(
    override val savedStateHandle: SavedStateHandle,
    override val navigator: ThreePaneScaffoldNavigator<FileInfoUiState>,
    override val lazyGridState: LazyGridState,
    override val snackbarHostState: SnackbarHostState,
    private val args: FolderArgs,
    override val pullRefreshState: PullToRefreshState,
    private val viewModel: FolderViewModel,
    override val scope: CoroutineScope,
) : FolderScreenState {

    override val existsReadlaterUseCase = viewModel.existsReadlaterUseCase
    override val getFileAttributeUseCase = viewModel.getFileAttributeUseCase

    override val manageFolderDisplaySettingsUseCase = viewModel.displaySettingsUseCase

    override val addReadLaterUseCase: AddReadLaterUseCase = viewModel.addReadLaterUseCase
    override val deleteReadLaterUseCase: DeleteReadLaterUseCase = viewModel.deleteReadLaterUseCase

    override val pagingDataFlow = viewModel.pagingDataFlow(args.bookshelfId, args.path)
    override var restorePath by savedStateHandle.saveable("restorePath", stateSaver = autoSaver()) {
        mutableStateOf(args.restorePath)
    }
    override var isSkipFirstRefresh by savedStateHandle.saveable { mutableStateOf(true) }
    override var isScrollableTop by savedStateHandle.saveable { mutableStateOf(false) }

    override var uiState by savedStateHandle.saveable { mutableStateOf(FolderScreenUiState()) }
        private set

    override var sortSheetUiState by savedStateHandle.saveable { mutableStateOf(SortSheetUiState()) }
        private set

    override fun openSortSheet() {
        sortSheetUiState = sortSheetUiState.copy(isVisible = true)
    }

    override fun onSortSheetDismissRequest() {
        sortSheetUiState = sortSheetUiState.copy(isVisible = false)
    }

    init {
        viewModel.displaySettings.distinctUntilChanged().onEach {
            val sortItem = when (it.sortType) {
                is SortType.DATE -> SortItem.Date
                is SortType.NAME -> SortItem.Name
                is SortType.SIZE -> SortItem.Size
            }
            val sortOrder = if (it.sortType.isAsc) SortOrder.Asc else SortOrder.Desc
            uiState = uiState.copy(
                folderAppBarUiState = uiState.folderAppBarUiState.copy(
                    fileListDisplay = it.fileListDisplay,
                    gridColumnSize = it.gridColumnSize,
                    showHiddenFile = it.showHiddenFiles
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
            sortSheetUiState =
                sortSheetUiState.copy(currentSortItem = sortItem, currentSortOrder = sortOrder)
        }.launchIn(scope)
        scope.launch {
            viewModel.getFileUseCase.execute(GetFileUseCase.Request(bookshelfId, path)).first()
                .onSuccess {
                    uiState = uiState.copy(
                        folderAppBarUiState = uiState.folderAppBarUiState.copy(title = it.name)
                    )
                }
        }
    }

    override val bookshelfId get() = args.bookshelfId
    override val path get() = args.path

    override val sort = viewModel.sort

    override val showHidden = viewModel.showHidden

    override fun toggleFileListType() {
        viewModel.updateDisplay(
            when (uiState.folderAppBarUiState.fileListDisplay) {
                FileListDisplay.Grid -> FileListDisplay.List
                FileListDisplay.List -> FileListDisplay.Grid
            }
        )
    }

    override fun onHideFileClick() {
        isScrollableTop = true
        scope.launch {
            pullRefreshState.animateToThreshold()
        }
        viewModel.updateShowHide(!uiState.folderAppBarUiState.showHiddenFile)
    }

    override fun onGridSizeChange() {
        if (uiState.folderAppBarUiState.fileListDisplay == FileListDisplay.Grid) {
            viewModel.updateGridSize()
        }
    }

    override fun onExtraPaneCloseClick() {
        navigator.navigateBack()
    }

    override var fileInfoJob: Job? = null

    override fun onFileInfoClick(file: File) {
        fetchFileInfo(file) {
            navigator.navigateTo(SupportingPaneScaffoldRole.Extra, it)
        }
    }

    init {
        navigator.currentDestination?.content?.let {
            onFileInfoClick(it.file)
        }
    }

    override fun onSortItemClick(sortItem: SortItem) {
        super.onSortItemClick(sortItem)
//        isScrollableTop = true
        if (isSkipFirstRefresh) {
            isSkipFirstRefresh = false
        }
        scope.launch {
            pullRefreshState.animateToThreshold()
        }
    }

    override fun onSortOrderClick(sortOrder: SortOrder) {
        super.onSortOrderClick(sortOrder)
//        isScrollableTop = true
        if (isSkipFirstRefresh) {
            isSkipFirstRefresh = false
        }

        scope.launch {
            pullRefreshState.animateToThreshold()
        }
    }

    override fun onNavClick() {
        if (lazyGridState.canScrollBackward) {
            scope.launch {
                lazyGridState.scrollToItem(0)
            }
        }
    }
}
