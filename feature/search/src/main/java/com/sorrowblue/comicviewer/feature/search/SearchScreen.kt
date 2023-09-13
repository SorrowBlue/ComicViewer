package com.sorrowblue.comicviewer.feature.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.ArrowBack
import androidx.compose.material.icons.twotone.Close
import androidx.compose.material.icons.twotone.FilterAlt
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.feature.search.section.SearchConditionSheet
import com.sorrowblue.comicviewer.feature.search.section.SearchConditionSheetUiState
import com.sorrowblue.comicviewer.feature.search.section.SearchResultSheet
import com.sorrowblue.comicviewer.feature.search.section.SearchResultSheetUiState
import com.sorrowblue.comicviewer.framework.compose.isLoadedData
import kotlinx.coroutines.launch
import logcat.logcat

internal data class SearchScreenUiState(
    val searchResultSheetUiState: SearchResultSheetUiState = SearchResultSheetUiState(),
    val searchConditionSheetUiState: SearchConditionSheetUiState = SearchConditionSheetUiState(),
    val searchQuery: String = "",
    val isShowSearchFilter: Boolean = false,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SearchRoute(viewModel: SearchViewModel = hiltViewModel(), onBackClick: () -> Unit) {
    val sheetState = remember {
        SheetState(
            skipPartiallyExpanded = false,
            initialValue = SheetValue.Expanded
        )
    }
    val scaffoldState = rememberBottomSheetScaffoldState(sheetState)
    val uiState by viewModel.uiState.collectAsState()
    val lazyPagingItems = viewModel.pagingDataFlow.collectAsLazyPagingItems()
    val lazyListState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    SearchScreen(
        uiState = uiState,
        scaffoldState = scaffoldState,
        lazyPagingItems = lazyPagingItems,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onBackClick = onBackClick,
        toggleSearchFilter = {
            scope.launch {
                if (scaffoldState.bottomSheetState.targetValue == SheetValue.Expanded) {
                    scaffoldState.bottomSheetState.partialExpand()
                } else {
                    scaffoldState.bottomSheetState.expand()
                }
            }
        },
        onChangeRange = viewModel::updateRange,
        onChangePeriod = viewModel::updatePeriod,
        onChangeSort = viewModel::updateSort,
        onChangeOrder = viewModel::updateOrder,
        onFileClick = {},
        lazyListState = lazyListState
    )
    LaunchedEffect(uiState.searchQuery, uiState.searchConditionSheetUiState) {
        if (!viewModel.isSkipFirstRefresh) {
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
    scaffoldState: BottomSheetScaffoldState,
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
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        Column {
            var parentHeight by remember { mutableStateOf(0.dp) }
            var componentHeight by remember { mutableStateOf(0.dp) }
            val sheetPeekHeight by remember(parentHeight, componentHeight) {
                derivedStateOf { parentHeight - componentHeight }
            }
            val density = LocalDensity.current

            TopAppBar(
                title = {
                    OutlinedTextField(
                        value = uiState.searchQuery,
                        onValueChange = onSearchQueryChange,
                        placeholder = { Text(text = "Search") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (scaffoldState.bottomSheetState.currentValue == SheetValue.PartiallyExpanded) {
                            toggleSearchFilter.invoke()
                        } else {
                            onBackClick.invoke()
                        }
                    }) {
                        if (scaffoldState.bottomSheetState.currentValue == SheetValue.PartiallyExpanded) {
                            Icon(Icons.TwoTone.Close, "")
                        } else {
                            Icon(Icons.TwoTone.ArrowBack, "")
                        }
                    }
                },
                actions = {
                    IconButton(onClick = toggleSearchFilter) {
                        Icon(Icons.TwoTone.FilterAlt, "")
                    }
                }
            )
            BottomSheetScaffold(
                modifier = Modifier.onGloballyPositioned {
                    componentHeight = with(density) { it.size.height.toDp() }
                },
                scaffoldState = scaffoldState,
                sheetPeekHeight = sheetPeekHeight,
                sheetContent = {
                    Box(
                        modifier = Modifier.onGloballyPositioned {
                            parentHeight = with(density) { it.size.height.toDp() }
                        }
                    ) {
                        SearchResultSheet(
                            lazyPagingItems = lazyPagingItems,
                            lazyListState = lazyListState,
                            uiState = uiState.searchResultSheetUiState,
                            onExpandRequest = toggleSearchFilter,
                            onFileClick = onFileClick
                        )
                    }
                },
                sheetSwipeEnabled = false,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                sheetDragHandle = {},
                content = {
                    SearchConditionSheet(
                        uiState = uiState.searchConditionSheetUiState,
                        onChangeRange = onChangeRange,
                        onChangePeriod = onChangePeriod,
                        onChangeSort = onChangeSort,
                        onChangeOrder = onChangeOrder
                    )
                }
            )
        }
    }
}
