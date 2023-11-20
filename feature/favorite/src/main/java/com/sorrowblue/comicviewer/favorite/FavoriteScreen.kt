package com.sorrowblue.comicviewer.favorite

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.favorite.section.FavoriteAppBar
import com.sorrowblue.comicviewer.favorite.section.FavoriteAppBarUiState
import com.sorrowblue.comicviewer.feature.favorite.R
import com.sorrowblue.comicviewer.file.component.FileContent
import com.sorrowblue.comicviewer.file.component.FileContentType
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.undraw.UndrawResumeFolder
import com.sorrowblue.comicviewer.framework.ui.EmptyContent
import com.sorrowblue.comicviewer.framework.ui.material3.TopAppBarDefaults
import com.sorrowblue.comicviewer.framework.ui.material3.pinnedScrollBehavior
import com.sorrowblue.comicviewer.framework.ui.paging.isEmptyData

@Composable
internal fun FavoriteRoute(
    onBackClick: () -> Unit,
    onEditClick: (FavoriteId) -> Unit,
    onSettingsClick: () -> Unit,
    onClickFile: (File, FavoriteId, Int) -> Unit,
    onClickLongFile: (File) -> Unit,
    viewModel: FavoriteViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val lazyPagingItems = viewModel.pagingDataFlow.collectAsLazyPagingItems()
    val lazyGridState = rememberLazyGridState()
    FavoriteScreen(
        uiState = uiState,
        lazyPagingItems = lazyPagingItems,
        onBackClick = onBackClick,
        onEditClick = { onEditClick(viewModel.favoriteId) },
        onFileListTypeChange = viewModel::toggleFileListType,
        onGridSizeChange = viewModel::toggleGridSize,
        onDeleteClick = { viewModel.delete(onBackClick) },
        onSettingsClick = onSettingsClick,
        onClickFile = { onClickFile(it, viewModel.favoriteId, lazyGridState.firstVisibleItemIndex) },
        onClickLongFile = onClickLongFile,
        lazyGridState = lazyGridState,
    )
}

internal data class FavoriteScreenUiState(
    val favoriteAppBarUiState: FavoriteAppBarUiState = FavoriteAppBarUiState(),
    val fileContentType: FileContentType = FileContentType.List,
)

@Composable
private fun FavoriteScreen(
    uiState: FavoriteScreenUiState,
    lazyPagingItems: LazyPagingItems<File>,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onFileListTypeChange: () -> Unit,
    onGridSizeChange: () -> Unit,
    onDeleteClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onClickFile: (File) -> Unit,
    onClickLongFile: (File) -> Unit,
    lazyGridState: LazyGridState,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            FavoriteAppBar(
                uiState = uiState.favoriteAppBarUiState,
                onBackClick = onBackClick,
                onEditClick = onEditClick,
                onFileListTypeChange = onFileListTypeChange,
                onGridSizeChange = onGridSizeChange,
                onDeleteClick = onDeleteClick,
                onSettingsClick = onSettingsClick,
                scrollBehavior = scrollBehavior
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { contentPadding ->
        if (lazyPagingItems.isEmptyData) {
            EmptyContent(
                imageVector = ComicIcons.UndrawResumeFolder,
                text = stringResource(id = R.string.favorite_label_no_favorites),
                contentPadding = contentPadding
            )
        } else {
            FileContent(
                type = uiState.fileContentType,
                lazyPagingItems = lazyPagingItems,
                contentPadding = contentPadding,
                onClickItem = onClickFile,
                onLongClickItem = onClickLongFile,
                state = lazyGridState
            )
        }
    }
}
