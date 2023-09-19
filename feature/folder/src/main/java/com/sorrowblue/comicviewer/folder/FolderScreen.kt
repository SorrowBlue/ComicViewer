package com.sorrowblue.comicviewer.folder

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.domain.entity.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.file.component.FileContent
import com.sorrowblue.comicviewer.file.component.FileContentUiState
import com.sorrowblue.comicviewer.folder.section.FolderAppBar
import com.sorrowblue.comicviewer.folder.section.FolderAppBarUiState
import com.sorrowblue.comicviewer.folder.section.FolderEmptyContent
import com.sorrowblue.comicviewer.folder.section.Sort
import com.sorrowblue.comicviewer.folder.section.SortSheet
import com.sorrowblue.comicviewer.folder.section.SortSheetUiState
import com.sorrowblue.comicviewer.framework.compose.isEmptyData
import com.sorrowblue.comicviewer.framework.compose.isLoadedData
import com.sorrowblue.comicviewer.framework.compose.pullrefresh.PullRefreshIndicator
import com.sorrowblue.comicviewer.framework.compose.pullrefresh.PullRefreshState
import com.sorrowblue.comicviewer.framework.compose.pullrefresh.pullRefresh
import com.sorrowblue.comicviewer.framework.compose.pullrefresh.rememberPullRefreshState

data class FolderScreenUiState(
    val folderAppBarUiState: FolderAppBarUiState = FolderAppBarUiState(),
    val sortSheetUiState: SortSheetUiState = SortSheetUiState.Hide,
    val fileContentUiState: FileContentUiState,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FolderRoute(
    contentPadding: PaddingValues,
    onSearchClick: (BookshelfId, String) -> Unit,
    onSettingsClick: () -> Unit,
    onBackClick: () -> Unit,
    onRestoreComplete: () -> Unit,
    onClickFile: (File, Int) -> Unit,
    onClickLongFile: (File) -> Unit,
    viewModel: FolderViewModel = hiltViewModel()
) {
    val lazyPagingItems = viewModel.pagingDataFlow.collectAsLazyPagingItems()
    val uiState by viewModel.uiState.collectAsState()
    val lazyGridState = rememberLazyGridState()
    val isRefreshing =
        remember(lazyPagingItems.loadState.refresh) { lazyPagingItems.loadState.refresh is LoadState.Loading }
    val pullRefreshState = rememberPullRefreshState(isRefreshing, lazyPagingItems::refresh)
    FolderScreen(
        contentPadding = contentPadding,
        uiState = uiState,
        lazyPagingItems = lazyPagingItems,
        onSearchClick = { onSearchClick(viewModel.bookshelfId, viewModel.path) },
        onSortSheetDismissRequest = viewModel::onSortSheetDismissRequest,
        onSortChange = viewModel::onSortChange,
        onClickFile = {
            onClickFile.invoke(it, lazyGridState.firstVisibleItemIndex)
        },
        onClickLongFile = onClickLongFile,
        lazyGridState = lazyGridState,
        isRefreshing = isRefreshing,
        pullRefreshState = pullRefreshState,
        onFileListChange = viewModel::toggleFileListType,
        onSettingsClick = onSettingsClick,
        onGridSizeChange = viewModel::onGridSizeChange,
        onBackClick = onBackClick,
        onSortClick = viewModel::openSort,
    )
    LaunchedEffect(lazyPagingItems.loadState) {
        if (0 <= viewModel.position && lazyPagingItems.loadState.refresh is LoadState.NotLoading && lazyPagingItems.itemCount > 0) {
            val position = viewModel.position
            viewModel.position = -1
            onRestoreComplete()
            lazyGridState.scrollToItem(position)
        } else if (lazyPagingItems.isLoadedData && viewModel.isScrollableTop) {
            viewModel.isScrollableTop = false
            lazyGridState.scrollToItem(0)
        }
    }

    val sort by viewModel.sort.collectAsState()
    LaunchedEffect(sort) {
        if (!viewModel.isSkipFirstRefresh) {
            lazyPagingItems.refresh()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FolderScreen(
    contentPadding: PaddingValues,
    uiState: FolderScreenUiState,
    lazyPagingItems: LazyPagingItems<File>,
    onSearchClick: () -> Unit,
    onSortSheetDismissRequest: () -> Unit,
    onSortChange: (Sort) -> Unit,
    onClickFile: (File) -> Unit,
    onClickLongFile: (File) -> Unit,
    lazyGridState: LazyGridState,
    isRefreshing: Boolean,
    pullRefreshState: PullRefreshState = rememberPullRefreshState(isRefreshing, { }),
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
    onFileListChange: () -> Unit,
    onSettingsClick: () -> Unit,
    onGridSizeChange: () -> Unit,
    onBackClick: () -> Unit,
    onSortClick: () -> Unit,
) {
    val localLayoutDirection = LocalLayoutDirection.current
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            FolderAppBar(
                uiState = uiState.folderAppBarUiState,
                onFileListChange = onFileListChange,
                onBackClick = onBackClick,
                onSearchClick = onSearchClick,
                onGridSizeChange = onGridSizeChange,
                onSortClick = onSortClick,
                onRefreshClick = { TODO() },
                onSettingsClick = onSettingsClick,
                scrollBehavior = scrollBehavior,
            )
        },
        contentWindowInsets = WindowInsets(
            left = contentPadding.calculateLeftPadding(localLayoutDirection),
            top = contentPadding.calculateTopPadding(),
            right = contentPadding.calculateRightPadding(localLayoutDirection),
            bottom = contentPadding.calculateBottomPadding()
        ),
    ) { innerPadding ->
        Box(
            Modifier
                .fillMaxSize()
                .pullRefresh(pullRefreshState)) {
            val isEmptyData by remember {
                derivedStateOf { lazyPagingItems.isEmptyData }
            }
            if (isEmptyData) {
                FolderEmptyContent(
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(innerPadding)
                )
            } else {
                FileContent(
                    uiState = uiState.fileContentUiState,
                    lazyPagingItems = lazyPagingItems,
                    contentPadding = innerPadding,
                    onClickItem = onClickFile,
                    onLongClickItem = onClickLongFile,
                    state = lazyGridState
                )
            }
            PullRefreshIndicator(
                refreshing = isRefreshing,
                state = pullRefreshState,
                scale = true,
                modifier = Modifier
                    .padding(innerPadding)
                    .align(Alignment.TopCenter)
            )
        }
    }
    SortSheet(
        uiState = uiState.sortSheetUiState,
        onDismissRequest = onSortSheetDismissRequest,
        onClick = onSortChange
    )
//    FolderScanInfoDialog(state.permissionRequestFolderScanInfoDialogUiState)
}
