package com.sorrowblue.comicviewer.folder

import android.os.Parcelable
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.PaneAdaptedValue
import androidx.compose.material3.adaptive.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.rememberSupportingPaneScaffoldNavigator
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import androidx.navigation.NavBackStackEntry
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.onSuccess
import com.sorrowblue.comicviewer.domain.model.settings.FolderDisplaySettings
import com.sorrowblue.comicviewer.domain.model.settings.SortType
import com.sorrowblue.comicviewer.domain.usecase.file.GetFileUseCase
import com.sorrowblue.comicviewer.feature.folder.R
import com.sorrowblue.comicviewer.file.FileInfoSheet
import com.sorrowblue.comicviewer.file.component.FileContent
import com.sorrowblue.comicviewer.file.component.FileContentType
import com.sorrowblue.comicviewer.file.component.toFileContentLayout
import com.sorrowblue.comicviewer.folder.navigation.FolderArgs
import com.sorrowblue.comicviewer.folder.section.FolderAppBar
import com.sorrowblue.comicviewer.folder.section.FolderAppBarUiState
import com.sorrowblue.comicviewer.folder.section.SortItem
import com.sorrowblue.comicviewer.folder.section.SortOrder
import com.sorrowblue.comicviewer.folder.section.SortSheet
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.undraw.UndrawResumeFolder
import com.sorrowblue.comicviewer.framework.designsystem.theme.LocalDimension
import com.sorrowblue.comicviewer.framework.ui.CanonicalScaffold
import com.sorrowblue.comicviewer.framework.ui.EmptyContent
import com.sorrowblue.comicviewer.framework.ui.add
import com.sorrowblue.comicviewer.framework.ui.calculateStandardPaneScaffoldDirective
import com.sorrowblue.comicviewer.framework.ui.paging.indexOf
import com.sorrowblue.comicviewer.framework.ui.paging.isEmptyData
import com.sorrowblue.comicviewer.framework.ui.paging.isLoadedData
import kotlin.math.min
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import logcat.asLog
import logcat.logcat

@Parcelize
internal data class FolderScreenUiState(
    val folderAppBarUiState: FolderAppBarUiState = FolderAppBarUiState(),
    val openSortSheet: Boolean = false,
    val sortItem: SortItem = SortItem.Name,
    val sortOrder: SortOrder = SortOrder.Asc,
    val fileContentType: FileContentType = FileContentType.Grid(),
) : Parcelable

@OptIn(ExperimentalMaterial3AdaptiveApi::class, SavedStateHandleSaveableApi::class)
@Stable
internal class FolderScreenState(
    savedStateHandle: SavedStateHandle,
    val args: FolderArgs,
    private val viewModel: FolderViewModel,
    scope: CoroutineScope,
    val navigator: ThreePaneScaffoldNavigator,
) {

    val pagingDataFlow = viewModel.pagingDataFlow
    var file: File? by savedStateHandle.saveable(
        "file",
        stateSaver = Saver(save = { it }, restore = { it })
    ) {
        mutableStateOf(null)
    }
    var restorePath by savedStateHandle.saveable(
        "restorePath",
        stateSaver = Saver(save = { it }, restore = { it })
    ) {
        mutableStateOf(args.restorePath)
    }
    var isSkipFirstRefresh by savedStateHandle.saveable { mutableStateOf(true) }
    var isScrollableTop by savedStateHandle.saveable { mutableStateOf(false) }

    var uiState: FolderScreenUiState by savedStateHandle.saveable {
        mutableStateOf(
            FolderScreenUiState()
        )
    }
        private set

    init {
        viewModel.displaySettings.map(FolderDisplaySettings::toFileContentLayout)
            .distinctUntilChanged().onEach {
                uiState = uiState.copy(
                    folderAppBarUiState = uiState.folderAppBarUiState.copy(fileContentType = it),
                    fileContentType = it
                )
            }.launchIn(scope)
        viewModel.displaySettings.map { it.sortType }
            .distinctUntilChanged().onEach {
                val sortItem = when (it) {
                    is SortType.DATE -> SortItem.Date
                    is SortType.NAME -> SortItem.Name
                    is SortType.SIZE -> SortItem.Size
                }
                val sortOrder = if (it.isAsc) SortOrder.Asc else SortOrder.Desc
                uiState = uiState.copy(
                    sortItem = sortItem,
                    sortOrder = sortOrder,
                    folderAppBarUiState = uiState.folderAppBarUiState.copy(
                        sortItem = sortItem,
                        sortOrder = sortOrder
                    )
                )
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

    val bookshelfId get() = args.bookshelfId
    val path get() = args.path

    val sort = viewModel.sort

    fun toggleFileListType() {
        viewModel.updateDisplay(
            when (uiState.fileContentType) {
                is FileContentType.Grid -> FolderDisplaySettings.Display.LIST
                FileContentType.List -> FolderDisplaySettings.Display.GRID
            }
        )
    }

    fun onGridSizeChange() {
        if (uiState.fileContentType is FileContentType.Grid) {
            viewModel.updateGridSize()
        }
    }

    fun openSort() {
        uiState = uiState.copy(openSortSheet = true)
    }

    fun onReadLaterClick(file: File) {
        viewModel.addToReadLater(file = file)
    }

    fun onSortSheetDismissRequest() {
        uiState = uiState.copy(openSortSheet = false)
    }

    fun onExtraPaneCloseClick() {
        navigator.navigateBack()
    }

    fun onFileInfoClick(file: File) {
        this.file = file
        navigator.navigateTo(SupportingPaneScaffoldRole.Extra)
    }

    fun onSortItemClick(sortItem: SortItem) {
        isScrollableTop = true
        isSkipFirstRefresh = false
        onSortSheetDismissRequest()
        viewModel.updateDisplaySettings {
            it.copy(
                sortType = when (sortItem) {
                    SortItem.Date -> SortType.DATE(uiState.sortOrder == SortOrder.Asc)
                    SortItem.Name -> SortType.NAME(uiState.sortOrder == SortOrder.Asc)
                    SortItem.Size -> SortType.SIZE(uiState.sortOrder == SortOrder.Asc)
                }
            )
        }
    }

    fun onSortOrderClick(sortOrder: SortOrder) {
        isScrollableTop = true
        isSkipFirstRefresh = false
        onSortSheetDismissRequest()
        viewModel.updateDisplaySettings {
            it.copy(
                sortType = when (sortOrder) {
                    SortOrder.Asc -> it.sortType.copy2(isAsc = true)
                    SortOrder.Desc -> it.sortType.copy2(isAsc = false)
                }
            )
        }
    }
}

context(NavBackStackEntry)
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
internal fun rememberFolderScreenState(
    args: FolderArgs,
    viewModel: FolderViewModel = hiltViewModel(),
    navigator: ThreePaneScaffoldNavigator = rememberSupportingPaneScaffoldNavigator(
        calculateStandardPaneScaffoldDirective(currentWindowAdaptiveInfo())
    ),
    scope: CoroutineScope = rememberCoroutineScope(),
): FolderScreenState {
    return remember {
        FolderScreenState(
            args = args,
            viewModel = viewModel,
            scope = scope,
            navigator = navigator,
            savedStateHandle = savedStateHandle
        )
    }
}

context(NavBackStackEntry)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
internal fun FolderRoute(
    contentPadding: PaddingValues,
    onSearchClick: (BookshelfId, String) -> Unit,
    onSettingsClick: () -> Unit,
    onBackClick: () -> Unit,
    onRestoreComplete: () -> Unit,
    onFileClick: (File) -> Unit,
    onFavoriteClick: (File) -> Unit,
    onOpenFolderClick: (File) -> Unit,
    state: FolderScreenState = rememberFolderScreenState(args = FolderArgs(arguments!!)),
) {
    val lazyPagingItems = state.pagingDataFlow.collectAsLazyPagingItems()
    val uiState = state.uiState
    val lazyGridState = rememberLazyGridState()
    val pullRefreshState = rememberPullToRefreshState()
    if (pullRefreshState.isRefreshing) {
        LaunchedEffect(true) {
            // fetch something
            lazyPagingItems.refresh()
        }
    }
    FolderScreen(
        contentPadding = contentPadding,
        uiState = uiState,
        file = state.file,
        navigator = state.navigator,
        lazyPagingItems = lazyPagingItems,
        onSearchClick = { onSearchClick(state.bookshelfId, state.path) },
        onFileClick = onFileClick,
        onFileInfoClick = state::onFileInfoClick,
        lazyGridState = lazyGridState,
        pullRefreshState = pullRefreshState,
        onFileListChange = state::toggleFileListType,
        onSettingsClick = onSettingsClick,
        onGridSizeChange = state::onGridSizeChange,
        onExtraPaneCloseClick = state::onExtraPaneCloseClick,
        onBackClick = onBackClick,
        onSortClick = state::openSort,
        onSortItemClick = state::onSortItemClick,
        onSortOrderClick = state::onSortOrderClick,
        onReadLaterClick = state::onReadLaterClick,
        onFavoriteClick = onFavoriteClick,
        onOpenFolderClick = onOpenFolderClick
    )

    if (uiState.openSortSheet) {
        SortSheet(
            currentSortItem = uiState.sortItem,
            currentSortOrder = uiState.sortOrder,
            onSortItemClick = state::onSortItemClick,
            onSortOrderClick = state::onSortOrderClick,
            onDismissRequest = state::onSortSheetDismissRequest
        )
    }
    LaunchedEffect(lazyPagingItems.loadState) {
        if (0 < lazyPagingItems.itemCount && state.restorePath != null) {
            val index = lazyPagingItems.indexOf { it?.path == state.restorePath }
            if (0 <= index) {
                state.restorePath = null
                runCatching {
                    lazyGridState.scrollToItem(min(index, lazyPagingItems.itemCount - 1))
                }.onFailure {
                    logcat { it.asLog() }
                }
                onRestoreComplete()
            } else if (!lazyPagingItems.loadState.isLoading) {
                onRestoreComplete()
            }
        }
        if (lazyPagingItems.isLoadedData) {
            pullRefreshState.endRefresh()
        }
        if (lazyPagingItems.isLoadedData && state.isScrollableTop) {
            state.isScrollableTop = false
            lazyGridState.scrollToItem(0)
        }
    }

    val sort by state.sort.collectAsState()
    LaunchedEffect(sort) {
        if (!state.isSkipFirstRefresh) {
            lazyPagingItems.refresh()
        }
    }

    BackHandler(enabled = state.navigator.scaffoldState.scaffoldValue.tertiary == PaneAdaptedValue.Expanded) {
        state.navigator.navigateBack()
    }
}

val CombinedLoadStates.isLoading
    get() =
        source.any { it == LoadState.Loading } || mediator?.any { it == LoadState.Loading } ?: false

fun LoadStates.all(op: (LoadState) -> Boolean): Boolean {
    return op(refresh) && op(append) && op(prepend)
}

fun LoadStates.any(op: (LoadState) -> Boolean): Boolean {
    return op(refresh) || op(append) || op(prepend)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
internal fun FolderScreen(
    contentPadding: PaddingValues,
    navigator: ThreePaneScaffoldNavigator,
    file: File?,
    uiState: FolderScreenUiState,
    lazyPagingItems: LazyPagingItems<File>,
    onSearchClick: () -> Unit,
    onFileClick: (File) -> Unit,
    onFileInfoClick: (File) -> Unit,
    lazyGridState: LazyGridState,
    pullRefreshState: PullToRefreshState,
    onFileListChange: () -> Unit,
    onSettingsClick: () -> Unit,
    onExtraPaneCloseClick: () -> Unit,
    onGridSizeChange: () -> Unit,
    onBackClick: () -> Unit,
    onSortClick: () -> Unit,
    onSortItemClick: (SortItem) -> Unit,
    onSortOrderClick: (SortOrder) -> Unit,
    onReadLaterClick: (File) -> Unit,
    onFavoriteClick: (File) -> Unit,
    onOpenFolderClick: (File) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    CanonicalScaffold(
        navigator = navigator,
        topBar = {
            FolderAppBar(
                uiState = uiState.folderAppBarUiState,
                onFileListChange = onFileListChange,
                onBackClick = onBackClick,
                onSearchClick = onSearchClick,
                onGridSizeChange = onGridSizeChange,
                onSortClick = onSortClick,
                onSortItemClick = onSortItemClick,
                onSortOrderClick = onSortOrderClick,
                onSettingsClick = onSettingsClick,
                scrollBehavior = scrollBehavior,
            )
        },
        extraPane = { innerPadding ->
            if (file != null) {
                FileInfoSheet(
                    file = file,
                    scaffoldDirective = navigator.scaffoldState.scaffoldDirective,
                    onCloseClick = onExtraPaneCloseClick,
                    onReadLaterClick = { onReadLaterClick(file) },
                    onFavoriteClick = { onFavoriteClick(file) },
                    onOpenFolderClick = { onOpenFolderClick(file) },
                    contentPadding = innerPadding
                )
            }
        },
        contentPadding = contentPadding,
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { innerPadding ->
        val scaleFraction = if (pullRefreshState.isRefreshing) {
            1f
        } else {
            LinearOutSlowInEasing.transform(pullRefreshState.progress).coerceIn(0f, 1f)
        }
        Box(Modifier.nestedScroll(pullRefreshState.nestedScrollConnection)) {
            if (lazyPagingItems.isEmptyData) {
                EmptyContent(
                    imageVector = ComicIcons.UndrawResumeFolder,
                    text = stringResource(
                        R.string.folder_text_nothing_in_folder,
                        uiState.folderAppBarUiState.title
                    ),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding)
                )
            } else {
                FileContent(
                    type = uiState.fileContentType,
                    lazyPagingItems = lazyPagingItems,
                    contentPadding = innerPadding,
                    onFileClick = onFileClick,
                    onInfoClick = onFileInfoClick,
                    state = lazyGridState
                )
            }
            PullToRefreshContainer(
                state = pullRefreshState,
                modifier = Modifier
                    .padding(innerPadding)
                    .align(Alignment.TopCenter)
                    .graphicsLayer(scaleX = scaleFraction, scaleY = scaleFraction),
            )
        }
    }
}
