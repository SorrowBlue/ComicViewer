package com.sorrowblue.comicviewer.folder

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
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
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.compose.LazyPagingItems
import com.ramcosta.composedestinations.result.NavResult
import com.sorrowblue.comicviewer.domain.model.PagingException
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.settings.folder.FileListDisplay
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderDisplaySettings
import com.sorrowblue.comicviewer.domain.model.settings.folder.GridColumnSize
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import com.sorrowblue.comicviewer.domain.usecase.file.AddReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.DeleteReadLaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.ExistsReadlaterUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.GetFileAttributeUseCase
import com.sorrowblue.comicviewer.domain.usecase.file.GetFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.ManageFolderDisplaySettingsUseCase
import com.sorrowblue.comicviewer.file.FileInfoSheetAction
import com.sorrowblue.comicviewer.file.FileInfoSheetState
import com.sorrowblue.comicviewer.file.FileInfoUiState
import com.sorrowblue.comicviewer.folder.section.FolderTopAppBarAction
import com.sorrowblue.comicviewer.framework.ui.SaveableScreenState
import com.sorrowblue.comicviewer.framework.ui.ScreenStateEvent
import com.sorrowblue.comicviewer.framework.ui.paging.indexOf
import com.sorrowblue.comicviewer.framework.ui.paging.isLoading
import com.sorrowblue.comicviewer.framework.ui.rememberSaveableScreenState
import kotlin.math.min
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flattenConcat
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

    data object Refresh : FolderScreenEvent
    data object Back : FolderScreenEvent
    data object Settings : FolderScreenEvent
    data object Restore : FolderScreenEvent
}

@OptIn(ExperimentalMaterial3Api::class)
@Stable
internal interface FolderScreenState :
    SaveableScreenState,
    FileInfoSheetState,
    ScreenStateEvent<FolderScreenEvent> {

    val lazyGridState: LazyGridState
    val uiState: FolderScreenUiState
    val pullRefreshState: PullToRefreshState
    val pagingDataFlow: Flow<PagingData<File>>
    fun onNavClick()
    fun onNavResult(navResult: NavResult<SortType>)
    fun onFolderTopAppBarAction(action: FolderTopAppBarAction)
    fun onFileInfoSheetAction(action: FileInfoSheetAction)
    fun onFolderContentsAction(action: FolderContentsAction)
    fun onLoadStateChange(lazyPagingItems: LazyPagingItems<File>)
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
        pullRefreshState = pullRefreshState,
        scope = scope,
        args = args,
        folderDisplaySettingsUseCase = viewModel.displaySettingsUseCase,
        existsReadlaterUseCase = viewModel.existsReadlaterUseCase,
        addReadLaterUseCase = viewModel.addReadLaterUseCase,
        deleteReadLaterUseCase = viewModel.deleteReadLaterUseCase,
        getFileAttributeUseCase = viewModel.getFileAttributeUseCase,
        pagingFileUseCase = viewModel.pagingFileUseCase,
        getFileUseCase = viewModel.getFileUseCase
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
    override val pullRefreshState: PullToRefreshState,
    override val scope: CoroutineScope,
    private val args: FolderArgs,
    private val folderDisplaySettingsUseCase: ManageFolderDisplaySettingsUseCase,
    getFileUseCase: GetFileUseCase,
    pagingFileUseCase: PagingFileUseCase,
    override val existsReadlaterUseCase: ExistsReadlaterUseCase,
    override val getFileAttributeUseCase: GetFileAttributeUseCase,
    override val addReadLaterUseCase: AddReadLaterUseCase,
    override val deleteReadLaterUseCase: DeleteReadLaterUseCase,
) : FolderScreenState {

    private var isRestored by savedStateHandle.saveable { mutableStateOf(false) }

    override val event = MutableSharedFlow<FolderScreenEvent>()

    @OptIn(ExperimentalCoroutinesApi::class)
    override val pagingDataFlow =
        pagingFileUseCase.execute(
            PagingFileUseCase.Request(PagingConfig(20), args.bookshelfId, args.path)
        ).filterSuccess().flattenConcat().cachedIn(scope)
    override var uiState by savedStateHandle.saveable { mutableStateOf(FolderScreenUiState()) }
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
        getFileUseCase.execute(GetFileUseCase.Request(args.bookshelfId, args.path))
            .onEach {
                when (it) {
                    is Resource.Error -> TODO()
                    is Resource.Success -> {
                        uiState = uiState.copy(
                            folderAppBarUiState = uiState.folderAppBarUiState.copy(title = it.data.name)
                        )
                    }
                }
            }.launchIn(scope)
    }

    override fun onFileInfoSheetAction(action: FileInfoSheetAction) {
        when (action) {
            FileInfoSheetAction.Close ->
                navigator.navigateBack()

            FileInfoSheetAction.Favorite ->
                sendEvent(FolderScreenEvent.Favorite(navigator.currentDestination!!.content!!.file))

            FileInfoSheetAction.OpenFolder ->
                TODO("Not yet implemented")

            FileInfoSheetAction.ReadLater -> onReadLaterClick()
        }
    }

    override fun onFolderContentsAction(action: FolderContentsAction) {
        when (action) {
            is FolderContentsAction.File -> sendEvent(FolderScreenEvent.File(action.file))
            is FolderContentsAction.FileInfo -> fetchFileInfo(action.file)
            FolderContentsAction.Refresh -> sendEvent(FolderScreenEvent.Refresh)
        }
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
                        PagingException.InvalidAuth -> snackbarHostState.showSnackbar("認証エラー")

                        PagingException.InvalidServer -> snackbarHostState.showSnackbar("サーバーエラー")

                        PagingException.NoNetwork -> snackbarHostState.showSnackbar("ネットワークエラー")

                        PagingException.NotFound -> snackbarHostState.showSnackbar("見つかりませんでした")
                    }
                }
            }
        }
    }

    override var fileInfoJob: Job? = null

    init {
        navigator.currentDestination?.content?.let {
            fetchFileInfo(it.file)
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
                    sendEvent(FolderScreenEvent.Refresh)
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
