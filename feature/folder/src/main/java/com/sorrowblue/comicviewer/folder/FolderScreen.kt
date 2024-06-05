package com.sorrowblue.comicviewer.folder

import android.os.Parcelable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.domain.model.PagingException
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.fakeBookFile
import com.sorrowblue.comicviewer.domain.model.settings.FolderDisplaySettings
import com.sorrowblue.comicviewer.feature.folder.R
import com.sorrowblue.comicviewer.file.FileInfoSheet
import com.sorrowblue.comicviewer.file.FileInfoUiState
import com.sorrowblue.comicviewer.file.component.FileLazyVerticalGrid
import com.sorrowblue.comicviewer.file.component.rememberFileContentType
import com.sorrowblue.comicviewer.file.rememberThreePaneScaffoldNavigatorContent
import com.sorrowblue.comicviewer.folder.section.FolderAppBar
import com.sorrowblue.comicviewer.folder.section.FolderAppBarUiState
import com.sorrowblue.comicviewer.folder.section.SortSheet
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.undraw.UndrawResumeFolder
import com.sorrowblue.comicviewer.framework.ui.CanonicalScaffold
import com.sorrowblue.comicviewer.framework.ui.EmptyContent
import com.sorrowblue.comicviewer.framework.ui.NavTabHandler
import com.sorrowblue.comicviewer.framework.ui.calculatePaddingMargins
import com.sorrowblue.comicviewer.framework.ui.material3.LinearPullRefreshContainer
import com.sorrowblue.comicviewer.framework.ui.material3.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.material3.adaptive.navigation.BackHandlerForNavigator
import com.sorrowblue.comicviewer.framework.ui.paging.indexOf
import com.sorrowblue.comicviewer.framework.ui.paging.isEmptyData
import com.sorrowblue.comicviewer.framework.ui.paging.isLoadedData
import com.sorrowblue.comicviewer.framework.ui.paging.isLoading
import com.sorrowblue.comicviewer.framework.ui.paging.isNotLoading
import com.sorrowblue.comicviewer.framework.ui.preview.flowData
import com.sorrowblue.comicviewer.framework.ui.preview.flowEmptyData
import kotlin.math.min
import kotlinx.coroutines.flow.Flow
import kotlinx.parcelize.Parcelize
import logcat.asLog
import logcat.logcat

@Parcelize
internal data class FolderScreenUiState(
    val folderAppBarUiState: FolderAppBarUiState = FolderAppBarUiState(),
    val display: FolderDisplaySettings.Display = FolderDisplaySettings.Display.List,
    val columnSize: FolderDisplaySettings.ColumnSize = FolderDisplaySettings.ColumnSize.Medium,
    val isThumbnailEnabled: Boolean = true,
) : Parcelable

@Composable
fun FolderScreen(
    args: FolderArgs,
    navigator: FolderScreenNavigator,
    onRestoreComplete: () -> Unit = {},
) {
    FolderScreen(
        args = args,
        onBackClick = navigator::navigateUp,
        onSearchClick = navigator::onSearchClick,
        onSettingsClick = navigator::onSettingsClick,
        onFileClick = navigator::onFileClick,
        onFavoriteClick = navigator::onFavoriteClick,
        onRestoreComplete = onRestoreComplete
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun FolderScreen(
    args: FolderArgs,
    onBackClick: () -> Unit,
    onSearchClick: (BookshelfId, String) -> Unit,
    onSettingsClick: () -> Unit,
    onFileClick: (File) -> Unit,
    onFavoriteClick: (File) -> Unit,
    onRestoreComplete: () -> Unit = {},
) {
    FolderScreen(
        onBackClick = onBackClick,
        onSearchClick = onSearchClick,
        onSettingsClick = onSettingsClick,
        onFileClick = onFileClick,
        onFavoriteClick = onFavoriteClick,
        state = rememberFolderScreenState(args = args),
        onRestoreComplete = onRestoreComplete
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
private fun FolderScreen(
    onBackClick: () -> Unit,
    onSearchClick: (BookshelfId, String) -> Unit,
    onSettingsClick: () -> Unit,
    onFileClick: (File) -> Unit,
    onFavoriteClick: (File) -> Unit,
    state: FolderScreenState,
    onRestoreComplete: () -> Unit = {},
) {
    val lazyPagingItems = state.pagingDataFlow.collectAsLazyPagingItems()
    val uiState = state.uiState
    var isRefreshing by remember { mutableStateOf(false) }
    FolderScreen(
        uiState = uiState,
        navigator = state.navigator,
        lazyPagingItems = lazyPagingItems,
        onSearchClick = { onSearchClick(state.bookshelfId, state.path) },
        onFileClick = onFileClick,
        onFileInfoClick = state::onFileInfoClick,
        lazyGridState = state.lazyGridState,
        pullRefreshState = state.pullRefreshState,
        snackbarHostState = state.snackbarHostState,
        onFileListChange = state::toggleFileListType,
        onHideFileClick = state::onHideFileClick,
        onSettingsClick = onSettingsClick,
        onExtraPaneCloseClick = state::onExtraPaneCloseClick,
        onGridSizeChange = state::onGridSizeChange,
        onBackClick = onBackClick,
        onSortClick = state::openSortSheet,
        onReadLaterClick = state::onReadLaterClick,
        onFavoriteClick = onFavoriteClick,
        onRefresh = lazyPagingItems::refresh,
        isRefreshing = isRefreshing
    )

    SortSheet(
        uiState = state.sortSheetUiState,
        onSortItemClick = state::onSortItemClick,
        onSortOrderClick = state::onSortOrderClick,
        onDismissRequest = state::onSortSheetDismissRequest
    )

    LaunchedEffect(lazyPagingItems.loadState) {
        if (lazyPagingItems.loadState.isNotLoading) {
            isRefreshing = false
        }
        if (lazyPagingItems.loadState.isLoading) {
            isRefreshing = true
        }
    }
    val onRestoreComplete1 by rememberUpdatedState(newValue = onRestoreComplete)
    LaunchedEffect(lazyPagingItems.loadState) {
        logcat { "state.restorePath=${state.restorePath}" }
        if (0 < lazyPagingItems.itemCount && state.restorePath != null) {
            val index = lazyPagingItems.indexOf { it?.path == state.restorePath }
            if (0 <= index) {
                state.restorePath = null
                runCatching {
                    logcat { "scroll" }
                    state.lazyGridState.scrollToItem(min(index, lazyPagingItems.itemCount - 1))
                }.onFailure {
                    logcat { it.asLog() }
                }
                logcat { "onRestoreComplete1" }
                onRestoreComplete1()
            } else if (!lazyPagingItems.loadState.isLoading) {
                onRestoreComplete1()
            }
        }
        if (lazyPagingItems.isLoadedData && state.isScrollableTop) {
            state.lazyGridState.scrollToItem(0)
            state.isScrollableTop = false
        }

        if (lazyPagingItems.loadState.refresh is LoadState.Error) {
            ((lazyPagingItems.loadState.refresh as LoadState.Error).error as? PagingException)?.let {
                when (it) {
                    PagingException.InvalidAuth -> {
                        state.snackbarHostState.showSnackbar("認証エラー")
                    }

                    PagingException.InvalidServer -> {
                        state.snackbarHostState.showSnackbar("サーバーエラー")
                    }

                    PagingException.NoNetwork -> {
                        state.snackbarHostState.showSnackbar("ネットワークエラー")
                    }

                    PagingException.NotFound -> {
                        state.snackbarHostState.showSnackbar("見つかりませんでした")
                    }
                }
            }
        }
    }

    val sort by state.sort.collectAsState()
    LaunchedEffect(sort) {
        if (!state.isSkipFirstRefresh) {
            lazyPagingItems.refresh()
        }
    }

    val showHidden by state.showHidden.collectAsState()
    LaunchedEffect(showHidden) {
        if (!state.isSkipFirstRefresh) {
            lazyPagingItems.refresh()
        }
    }

    BackHandlerForNavigator(navigator = state.navigator)

    NavTabHandler(onClick = state::onNavClick)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
private fun FolderScreen(
    uiState: FolderScreenUiState,
    lazyPagingItems: LazyPagingItems<File>,
    onFileClick: (File) -> Unit,
    onFileInfoClick: (File) -> Unit,
    onBackClick: () -> Unit,
    onSearchClick: () -> Unit,
    onSortClick: () -> Unit,
    onFileListChange: () -> Unit,
    onGridSizeChange: () -> Unit,
    onHideFileClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onReadLaterClick: () -> Unit,
    onFavoriteClick: (File) -> Unit,
    onExtraPaneCloseClick: () -> Unit,
    onRefresh: () -> Unit,
    isRefreshing: Boolean = false,
    navigator: ThreePaneScaffoldNavigator<FileInfoUiState> = rememberSupportingPaneScaffoldNavigator<FileInfoUiState>(),
    lazyGridState: LazyGridState = rememberLazyGridState(),
    pullRefreshState: PullToRefreshState = rememberPullToRefreshState(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
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
                onHideFileClick = onHideFileClick,
                onSettingsClick = onSettingsClick,
                scrollBehavior = scrollBehavior,
            )
        },
        extraPane = { innerPadding ->
            val fileInfo by rememberThreePaneScaffoldNavigatorContent(navigator)
            fileInfo?.let {
                FileInfoSheet(
                    fileInfoUiState = it,
                    scaffoldDirective = navigator.scaffoldDirective,
                    onCloseClick = onExtraPaneCloseClick,
                    onReadLaterClick = onReadLaterClick,
                    onFavoriteClick = { onFavoriteClick(it.file) },
                    onOpenFolderClick = null,
                    contentPadding = innerPadding
                )
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { contentPadding ->
        LinearPullRefreshContainer(
            pullRefreshState = pullRefreshState,
            contentPadding = contentPadding,
            isRefreshing = isRefreshing,
            onRefresh = onRefresh
        ) {
            if (lazyPagingItems.isEmptyData) {
                EmptyContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding),
                    imageVector = ComicIcons.UndrawResumeFolder,
                    text = stringResource(
                        id = R.string.folder_text_nothing_in_folder,
                        uiState.folderAppBarUiState.title
                    )
                )
            } else {
                val contentType by rememberFileContentType(uiState.display, uiState.columnSize)
                val (paddings, margins) = calculatePaddingMargins(contentPadding)
                FileLazyVerticalGrid(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(margins),
                    isThumbnailEnabled = uiState.isThumbnailEnabled,
                    contentType = contentType,
                    lazyPagingItems = lazyPagingItems,
                    contentPadding = paddings,
                    onItemClick = onFileClick,
                    onItemInfoClick = onFileInfoClick,
                    state = lazyGridState
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun PreviewFolderScreen() {
    val files = List<File>(30) {
        fakeBookFile(BookshelfId(it))
    }
    val pagingDataFlow = PagingData.flowData(files)
    val lazyPagingItems = pagingDataFlow.collectAsLazyPagingItems()
    PreviewTheme {
        FolderScreen(
            uiState = FolderScreenUiState(),
            lazyPagingItems = lazyPagingItems,
            onSearchClick = {},
            onFileClick = {},
            onFileInfoClick = {},
            onFileListChange = {},
            onHideFileClick = {},
            onSettingsClick = {},
            onExtraPaneCloseClick = {},
            onGridSizeChange = {},
            onBackClick = {},
            onSortClick = {},
            onReadLaterClick = {},
            onFavoriteClick = {},
            onRefresh = {},
        )
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun PreviewFolderScreenEmpty() {
    val pagingDataFlow: Flow<PagingData<File>> = PagingData.flowEmptyData()
    val lazyPagingItems = pagingDataFlow.collectAsLazyPagingItems()
    PreviewTheme {
        FolderScreen(
            uiState = FolderScreenUiState(),
            lazyPagingItems = lazyPagingItems,
            onSearchClick = {},
            onFileClick = {},
            onFileInfoClick = {},
            onFileListChange = {},
            onHideFileClick = {},
            onSettingsClick = {},
            onExtraPaneCloseClick = {},
            onGridSizeChange = {},
            onBackClick = {},
            onSortClick = {},
            onReadLaterClick = {},
            onFavoriteClick = {},
            onRefresh = {},
        )
    }
}
