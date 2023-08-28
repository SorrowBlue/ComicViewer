package com.sorrowblue.comicviewer.feature.readlater

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.domain.entity.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.entity.file.BookFile
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.file.FileListType
import com.sorrowblue.comicviewer.folder.section.FileInfoSheet
import com.sorrowblue.comicviewer.folder.section.FileInfoSheetUiState
import com.sorrowblue.comicviewer.folder.section.FileListSheet
import com.sorrowblue.comicviewer.framework.compose.isEmptyData
import com.sorrowblue.comicviewer.feature.readlater.section.EmptyContent
import com.sorrowblue.comicviewer.feature.readlater.section.ReadLaterAppBar
import com.sorrowblue.comicviewer.framework.compose.AppMaterialTheme
import kotlinx.coroutines.flow.flowOf

@Composable
internal fun ReadLaterRoute(
    onFileClick: (File) -> Unit,
    onAddFavoriteClick: (File) -> Unit,
    onOpenFolderClick: (File) -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: ReadLaterViewModel = hiltViewModel()
) {
    val lazyPagingItems = viewModel.pagingDataFlow.collectAsLazyPagingItems()
    val uiState by viewModel.uiState.collectAsState()
    ReadLaterScreen(
        uiState = uiState,
        lazyPagingItems = lazyPagingItems,
        onFileClick = onFileClick,
        onFileLongClick = viewModel::onFileLongClick,
        onFileInfoDismissRequest = viewModel::onFileInfoDismissRequest,
        onAddReadLaterClick = viewModel::addsReadLater,
        onAddFavoriteClick = onAddFavoriteClick,
        onOpenFolderClick = onOpenFolderClick,
        onFileListTypeClick = viewModel::toggleDisplay,
        onGridSizeClick = viewModel::toggleSpanCount,
        onSettingsClick = onSettingsClick
    )
}

data class ReadLaterScreenUiState(
    val fileListType: FileListType,
    val fileInfoSheetUiState: FileInfoSheetUiState
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ReadLaterScreen(
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
) {
    val appBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            ReadLaterAppBar(
                fileListType = uiState.fileListType,
                topAppBarScrollBehavior = appBarScrollBehavior,
                onFileListTypeClick = onFileListTypeClick,
                onGridSizeClick = onGridSizeClick,
                onSettingsClick = onSettingsClick
            )
        },
        modifier = Modifier.nestedScroll(appBarScrollBehavior.nestedScrollConnection)
    ) { contentPadding ->
        if (lazyPagingItems.isEmptyData) {
            EmptyContent(
                Modifier
                    .fillMaxSize()
                    .padding(contentPadding))
        } else {
            FileListSheet(
                fileListType = uiState.fileListType,
                lazyPagingItems = lazyPagingItems,
                contentPadding = contentPadding,
                onClickItem = onFileClick,
                onLongClickItem = onFileLongClick
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
