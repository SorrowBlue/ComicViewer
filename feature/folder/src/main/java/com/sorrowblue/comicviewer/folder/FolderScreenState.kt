package com.sorrowblue.comicviewer.folder

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.ramcosta.composedestinations.result.NavResult
import com.sorrowblue.comicviewer.domain.model.PagingException
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.settings.folder.FileListDisplay
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderDisplaySettings
import com.sorrowblue.comicviewer.domain.model.settings.folder.GridColumnSize
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import com.sorrowblue.comicviewer.domain.usecase.file.GetFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderDisplaySettingsUseCase
import com.sorrowblue.comicviewer.file.FileInfoSheetNavigator
import com.sorrowblue.comicviewer.folder.section.FolderFabAction
import com.sorrowblue.comicviewer.folder.section.FolderTopAppBarAction
import com.sorrowblue.comicviewer.framework.ui.SaveableScreenState
import com.sorrowblue.comicviewer.framework.ui.ScreenStateEvent
import com.sorrowblue.comicviewer.framework.ui.paging.indexOf
import com.sorrowblue.comicviewer.framework.ui.paging.isLoading
import com.sorrowblue.comicviewer.framework.ui.rememberSaveableScreenState
import kotlin.math.min
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import logcat.asLog
import logcat.logcat

internal sealed interface FolderScreenEvent {
    data class Favorite(val file: com.sorrowblue.comicviewer.domain.model.file.File) :
        FolderScreenEvent

    data class File(val file: com.sorrowblue.comicviewer.domain.model.file.File) : FolderScreenEvent

    data class Search(val bookshelfId: BookshelfId, val path: String) : FolderScreenEvent

    data class Sort(val sortType: SortType) : FolderScreenEvent

    data object Back : FolderScreenEvent
    data object Settings : FolderScreenEvent
    data object Restore : FolderScreenEvent
}

@Stable
internal interface FolderScreenState :
    SaveableScreenState,
    ScreenStateEvent<FolderScreenEvent> {

    val navigator: ThreePaneScaffoldNavigator<File>
    val snackbarHostState: SnackbarHostState
    val lazyPagingItems: LazyPagingItems<File>
    val lazyGridState: LazyGridState
    val uiState: FolderScreenUiState
    val pullRefreshState: PullToRefreshState
    fun onNavClick()
    fun onNavResult(navResult: NavResult<SortType>)
    fun onFolderTopAppBarAction(action: FolderTopAppBarAction)
    fun onFileInfoSheetAction(action: FileInfoSheetNavigator)
    fun onFolderContentsAction(action: FolderContentsAction)
    fun onLoadStateChange(lazyPagingItems: LazyPagingItems<File>)
    fun onFolderFabAction(action: FolderFabAction)
}

@Composable
internal fun rememberFolderScreenState(
    args: FolderArgs,
    navigator: ThreePaneScaffoldNavigator<File> = rememberSupportingPaneScaffoldNavigator<File>(),
    pullRefreshState: PullToRefreshState = rememberPullToRefreshState(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    viewModel: FolderViewModel = hiltViewModel(),
    scope: CoroutineScope = rememberCoroutineScope(),
    lazyGridState: LazyGridState = rememberLazyGridState(),
): FolderScreenState {
    val lazyPagingItems =
        viewModel.pagingDataFlow(args.bookshelfId, args.path).collectAsLazyPagingItems()
    return rememberSaveableScreenState {
        FolderScreenStateImpl(
            lazyPagingItems = lazyPagingItems,
            savedStateHandle = it,
            navigator = navigator,
            lazyGridState = lazyGridState,
            snackbarHostState = snackbarHostState,
            pullRefreshState = pullRefreshState,
            scope = scope,
            args = args,
            folderDisplaySettingsUseCase = viewModel.displaySettingsUseCase,
            getFileUseCase = viewModel.getFileUseCase
        )
    }
}

@OptIn(SavedStateHandleSaveableApi::class)
private class FolderScreenStateImpl(
    override val lazyPagingItems: LazyPagingItems<File>,
    override val savedStateHandle: SavedStateHandle,
    override val navigator: ThreePaneScaffoldNavigator<File>,
    override val lazyGridState: LazyGridState,
    override val snackbarHostState: SnackbarHostState,
    override val pullRefreshState: PullToRefreshState,
    override val scope: CoroutineScope,
    private val args: FolderArgs,
    private val folderDisplaySettingsUseCase: ManageFolderDisplaySettingsUseCase,
    getFileUseCase: GetFileUseCase,
) : FolderScreenState {

    private var isRestored by savedStateHandle.saveable { mutableStateOf(false) }

    override val event = MutableSharedFlow<FolderScreenEvent>()

    override var uiState by savedStateHandle.saveable {
        mutableStateOf(FolderScreenUiState(bookshelfId = args.bookshelfId))
    }
        private set

    init {
        folderDisplaySettingsUseCase.settings.distinctUntilChanged().onEach {
            uiState = uiState.copy(
                sortType = it.sortType,
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
        }.launchIn(scope)
        getFileUseCase(GetFileUseCase.Request(args.bookshelfId, args.path))
            .onEach {
                when (it) {
                    is Resource.Error -> Unit
                    is Resource.Success -> {
                        uiState = uiState.copy(
                            folderAppBarUiState = uiState.folderAppBarUiState.copy(title = it.data.name)
                        )
                    }
                }
            }.launchIn(scope)
    }

    override fun onFileInfoSheetAction(action: FileInfoSheetNavigator) {
        when (action) {
            FileInfoSheetNavigator.Back -> scope.launch { navigator.navigateBack() }
            is FileInfoSheetNavigator.Favorite ->
                sendEvent(FolderScreenEvent.Favorite(navigator.currentDestination!!.contentKey!!))

            is FileInfoSheetNavigator.OpenFolder -> Unit
        }
    }

    override fun onFolderContentsAction(action: FolderContentsAction) {
        when (action) {
            is FolderContentsAction.File -> sendEvent(FolderScreenEvent.File(action.file))
            is FolderContentsAction.FileInfo -> scope.launch {
                navigator.navigateTo(SupportingPaneScaffoldRole.Extra, action.file)
            }

            FolderContentsAction.Refresh -> refreshItems()
        }
    }

    private fun refreshItems() {
        lazyPagingItems.refresh()
    }

    override fun onFolderTopAppBarAction(action: FolderTopAppBarAction) {
        when (action) {
            FolderTopAppBarAction.Back -> sendEvent(FolderScreenEvent.Back)

            FolderTopAppBarAction.FileListDisplay ->
                updateFolderDisplaySettings {
                    it.copy(
                        fileListDisplay = when (uiState.folderAppBarUiState.fileListDisplay) {
                            FileListDisplay.Grid -> FileListDisplay.List
                            FileListDisplay.List -> FileListDisplay.Grid
                        }
                    )
                }

            FolderTopAppBarAction.GridSize ->
                if (uiState.folderAppBarUiState.fileListDisplay == FileListDisplay.Grid) {
                    updateFolderDisplaySettings {
                        it.copy(
                            gridColumnSize = when (it.gridColumnSize) {
                                GridColumnSize.Medium -> GridColumnSize.Large
                                GridColumnSize.Large -> GridColumnSize.Medium
                            }
                        )
                    }
                }

            FolderTopAppBarAction.HiddenFile ->
                updateFolderDisplaySettings {
                    it.copy(showHiddenFiles = !it.showHiddenFiles)
                }

            FolderTopAppBarAction.Search ->
                sendEvent(FolderScreenEvent.Search(args.bookshelfId, args.path))

            FolderTopAppBarAction.Settings -> sendEvent(FolderScreenEvent.Settings)
            FolderTopAppBarAction.Sort -> sendEvent(FolderScreenEvent.Sort(uiState.sortType))
        }
    }

    override fun onLoadStateChange(lazyPagingItems: LazyPagingItems<File>) {
        if (!isRestored && args.restorePath != null && 0 < lazyPagingItems.itemCount) {
            val index = lazyPagingItems.indexOf { it?.path == args.restorePath }
            if (0 <= index) {
                isRestored = true
                runCatching {
                    scope.launch {
                        lazyGridState.scrollToItem(min(index, lazyPagingItems.itemCount - 1))
                    }
                }.onFailure {
                    logcat { it.asLog() }
                }
                sendEvent(FolderScreenEvent.Restore)
            } else if (!lazyPagingItems.loadState.isLoading) {
                sendEvent(FolderScreenEvent.Restore)
            }
        }
        if (lazyPagingItems.loadState.refresh is LoadState.Error) {
            ((lazyPagingItems.loadState.refresh as LoadState.Error).error as? PagingException)?.let {
                scope.launch {
                    when (it) {
                        is PagingException.InvalidAuth -> snackbarHostState.showSnackbar("認証エラー")

                        is PagingException.InvalidServer -> snackbarHostState.showSnackbar("サーバーエラー")

                        is PagingException.NoNetwork -> snackbarHostState.showSnackbar("ネットワークエラー")

                        is PagingException.NotFound -> snackbarHostState.showSnackbar("見つかりませんでした")
                    }
                }
            }
        }
    }

    override fun onFolderFabAction(action: FolderFabAction) {
        scope.launch {
            when (action) {
                FolderFabAction.Down -> while (lazyGridState.canScrollForward) {
                    lazyGridState.scrollToItem(lazyPagingItems.itemCount)
                }

                FolderFabAction.Up -> lazyGridState.scrollToItem(0)
            }
        }
    }

    override fun onNavResult(navResult: NavResult<SortType>) {
        when (navResult) {
            NavResult.Canceled -> Unit
            is NavResult.Value -> {
                scope.launch {
                    folderDisplaySettingsUseCase.edit {
                        it.copy(sortType = navResult.value)
                    }
                    refreshItems()
                }
            }
        }
    }

    override fun onNavClick() {
        if (lazyGridState.canScrollBackward) {
            scope.launch {
                lazyGridState.scrollToItem(0)
            }
        }
    }

    private fun updateFolderDisplaySettings(edit: (FolderDisplaySettings) -> FolderDisplaySettings) {
        scope.launch {
            folderDisplaySettingsUseCase.edit(edit)
        }
    }
}
