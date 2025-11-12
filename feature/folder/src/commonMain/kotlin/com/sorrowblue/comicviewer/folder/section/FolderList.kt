package com.sorrowblue.comicviewer.folder.section

import androidx.compose.foundation.ScrollbarBox
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.paging.compose.LazyPagingItems
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.file.component.FileLazyVerticalGrid
import com.sorrowblue.comicviewer.file.component.FileLazyVerticalGridUiState
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.undraw.UndrawResumeFolder
import com.sorrowblue.comicviewer.framework.ui.EmptyContent
import com.sorrowblue.comicviewer.framework.ui.layout.asWindowInsets
import com.sorrowblue.comicviewer.framework.ui.layout.union
import com.sorrowblue.comicviewer.framework.ui.material3.LinearPullRefreshContainer
import com.sorrowblue.comicviewer.framework.ui.paging.isEmptyData
import com.sorrowblue.comicviewer.framework.ui.paging.isLoading
import comicviewer.feature.folder.generated.resources.Res
import comicviewer.feature.folder.generated.resources.folder_text_nothing_in_folder
import org.jetbrains.compose.resources.stringResource

internal data class FolderListUiState(
    val title: String = "",
    val fileLazyVerticalGridUiState: FileLazyVerticalGridUiState = FileLazyVerticalGridUiState(),
    val emphasisPath: String = "",
)

@Composable
internal fun FolderList(
    uiState: FolderListUiState,
    lazyPagingItems: LazyPagingItems<File>,
    lazyGridState: LazyGridState,
    contentPadding: PaddingValues,
    onRefresh: () -> Unit,
    onFileClick: (File) -> Unit,
    onFileInfoClick: (File) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pullRefreshState = rememberPullToRefreshState()
    val isRefreshing by remember(
        lazyPagingItems.loadState,
    ) { derivedStateOf { lazyPagingItems.loadState.isLoading } }
    LinearPullRefreshContainer(
        pullRefreshState = pullRefreshState,
        contentPadding = contentPadding,
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier,
    ) {
        if (lazyPagingItems.isEmptyData) {
            EmptyContent(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(contentPadding),
                imageVector = ComicIcons.UndrawResumeFolder,
                text = stringResource(Res.string.folder_text_nothing_in_folder, uiState.title),
            )
        } else {
            ScrollbarBox(
                state = lazyGridState,
                scrollbarWindowInsets = scrollbarWindowInsets(contentPadding),
            ) {
                FileLazyVerticalGrid(
                    modifier = Modifier.fillMaxSize(),
                    uiState = uiState.fileLazyVerticalGridUiState,
                    lazyPagingItems = lazyPagingItems,
                    contentPadding = contentPadding,
                    onItemClick = onFileClick,
                    onItemInfoClick = onFileInfoClick,
                    state = lazyGridState,
                    emphasisPath = uiState.emphasisPath,
                )
            }
        }
    }
}

@Composable
private fun scrollbarWindowInsets(contentPadding: PaddingValues): WindowInsets =
    WindowInsets.safeDrawing.only(
        WindowInsetsSides.Companion.Vertical + WindowInsetsSides.Companion.End,
    ) union contentPadding
        .asWindowInsets()
        .only(WindowInsetsSides.Companion.Vertical)
