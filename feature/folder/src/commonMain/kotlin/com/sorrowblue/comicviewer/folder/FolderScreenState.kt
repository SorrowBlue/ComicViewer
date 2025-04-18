package com.sorrowblue.comicviewer.folder

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.paging.LoadState
import com.sorrowblue.cmpdestinations.result.NavResult
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
import com.sorrowblue.comicviewer.folder.section.FolderTopAppBarAction
import com.sorrowblue.comicviewer.framework.ui.EventFlow
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.rememberCanonicalScaffoldNavigator
import com.sorrowblue.comicviewer.framework.ui.paging.LazyPagingItems
import com.sorrowblue.comicviewer.framework.ui.paging.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.framework.ui.paging.indexOf
import com.sorrowblue.comicviewer.framework.ui.paging.isLoading
import kotlin.math.min
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import logcat.asLog
import logcat.logcat
import org.koin.compose.viewmodel.koinViewModel

internal sealed interface FolderScreenEvent {
    data class Collection(val bookshelfId: BookshelfId, val path: String) : FolderScreenEvent

    data class File(val file: com.sorrowblue.comicviewer.domain.model.file.File) : FolderScreenEvent

    data class Search(val bookshelfId: BookshelfId, val path: String) : FolderScreenEvent

    data class Sort(val sortType: SortType) : FolderScreenEvent

    data object Back : FolderScreenEvent
    data object Settings : FolderScreenEvent
    data object Restore : FolderScreenEvent
}

@Stable
internal interface FolderScreenState {

    val navigator: ThreePaneScaffoldNavigator<File.Key>
    val events: EventFlow<FolderScreenEvent>
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
}

@Composable
internal fun rememberFolderScreenState(
    args: Folder,
    navigator: ThreePaneScaffoldNavigator<File.Key> = rememberCanonicalScaffoldNavigator(),
    pullRefreshState: PullToRefreshState = rememberPullToRefreshState(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    viewModel: FolderViewModel = koinViewModel(),
    scope: CoroutineScope = rememberCoroutineScope(),
    lazyGridState: LazyGridState = rememberLazyGridState(),
): FolderScreenState {
    val lazyPagingItems =
        viewModel.pagingDataFlow(args.bookshelfId, args.path).collectAsLazyPagingItems()
    return rememberSaveable(
        saver = Saver(
            save = {
                it.isRestored
            },
            restore = {
                FolderScreenStateImpl(
                    lazyPagingItems = lazyPagingItems,
                    navigator = navigator,
                    lazyGridState = lazyGridState,
                    snackbarHostState = snackbarHostState,
                    pullRefreshState = pullRefreshState,
                    scope = scope,
                    args = args,
                    folderDisplaySettingsUseCase = viewModel.displaySettingsUseCase,
                    getFileUseCase = viewModel.getFileUseCase
                ).apply {
                    isRestored = it
                }
            }
        )
    ) {
        FolderScreenStateImpl(
            lazyPagingItems = lazyPagingItems,
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

private class FolderScreenStateImpl(
    override val lazyPagingItems: LazyPagingItems<File>,
    override val navigator: ThreePaneScaffoldNavigator<File.Key>,
    override val lazyGridState: LazyGridState,
    override val snackbarHostState: SnackbarHostState,
    override val pullRefreshState: PullToRefreshState,
    private val scope: CoroutineScope,
    private val args: Folder,
    private val folderDisplaySettingsUseCase: ManageFolderDisplaySettingsUseCase,
    getFileUseCase: GetFileUseCase,
) : FolderScreenState {

    var isRestored by mutableStateOf(false)

    override val events = EventFlow<FolderScreenEvent>()

    override var uiState by mutableStateOf(FolderScreenUiState(bookshelfId = args.bookshelfId))
        private set

    init {
        uiState = uiState.copy(emphasisPath = args.restorePath.orEmpty())
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
            is FileInfoSheetNavigator.Collection -> navigator.currentDestination!!.contentKey!!.let {
                events.tryEmit(FolderScreenEvent.Collection(it.bookshelfId, it.path))
            }

            is FileInfoSheetNavigator.OpenFolder -> Unit
        }
    }

    override fun onFolderContentsAction(action: FolderContentsAction) {
        when (action) {
            is FolderContentsAction.File -> events.tryEmit(FolderScreenEvent.File(action.file))
            is FolderContentsAction.FileInfo -> scope.launch {
                navigator.navigateTo(SupportingPaneScaffoldRole.Extra, action.file.key())
            }

            FolderContentsAction.Refresh -> refreshItems()
        }
    }

    private fun refreshItems() {
        lazyPagingItems.refresh()
    }

    override fun onFolderTopAppBarAction(action: FolderTopAppBarAction) {
        when (action) {
            FolderTopAppBarAction.Back -> events.tryEmit(FolderScreenEvent.Back)

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
                events.tryEmit(FolderScreenEvent.Search(args.bookshelfId, args.path))

            FolderTopAppBarAction.Settings -> events.tryEmit(FolderScreenEvent.Settings)
            FolderTopAppBarAction.Sort -> events.tryEmit(FolderScreenEvent.Sort(uiState.sortType))
        }
    }

    override fun onLoadStateChange(lazyPagingItems: LazyPagingItems<File>) {
        logcat { "isRestored: $isRestored, restorePath: ${args.restorePath}, itemCount: ${lazyPagingItems.itemCount}" }
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
                events.tryEmit(FolderScreenEvent.Restore)
            } else if (!lazyPagingItems.loadState.isLoading) {
                events.tryEmit(FolderScreenEvent.Restore)
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
