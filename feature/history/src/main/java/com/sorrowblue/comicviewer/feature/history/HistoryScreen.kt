package com.sorrowblue.comicviewer.feature.history

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.feature.history.section.EmptyContent
import com.sorrowblue.comicviewer.feature.history.section.HistoryAppBar
import com.sorrowblue.comicviewer.file.component.FileContent
import com.sorrowblue.comicviewer.file.component.FileContentUiState
import com.sorrowblue.comicviewer.file.component.FileInfoSheet
import com.sorrowblue.comicviewer.file.component.FileInfoSheetUiState
import com.sorrowblue.comicviewer.framework.compose.isEmptyData

@Composable
internal fun HistoryRoute(
    onFileClick: (File, Int) -> Unit,
    onAddFavoriteClick: (File) -> Unit,
    onOpenFolderClick: (File) -> Unit,
    onSettingsClick: () -> Unit,
    contentPadding: PaddingValues,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val lazyPagingItems = viewModel.pagingDataFlow.collectAsLazyPagingItems()
    val uiState: ReadLaterScreenUiState by viewModel.uiState.collectAsState()
    val lazyGridState = rememberLazyGridState()
    HistoryScreen(
        uiState = uiState,
        lazyPagingItems = lazyPagingItems,
        onFileClick = { onFileClick(it, lazyGridState.firstVisibleItemIndex) },
        onFileLongClick = viewModel::onFileLongClick,
        onAddReadLaterClick = viewModel::addsReadLater,
        onFileInfoDismissRequest = viewModel::onFileInfoDismissRequest,
        onAddFavoriteClick = onAddFavoriteClick,
        onOpenFolderClick = onOpenFolderClick,
        onFileListTypeClick = viewModel::toggleDisplay,
        onGridSizeClick = viewModel::onGridSizeChange,
        onSettingsClick = onSettingsClick,
        contentPadding = contentPadding,
        lazyGridState = lazyGridState
    )
}

data class ReadLaterScreenUiState(
    val fileInfoSheetUiState: FileInfoSheetUiState,
    val fileContentUiState: FileContentUiState
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HistoryScreen(
    uiState: ReadLaterScreenUiState,
    lazyPagingItems: LazyPagingItems<File>,
    onFileClick: (File) -> Unit = {},
    onFileLongClick: (File) -> Unit = {},
    onAddReadLaterClick: (File) -> Unit = {},
    onFileInfoDismissRequest: () -> Unit = {},
    onAddFavoriteClick: (File) -> Unit = {},
    onOpenFolderClick: (File) -> Unit = {},
    onFileListTypeClick: () -> Unit = {},
    onGridSizeClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    contentPadding: PaddingValues,
    lazyGridState: LazyGridState = rememberLazyGridState(),
) {
    val appBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val localLayoutDirection = LocalLayoutDirection.current
    Scaffold(
        topBar = {
            HistoryAppBar(
                fileContentLayout = uiState.fileContentUiState.layout,
                topAppBarScrollBehavior = appBarScrollBehavior,
                onFileContentLayoutClick = onFileListTypeClick,
                onGridSizeClick = onGridSizeClick,
                onSettingsClick = onSettingsClick,
            )
        },
        contentWindowInsets = WindowInsets(
            left = contentPadding.calculateLeftPadding(localLayoutDirection),
            top = contentPadding.calculateTopPadding(),
            right = contentPadding.calculateRightPadding(localLayoutDirection),
            bottom = contentPadding.calculateBottomPadding()
        ),
        modifier = Modifier.nestedScroll(appBarScrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        if (lazyPagingItems.isEmptyData) {
            EmptyContent(Modifier.padding(innerPadding))
        } else {
            FileContent(
                uiState = uiState.fileContentUiState,
                lazyPagingItems = lazyPagingItems,
                contentPadding = innerPadding,
                onClickItem = onFileClick,
                onLongClickItem = onFileLongClick,
                state = lazyGridState
            )
        }
    }

    FileInfoSheet(
        uiState = uiState.fileInfoSheetUiState,
        onDismissRequest = onFileInfoDismissRequest,
        onAddReadLaterClick = onAddReadLaterClick,
        onAddFavoriteClick = onAddFavoriteClick,
        onOpenFolderClick = onOpenFolderClick
    )
}
