package com.sorrowblue.comicviewer.folder

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.paging.compose.LazyPagingItems
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import com.sorrowblue.comicviewer.folder.section.FolderAppBar
import com.sorrowblue.comicviewer.folder.section.FolderAppBarUiState
import com.sorrowblue.comicviewer.folder.section.FolderList
import com.sorrowblue.comicviewer.folder.section.FolderListUiState
import com.sorrowblue.comicviewer.framework.ui.adaptive.AdaptiveNavigationSuiteScaffold
import com.sorrowblue.comicviewer.framework.ui.adaptive.AdaptiveNavigationSuiteScaffoldState

internal data class FolderScreenUiState(
    val folderAppBarUiState: FolderAppBarUiState = FolderAppBarUiState(),
    val folderListUiState: FolderListUiState = FolderListUiState(),
)

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
) {
    AdaptiveNavigationSuiteScaffold {
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
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
                )
            },
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
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
