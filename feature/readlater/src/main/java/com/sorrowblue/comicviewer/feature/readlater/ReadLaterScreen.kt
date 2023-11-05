package com.sorrowblue.comicviewer.feature.readlater

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
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
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.feature.readlater.section.EmptyContent
import com.sorrowblue.comicviewer.feature.readlater.section.ReadLaterAction
import com.sorrowblue.comicviewer.feature.readlater.section.ReadLaterAppBar
import com.sorrowblue.comicviewer.file.component.FileContent
import com.sorrowblue.comicviewer.file.component.FileContentType
import com.sorrowblue.comicviewer.framework.ui.paging.isEmptyData
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList

@Composable
internal fun ReadLaterRoute(
    onFileClick: (File, Int) -> Unit,
    onSettingsClick: () -> Unit,
    contentPadding: PaddingValues,
    viewModel: ReadLaterViewModel = hiltViewModel()
) {
    val lazyGridState = rememberLazyGridState()
    val lazyPagingItems = viewModel.pagingDataFlow.collectAsLazyPagingItems()
    val uiState by viewModel.uiState.collectAsState()
    ReadLaterScreen(
        uiState = uiState,
        lazyPagingItems = lazyPagingItems,
        contentPadding = contentPadding,
        lazyGridState = lazyGridState,
        onFileClick = { onFileClick(it, lazyGridState.firstVisibleItemIndex) },
        onFileLongClick = { /*TODO*/ },
        onFileListTypeClick = viewModel::toggleDisplay,
        onGridSizeClick = viewModel::toggleSpanCount,
        onSettingsClick = onSettingsClick,
        onClearAllClick = viewModel::clearAll,
    )
}

internal data class ReadLaterScreenUiState(
    val list: PersistentList<ReadLaterAction> = ReadLaterAction.entries.filterNot { it == ReadLaterAction.FileContetGrid }
        .toPersistentList(),
    val fileContentType: FileContentType = FileContentType.List,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReadLaterScreen(
    uiState: ReadLaterScreenUiState,
    lazyPagingItems: LazyPagingItems<File>,
    contentPadding: PaddingValues,
    lazyGridState: LazyGridState,
    onFileClick: (File) -> Unit,
    onFileLongClick: (File) -> Unit,
    onFileListTypeClick: () -> Unit,
    onGridSizeClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onClearAllClick: () -> Unit,
) {
    val appBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val localLayoutDirection = LocalLayoutDirection.current
    Scaffold(
        topBar = {
            ReadLaterAppBar(
                list = uiState.list,
                fileContentType = uiState.fileContentType,
                topAppBarScrollBehavior = appBarScrollBehavior,
                onFileContentLayoutClick = onFileListTypeClick,
                onGridSizeClick = onGridSizeClick,
                onSettingsClick = onSettingsClick,
                onClearAllClick = onClearAllClick
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
            EmptyContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        } else {
            FileContent(
                type = uiState.fileContentType,
                lazyPagingItems = lazyPagingItems,
                contentPadding = innerPadding,
                onClickItem = onFileClick,
                onLongClickItem = onFileLongClick,
                state = lazyGridState
            )
        }
    }
}
