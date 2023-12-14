package com.sorrowblue.comicviewer.feature.history

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.feature.history.section.EmptyContent
import com.sorrowblue.comicviewer.feature.history.section.HistoryAppBar
import com.sorrowblue.comicviewer.file.component.FileContent
import com.sorrowblue.comicviewer.file.component.FileContentType
import com.sorrowblue.comicviewer.file.rememberSideSheetFileState
import com.sorrowblue.comicviewer.framework.ui.paging.isEmptyData
import com.sorrowblue.comicviewer.framework.ui.responsive.ResponsiveScaffold
import com.sorrowblue.comicviewer.framework.ui.responsive.ResponsiveScaffoldState
import com.sorrowblue.comicviewer.framework.ui.responsive.rememberResponsiveScaffoldState

@Composable
internal fun HistoryRoute(
    onFileClick: (Book) -> Unit,
    onSettingsClick: () -> Unit,
    contentPadding: PaddingValues,
    viewModel: HistoryViewModel = hiltViewModel(),
) {
    val lazyPagingItems = viewModel.pagingDataFlow.collectAsLazyPagingItems()
    val uiState: ReadLaterScreenUiState by viewModel.uiState.collectAsState()
    val lazyGridState = rememberLazyGridState()
    val scaffoldState =
        rememberResponsiveScaffoldState(sideSheetState = rememberSideSheetFileState())
    HistoryScreen(
        uiState = uiState,
        lazyPagingItems = lazyPagingItems,
        scaffoldState = scaffoldState,
        onFileClick = onFileClick,
        onFileLongClick = scaffoldState.sheetState::show,
        onFileListTypeClick = viewModel::toggleDisplay,
        onGridSizeClick = viewModel::onGridSizeChange,
        onSettingsClick = onSettingsClick,
        contentPadding = contentPadding,
        lazyGridState = lazyGridState,
    )
}

data class ReadLaterScreenUiState(
    val fileContentType: FileContentType,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HistoryScreen(
    uiState: ReadLaterScreenUiState,
    lazyPagingItems: LazyPagingItems<Book>,
    scaffoldState: ResponsiveScaffoldState<File>,
    onFileClick: (Book) -> Unit,
    onFileLongClick: (File) -> Unit,
    onFileListTypeClick: () -> Unit,
    onGridSizeClick: () -> Unit,
    onSettingsClick: () -> Unit,
    contentPadding: PaddingValues,
    lazyGridState: LazyGridState,
) {
    val appBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val localLayoutDirection = LocalLayoutDirection.current
    ResponsiveScaffold(
        state = scaffoldState,
        topBar = {
            HistoryAppBar(
                fileContentType = uiState.fileContentType,
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
        sideSheet = { file, innerPadding ->
        },
        bottomSheet = { file ->
        },
        modifier = Modifier.nestedScroll(appBarScrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        if (lazyPagingItems.isEmptyData) {
            EmptyContent(Modifier.padding(innerPadding))
        } else {
            FileContent(
                type = uiState.fileContentType,
                lazyPagingItems = lazyPagingItems,
                contentPadding = innerPadding,
                onFileClick = onFileClick,
                onInfoClick = onFileLongClick,
                state = lazyGridState
            )
        }
    }
}
