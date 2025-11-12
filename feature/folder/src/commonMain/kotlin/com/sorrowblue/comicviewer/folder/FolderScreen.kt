package com.sorrowblue.comicviewer.folder

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.paging.compose.LazyPagingItems
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import com.sorrowblue.comicviewer.folder.section.FolderAppBar
import com.sorrowblue.comicviewer.folder.section.FolderAppBarUiState
import com.sorrowblue.comicviewer.folder.section.FolderList
import com.sorrowblue.comicviewer.folder.section.FolderListUiState
import com.sorrowblue.comicviewer.framework.ui.AdaptiveNavigationSuiteScaffold
import com.sorrowblue.comicviewer.framework.ui.AdaptiveNavigationSuiteScaffoldState

/**
 * フォルダ画面の引数
 *
 * @param bookshelfId 本棚ID
 * @param path フォルダのパス
 * @param restorePath 復元するパス (nullの場合は復元しない)
 */
interface Folder {
    val bookshelfId: BookshelfId
    val path: String
    val restorePath: String?
}

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
) {
    AdaptiveNavigationSuiteScaffold {
        Scaffold(
            topBar = {
                FolderAppBar(
                    uiState = uiState.folderAppBarUiState,
                    onBackClick = onBackClick,
                    onSearchClick = onSearchClick,
                    onSortClick = onSortClick,
                    onSettingsClick = {},
                )
            },
        ) { contentPadding ->
            FolderList(
                uiState = uiState.folderListUiState,
                lazyPagingItems = lazyPagingItems,
                lazyGridState = lazyGridState,
                contentPadding = contentPadding,
                onRefresh = {},
                onFileClick = onFileClick,
                onFileInfoClick = onFileInfoClick,
            )
        }
    }
}
