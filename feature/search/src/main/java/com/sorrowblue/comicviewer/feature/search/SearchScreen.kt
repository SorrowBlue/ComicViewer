package com.sorrowblue.comicviewer.feature.search

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.feature.search.component.SearchAppBar
import com.sorrowblue.comicviewer.feature.search.section.SearchConditionSheet
import com.sorrowblue.comicviewer.feature.search.section.SearchConditionSheetUiState
import com.sorrowblue.comicviewer.feature.search.section.SearchResultSheet
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.ComicPreviews
import com.sorrowblue.comicviewer.framework.ui.fakeLazyPagingItems
import com.sorrowblue.comicviewer.framework.ui.paging.isLoadedData
import kotlinx.coroutines.delay
import logcat.logcat

internal data class SearchScreenUiState(
    val searchConditionSheetUiState: SearchConditionSheetUiState = SearchConditionSheetUiState(),
    val searchQuery: String = "",
)

@Composable
internal fun SearchRoute(
    viewModel: SearchViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onFileClick: (File) -> Unit,
    onFileLongClick: (File) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val lazyPagingItems = viewModel.pagingDataFlow.collectAsLazyPagingItems()
    val lazyListState = rememberLazyListState()
    SearchScreen(
        uiState = uiState,
        lazyListState = lazyListState,
        lazyPagingItems = lazyPagingItems,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onBackClick = onBackClick,
        toggleSearchFilter = viewModel::toggleSearchFilter,
        onChangeRange = viewModel::updateRange,
        onChangePeriod = viewModel::updatePeriod,
        onChangeSort = viewModel::updateSort,
        onChangeOrder = viewModel::updateOrder,
        onFileClick = onFileClick,
        onFileLongClick = onFileLongClick,
    )
    LaunchedEffect(uiState.searchQuery, uiState.searchConditionSheetUiState) {
        if (!viewModel.isSkipFirstRefresh) {
            delay(500)
            logcat { "Refresh FileList(search)" }
            lazyPagingItems.refresh()
        }
    }
    LaunchedEffect(lazyPagingItems.loadState) {
        if (lazyPagingItems.isLoadedData && viewModel.isScrollableTop) {
            logcat { "Scroll to top.(search)" }
            viewModel.isScrollableTop = false
            lazyListState.scrollToItem(0)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchScreen(
    uiState: SearchScreenUiState,
    lazyListState: LazyListState,
    lazyPagingItems: LazyPagingItems<File>,
    onSearchQueryChange: (String) -> Unit = {},
    onBackClick: () -> Unit = {},
    toggleSearchFilter: () -> Unit = {},
    onChangeRange: (SearchConditionSheetUiState.Range) -> Unit = {},
    onChangePeriod: (SearchConditionSheetUiState.Period) -> Unit = {},
    onChangeSort: (SearchConditionSheetUiState.Sort) -> Unit = {},
    onChangeOrder: (SearchConditionSheetUiState.Order) -> Unit = {},
    onFileClick: (File) -> Unit = {},
    onFileLongClick: (File) -> Unit = {},
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            SearchAppBar(
                uiState = uiState,
                onSearchQueryChange = onSearchQueryChange,
                onBackClick = onBackClick,
                toggleSearchFilter = toggleSearchFilter,
                scrollBehavior = scrollBehavior
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { contentPadding ->
        SearchResultSheet(
            query = uiState.searchQuery,
            lazyPagingItems = lazyPagingItems,
            contentPadding = contentPadding,
            lazyListState = lazyListState,
            onFileClick = onFileClick,
            onFileLongClick = onFileLongClick,
        )
    }
    if (uiState.searchConditionSheetUiState.isVisible) {
        ModalBottomSheet(
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            onDismissRequest = toggleSearchFilter,
            windowInsets = WindowInsets(0)
        ) {
            SearchConditionSheet(
                uiState = uiState.searchConditionSheetUiState,
                onChangeRange = onChangeRange,
                onChangePeriod = onChangePeriod,
                onChangeSort = onChangeSort,
                onChangeOrder = onChangeOrder
            )
        }
    }
}

@ComicPreviews
@Composable
private fun Preview() {
    ComicTheme {
        SearchScreen(
            uiState = SearchScreenUiState(
                searchConditionSheetUiState = SearchConditionSheetUiState(),
                searchQuery = ""
            ),
            lazyListState = rememberLazyListState(),
            lazyPagingItems = fakeLazyPagingItems(),

            )
    }
}

@Preview
@Composable
private fun PreviewFilter() {
    ComicTheme {
        SearchScreen(
            uiState = SearchScreenUiState(
                searchConditionSheetUiState = SearchConditionSheetUiState(
                    isVisible = true
                ), searchQuery = ""
            ),
            lazyListState = rememberLazyListState(),
            lazyPagingItems = fakeLazyPagingItems(),

            )
    }
}
