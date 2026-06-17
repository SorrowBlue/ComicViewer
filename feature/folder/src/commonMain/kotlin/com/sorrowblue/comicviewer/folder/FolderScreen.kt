package com.sorrowblue.comicviewer.folder

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.github.skydoves.navgraph.annotations.NavDestination
import com.github.skydoves.navgraph.annotations.NavEdge
import com.github.skydoves.navgraph.annotations.NavPreview
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import com.sorrowblue.comicviewer.file.navigation.FileInfoNavKey
import com.sorrowblue.comicviewer.folder.navigation.FolderNavKey
import com.sorrowblue.comicviewer.folder.section.FolderAppBar
import com.sorrowblue.comicviewer.folder.section.FolderAppBarUiState
import com.sorrowblue.comicviewer.folder.section.FolderList
import com.sorrowblue.comicviewer.folder.section.FolderListUiState
import com.sorrowblue.comicviewer.framework.ui.adaptive.AdaptiveNavigationSuiteScaffold
import com.sorrowblue.comicviewer.framework.ui.adaptive.AdaptiveNavigationSuiteScaffoldState
import com.sorrowblue.comicviewer.framework.ui.adaptive.rememberAdaptiveNavigationSuiteScaffoldState
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeBookFile
import com.sorrowblue.comicviewer.framework.ui.preview.fake.flowData

@NavEdge(FileInfoNavKey::class)
@NavDestination(FolderNavKey::class)
@Composable
internal fun AdaptiveNavigationSuiteScaffoldState.FolderScreen(
    uiState: FolderScreenUiState,
    lazyPagingItems: LazyPagingItems<File>,
    lazyGridState: LazyGridState,
    onBackClick: () -> Unit,
    onSearchClick: () -> Unit,
    onFileClick: (File) -> Unit,
    onFileInfoClick: (File) -> Unit,
    onSortClick: (SortType) -> Unit,
    onFolderScopeOnlyClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onRefresh: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    AdaptiveNavigationSuiteScaffold(modifier = modifier) {
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
        val scrollBehavior2 = TopAppBarDefaults.enterAlwaysScrollBehavior()
        Scaffold(
            topBar = {
                FolderAppBar(
                    uiState = uiState.folderAppBarUiState,
                    onBackClick = onBackClick,
                    onSearchClick = onSearchClick,
                    onSortClick = onSortClick,
                    onFolderScopeOnlyClick = onFolderScopeOnlyClick,
                    onSettingsClick = onSettingsClick,
                    scrollBehavior = scrollBehavior,
                    scrollBehavior2 = scrollBehavior2,
                )
            },
            snackbarHost = {
                SnackbarHost(snackbarHostState)
            },
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .nestedScroll(scrollBehavior2.nestedScrollConnection),
        ) { contentPadding ->
            FolderList(
                uiState = uiState.folderListUiState,
                lazyPagingItems = lazyPagingItems,
                lazyGridState = lazyGridState,
                contentPadding = contentPadding,
                onRefresh = onRefresh,
                onFileClick = onFileClick,
                onFileInfoClick = onFileInfoClick,
            )
        }
    }
}

@NavPreview(FolderNavKey::class, primary = true)
@Preview
@Composable
private fun FolderScreenPreview() = PreviewTheme {
    val scaffoldState = rememberAdaptiveNavigationSuiteScaffoldState()
    scaffoldState.FolderScreen(
        uiState = FolderScreenUiState(
            folderAppBarUiState = FolderAppBarUiState(
                title = "Folder Name",
                folderScopeOnly = false,
            ),
            folderListUiState = FolderListUiState(),
        ),
        lazyPagingItems = PagingData.flowData<File> { fakeBookFile() }.collectAsLazyPagingItems(),
        lazyGridState = rememberLazyGridState(),
        onBackClick = {},
        onSearchClick = {},
        onFileClick = {},
        onFileInfoClick = {},
        onSortClick = {},
        onFolderScopeOnlyClick = {},
        onSettingsClick = {},
        onRefresh = {},
        snackbarHostState = SnackbarHostState(),
    )
}
