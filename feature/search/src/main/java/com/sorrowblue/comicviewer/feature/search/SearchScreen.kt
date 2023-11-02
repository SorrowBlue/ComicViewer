package com.sorrowblue.comicviewer.feature.search

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.domain.model.SearchCondition
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.feature.search.component.DropdownMenuChip
import com.sorrowblue.comicviewer.feature.search.component.SearchAppBar
import com.sorrowblue.comicviewer.feature.search.navigation.SearchArgs
import com.sorrowblue.comicviewer.feature.search.section.SearchConditionSheetUiState
import com.sorrowblue.comicviewer.feature.search.section.SearchResultSheet
import com.sorrowblue.comicviewer.file.FileInfoBottomSheet
import com.sorrowblue.comicviewer.file.FileInfoSheet
import com.sorrowblue.comicviewer.file.rememberSideSheetFileState
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.add
import com.sorrowblue.comicviewer.framework.ui.asWindowInsets
import com.sorrowblue.comicviewer.framework.ui.material3.TopAppBarDefaults
import com.sorrowblue.comicviewer.framework.ui.material3.pinnedScrollBehavior
import com.sorrowblue.comicviewer.framework.ui.paging.isLoadedData
import com.sorrowblue.comicviewer.framework.ui.responsive.ResponsiveScaffold
import com.sorrowblue.comicviewer.framework.ui.responsive.ResponsiveScaffoldState
import com.sorrowblue.comicviewer.framework.ui.responsive.rememberResponsiveScaffoldState
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.delay
import logcat.logcat

internal data class SearchScreenUiState(
    val query: String = "",
    val range: SearchConditionSheetUiState.Range = SearchConditionSheetUiState.Range.BOOKSHELF,
    val period: SearchConditionSheetUiState.Period = SearchConditionSheetUiState.Period.NONE,
    val order: SearchConditionSheetUiState.Order = SearchConditionSheetUiState.Order.NAME,
    val sort: SearchConditionSheetUiState.Sort = SearchConditionSheetUiState.Sort.ASC,
)

@Stable
internal class SearchScreenState(
    private val args: SearchArgs,
    private val viewModel: SearchViewModel,
) {

    init {
        viewModel.searchCondition = {
            SearchCondition(
                uiState.query,
                when (uiState.range) {
                    SearchConditionSheetUiState.Range.BOOKSHELF -> SearchCondition.Range.BOOKSHELF
                    SearchConditionSheetUiState.Range.IN_FOLDER ->
                        SearchCondition.Range.InFolder(args.path)

                    SearchConditionSheetUiState.Range.FOLDER_BELOW ->
                        SearchCondition.Range.SubFolder(args.path)
                },
                when (uiState.period) {
                    SearchConditionSheetUiState.Period.NONE -> SearchCondition.Period.NONE
                    SearchConditionSheetUiState.Period.HOUR_24 -> SearchCondition.Period.HOUR_24
                    SearchConditionSheetUiState.Period.WEEK_1 -> SearchCondition.Period.WEEK_1
                    SearchConditionSheetUiState.Period.MONTH_1 -> SearchCondition.Period.MONTH_1
                },
                when (uiState.order) {
                    SearchConditionSheetUiState.Order.NAME -> SearchCondition.Order.NAME
                    SearchConditionSheetUiState.Order.TIMESTAMP -> SearchCondition.Order.DATE
                    SearchConditionSheetUiState.Order.SIZE -> SearchCondition.Order.SIZE
                },
                when (uiState.sort) {
                    SearchConditionSheetUiState.Sort.ASC -> SearchCondition.Sort.ASC
                    SearchConditionSheetUiState.Sort.DESC -> SearchCondition.Sort.DESC
                }
            )
        }
    }

    val lazyPagingItems: LazyPagingItems<File>
        @Composable
        get() = viewModel.pagingDataFlow.collectAsLazyPagingItems()
    var isScrollableTop by mutableStateOf(false)
    var isSkipFirstRefresh by mutableStateOf(true)
    var uiState by mutableStateOf(SearchScreenUiState())
        private set

    fun onRangeChange(range: SearchConditionSheetUiState.Range) {
        uiState = uiState.copy(range = range)
        update()
    }

    fun onPeriodChange(period: SearchConditionSheetUiState.Period) {
        uiState = uiState.copy(period = period)
        update()
    }

    fun onSortChange(sort: SearchConditionSheetUiState.Sort) {
        uiState = uiState.copy(sort = sort)
        update()
    }

    fun onOrderChange(order: SearchConditionSheetUiState.Order) {
        uiState = uiState.copy(order = order)
        update()
    }

    fun onQueryChange(query: String) {
        uiState = uiState.copy(query = query)
        update()
    }

    private fun update() {
        isScrollableTop = true
        if (isSkipFirstRefresh) {
            isSkipFirstRefresh = false
        }
    }
}

@Composable
private fun rememberSearchScreenState(
    args: SearchArgs,
    viewModel: SearchViewModel = hiltViewModel(),
) = remember {
    SearchScreenState(args = args, viewModel = viewModel)
}

@Composable
internal fun SearchRoute(
    args: SearchArgs,
    state: SearchScreenState = rememberSearchScreenState(args),
    onBackClick: () -> Unit,
    onFileClick: (File) -> Unit,
    contentPadding: PaddingValues = PaddingValues(),
) {
    val uiState = state.uiState
    val lazyPagingItems: LazyPagingItems<File> = state.lazyPagingItems
    val lazyGridState = rememberLazyGridState()
    val scaffoldState: ResponsiveScaffoldState<File> =
        rememberResponsiveScaffoldState(sideSheetState = rememberSideSheetFileState())
    SearchScreen(
        uiState = uiState,
        lazyGridState = lazyGridState,
        scaffoldState = scaffoldState,
        lazyPagingItems = lazyPagingItems,
        onQueryChange = state::onQueryChange,
        onBackClick = onBackClick,
        onChangeRange = state::onRangeChange,
        onChangePeriod = state::onPeriodChange,
        onChangeSort = state::onSortChange,
        onChangeOrder = state::onOrderChange,
        onFileClick = onFileClick,
        onFileLongClick = { scaffoldState.sheetState.show(it) },
        onFileInfoCloseClick = { scaffoldState.sheetState.hide() },
        contentPadding = contentPadding
    )
    LaunchedEffect(uiState) {
        if (!state.isSkipFirstRefresh) {
            delay(500)
            logcat { "Refresh FileList(search)" }
            lazyPagingItems.refresh()
        }
    }
    LaunchedEffect(lazyPagingItems.loadState) {
        if (lazyPagingItems.isLoadedData && state.isScrollableTop) {
            logcat { "Scroll to top.(search)" }
            state.isScrollableTop = false
            lazyGridState.scrollToItem(0)
        }
    }
}

@Composable
private fun SearchScreen(
    uiState: SearchScreenUiState,
    lazyGridState: LazyGridState,
    scaffoldState: ResponsiveScaffoldState<File>,
    lazyPagingItems: LazyPagingItems<File>,
    onQueryChange: (String) -> Unit = {},
    onBackClick: () -> Unit = {},
    onChangeRange: (SearchConditionSheetUiState.Range) -> Unit = {},
    onChangePeriod: (SearchConditionSheetUiState.Period) -> Unit = {},
    onChangeSort: (SearchConditionSheetUiState.Sort) -> Unit = {},
    onChangeOrder: (SearchConditionSheetUiState.Order) -> Unit = {},
    onFileClick: (File) -> Unit = {},
    onFileLongClick: (File) -> Unit = {},
    onFileInfoCloseClick: () -> Unit = {},
    contentPadding: PaddingValues = PaddingValues(),
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    ResponsiveScaffold(
        state = scaffoldState,
        topBar = {
            SearchAppBar(
                uiState = uiState,
                onBackClick = onBackClick,
                onQueryChange = onQueryChange,
                onChangeRange = onChangeRange,
                onChangePeriod = onChangePeriod,
                onChangeSort = onChangeSort,
                onChangeOrder = onChangeOrder,
                contentPadding = contentPadding,
                scrollBehavior = scrollBehavior
            )
        },
        sideSheet = { file, innerPadding ->
            FileInfoSheet(
                file,
                contentPadding = innerPadding.add(paddingValues = PaddingValues(top = 8.dp)),
                onCloseClick = onFileInfoCloseClick
            )
        },
        bottomSheet = {
            FileInfoBottomSheet(it)
        },
        contentWindowInsets = contentPadding.asWindowInsets(),
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        SearchResultSheet(
            query = uiState.query,
            lazyPagingItems = lazyPagingItems,
            contentPadding = innerPadding,
            lazyListState = lazyGridState,
            onFileClick = onFileClick,
            onFileLongClick = onFileLongClick,
        )
    }
}
