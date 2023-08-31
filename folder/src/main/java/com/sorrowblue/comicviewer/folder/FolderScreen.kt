package com.sorrowblue.comicviewer.folder

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.sorrowblue.comicviewer.domain.entity.settings.FolderDisplaySettings
import com.sorrowblue.comicviewer.file.FileListType2
import com.sorrowblue.comicviewer.file.component.FileContent
import com.sorrowblue.comicviewer.file.component.FileContentUiState
import com.sorrowblue.comicviewer.folder.section.FileInfoSheet
import com.sorrowblue.comicviewer.folder.section.FileInfoSheetUiState
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
    val fileInfoSheetUiState: FileInfoSheetUiState = FileInfoSheetUiState.Hide,
    val fileContentUiState: FileContentUiState,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FolderRoute(
    contentPadding: PaddingValues,
    onSearchClick: (BookshelfId, String) -> Unit,
    onAddFavoriteClick: (File) -> Unit,
    onClickFile: (File) -> Unit,
    onSettingsClick: () -> Unit,
    onBackClick: () -> Unit,
    viewModel: FolderViewModel = hiltViewModel()
) {
    val lazyPagingItems = viewModel.pagingDataFlow.collectAsLazyPagingItems()
    val uiState by viewModel.uiState.collectAsState()
    val lazyStaggeredGridState = rememberLazyStaggeredGridState()
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
        onFileInfoSheetDismissRequest = viewModel::onFileInfoSheetDismissRequest,
        onAddReadLaterClick = viewModel::onAddReadLaterClick,
        onAddFavoriteClick = {
            viewModel.onFileInfoSheetDismissRequest()
            onAddFavoriteClick(it)
        },
        onClickFile = onClickFile,
        onClickLongFile = viewModel::onClickLongFile,
        lazyStaggeredGridState = lazyStaggeredGridState,
        isRefreshing = isRefreshing,
        pullRefreshState = pullRefreshState,
        onFileListChange = viewModel::toggleFileListType,
        onSettingsClick = onSettingsClick,
        onGridSizeChange = viewModel::onGridSizeChange,
        onBackClick = onBackClick,
        onSortClick = viewModel::openSort,
    )
    LaunchedEffect(lazyPagingItems.loadState) {
        if (lazyPagingItems.isLoadedData && viewModel.isScrollableTop) {
            viewModel.isScrollableTop = false
            lazyStaggeredGridState.scrollToItem(0)
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
    onFileInfoSheetDismissRequest: () -> Unit,
    onAddReadLaterClick: (File) -> Unit,
    onAddFavoriteClick: (File) -> Unit,
    onClickFile: (File) -> Unit,
    onClickLongFile: (File) -> Unit,
    lazyStaggeredGridState: LazyStaggeredGridState,
    isRefreshing: Boolean,
    pullRefreshState: PullRefreshState = rememberPullRefreshState(false, { }),
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
        Box(Modifier.pullRefresh(pullRefreshState)) {
            if (lazyPagingItems.isEmptyData) {
                FolderEmptyContent(
                    Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            } else {
                FileContent(
                    uiState = uiState.fileContentUiState,
                    lazyPagingItems = lazyPagingItems,
                    contentPadding = innerPadding,
                    onClickItem = onClickFile,
                    onLongClickItem = onClickLongFile,
                    state = lazyStaggeredGridState
                )
            }
            PullRefreshIndicator(
                refreshing = isRefreshing,
                state = pullRefreshState,
                scale = true,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
    SortSheet(
        uiState = uiState.sortSheetUiState,
        onDismissRequest = onSortSheetDismissRequest,
        onClick = onSortChange
    )
    FileInfoSheet(
        uiState = uiState.fileInfoSheetUiState,
        onDismissRequest = onFileInfoSheetDismissRequest,
        onAddReadLaterClick = onAddReadLaterClick,
        onAddFavoriteClick = onAddFavoriteClick
    )
//    FolderScanInfoDialog(state.permissionRequestFolderScanInfoDialogUiState)
}
