package com.sorrowblue.comicviewer.folder

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.paging.compose.LazyPagingItems
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import com.sorrowblue.comicviewer.folder.section.FolderAppBar
import com.sorrowblue.comicviewer.folder.section.FolderAppBarUiState
import com.sorrowblue.comicviewer.folder.section.FolderList
import com.sorrowblue.comicviewer.folder.section.FolderListUiState
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.AdaptiveNavigationSuiteScaffold
import com.sorrowblue.comicviewer.framework.ui.AdaptiveNavigationSuiteScaffoldState
import com.sorrowblue.comicviewer.framework.ui.canonical.isNavigationRail
import com.sorrowblue.comicviewer.framework.ui.layout.plus

internal data class FolderScreenUiState(
    val folderAppBarUiState: FolderAppBarUiState = FolderAppBarUiState(),
    val folderListUiState: FolderListUiState = FolderListUiState(),
    val sortType: SortType = SortType.Name(true),
    val folderScopeOnly: Boolean = false,
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
    onSortClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onRefresh: () -> Unit,
) {
    AdaptiveNavigationSuiteScaffold {
        Scaffold(
            topBar = {
                FolderAppBar(
                    uiState = uiState.folderAppBarUiState,
                    onBackClick = onBackClick,
                    onSearchClick = onSearchClick,
                    onSortClick = onSortClick,
                    onSettingsClick = onSettingsClick,
                )
            },
        ) { contentPadding ->
            val additionalPaddings = if (navigationSuiteType.isNavigationRail) {
                PaddingValues(
                    end = ComicTheme.dimension.margin,
                    bottom = ComicTheme.dimension.margin,
                )
            } else {
                PaddingValues(
                    start = ComicTheme.dimension.margin,
                    end = ComicTheme.dimension.margin,
                    bottom = ComicTheme.dimension.margin,
                )
            }
            FolderList(
                uiState = uiState.folderListUiState,
                lazyPagingItems = lazyPagingItems,
                lazyGridState = lazyGridState,
                contentPadding = contentPadding + additionalPaddings,
                onRefresh = onRefresh,
                onFileClick = onFileClick,
                onFileInfoClick = onFileInfoClick,
            )
        }
    }
}
