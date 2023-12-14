package com.sorrowblue.comicviewer.feature.search

import android.os.Parcelable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.domain.model.SearchCondition
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.feature.search.component.SearchAppBar
import com.sorrowblue.comicviewer.feature.search.navigation.SearchArgs
import com.sorrowblue.comicviewer.feature.search.section.SearchConditions
import com.sorrowblue.comicviewer.feature.search.section.SearchConditionsUiState
import com.sorrowblue.comicviewer.feature.search.section.SearchResultSheet
import com.sorrowblue.comicviewer.file.rememberSideSheetFileState
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.asWindowInsets
import com.sorrowblue.comicviewer.framework.ui.material3.TopAppBarDefaults
import com.sorrowblue.comicviewer.framework.ui.material3.pinnedScrollBehavior
import com.sorrowblue.comicviewer.framework.ui.paging.isLoadedData
import com.sorrowblue.comicviewer.framework.ui.responsive.ResponsiveScaffold
import com.sorrowblue.comicviewer.framework.ui.responsive.ResponsiveScaffoldState
import com.sorrowblue.comicviewer.framework.ui.responsive.rememberResponsiveScaffoldState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class SearchScreenUiState(
    val query: String = "",
    val searchConditionsUiState: SearchConditionsUiState = SearchConditionsUiState(),
) : Parcelable

@Stable
internal class SearchScreenState(
    initUiState: SearchScreenUiState = SearchScreenUiState(),
    private val args: SearchArgs,
    private val viewModel: SearchViewModel,
) {

    val lazyPagingItems: Flow<PagingData<File>> = viewModel.pagingDataFlow
    var isScrollableTop by mutableStateOf(false)
    var isSkipFirstRefresh by mutableStateOf(true)
    var uiState by mutableStateOf(initUiState)
        private set

    init {
        viewModel.searchCondition = {
            SearchCondition(
                uiState.query,
                when (uiState.searchConditionsUiState.range) {
                    SearchConditionsUiState.Range.BOOKSHELF -> SearchCondition.Range.BOOKSHELF
                    SearchConditionsUiState.Range.IN_FOLDER ->
                        SearchCondition.Range.InFolder(args.path)

                    SearchConditionsUiState.Range.FOLDER_BELOW ->
                        SearchCondition.Range.SubFolder(args.path)
                },
                when (uiState.searchConditionsUiState.period) {
                    SearchConditionsUiState.Period.NONE -> SearchCondition.Period.NONE
                    SearchConditionsUiState.Period.HOUR_24 -> SearchCondition.Period.HOUR_24
                    SearchConditionsUiState.Period.WEEK_1 -> SearchCondition.Period.WEEK_1
                    SearchConditionsUiState.Period.MONTH_1 -> SearchCondition.Period.MONTH_1
                },
                when (uiState.searchConditionsUiState.order) {
                    SearchConditionsUiState.Order.NAME -> SearchCondition.Order.NAME
                    SearchConditionsUiState.Order.TIMESTAMP -> SearchCondition.Order.DATE
                    SearchConditionsUiState.Order.SIZE -> SearchCondition.Order.SIZE
                },
                when (uiState.searchConditionsUiState.sort) {
                    SearchConditionsUiState.Sort.ASC -> SearchCondition.Sort.ASC
                    SearchConditionsUiState.Sort.DESC -> SearchCondition.Sort.DESC
                }
            )
        }
    }

    fun onChangeSearchCondition(searchCondition: SearchConditionsUiState.SearchCondition) {
        val searchConditionsUiState = uiState.searchConditionsUiState
        uiState = uiState.copy(
            searchConditionsUiState = when (searchCondition) {
                is SearchConditionsUiState.Order -> searchConditionsUiState.copy(order = searchCondition)
                is SearchConditionsUiState.Period -> searchConditionsUiState.copy(period = searchCondition)
                is SearchConditionsUiState.Range -> searchConditionsUiState.copy(range = searchCondition)
                is SearchConditionsUiState.Sort -> searchConditionsUiState.copy(sort = searchCondition)
            }
        )
        update()
    }

    fun onQueryChange(query: String) {
        uiState = uiState.copy(query = query)
        update()
    }

    fun onReadLaterClick(file: File) {
        viewModel.addReadLater(file)
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
) = rememberSaveable(
    saver = Saver(
        save = { it.uiState },
        restore = { SearchScreenState(args = args, viewModel = viewModel, initUiState = it) }
    )
) {
    SearchScreenState(args = args, viewModel = viewModel)
}

private const val WaitLoadPage = 500L

@Composable
internal fun SearchRoute(
    args: SearchArgs,
    contentPadding: PaddingValues,
    onBackClick: () -> Unit,
    onFileClick: (File) -> Unit,
    onFavoriteClick: (File) -> Unit,
    onOpenFolderClick: (File) -> Unit,
    state: SearchScreenState = rememberSearchScreenState(args),
) {
    val uiState = state.uiState
    val lazyPagingItems = state.lazyPagingItems.collectAsLazyPagingItems()
    val lazyGridState = rememberLazyGridState()
    val scaffoldState = rememberResponsiveScaffoldState(rememberSideSheetFileState())
    SearchScreen(
        uiState = uiState,
        lazyGridState = lazyGridState,
        scaffoldState = scaffoldState,
        lazyPagingItems = lazyPagingItems,
        onQueryChange = state::onQueryChange,
        onBackClick = onBackClick,
        onChangeSearchCondition = state::onChangeSearchCondition,
        onFileClick = onFileClick,
        onFileLongClick = { scaffoldState.sheetState.show(it) },
        onFileInfoCloseClick = { scaffoldState.sheetState.hide() },
        onReadLaterClick = state::onReadLaterClick,
        onFavoriteClick = onFavoriteClick,
        onOpenFolderClick = onOpenFolderClick,
        contentPadding = contentPadding
    )
    LaunchedEffect(uiState) {
        if (!state.isSkipFirstRefresh) {
            delay(WaitLoadPage)
            lazyPagingItems.refresh()
        }
    }
    LaunchedEffect(lazyPagingItems.loadState) {
        if (lazyPagingItems.isLoadedData && state.isScrollableTop) {
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

    onQueryChange: (String) -> Unit,
    onBackClick: () -> Unit,

    onChangeSearchCondition: (SearchConditionsUiState.SearchCondition) -> Unit,

    onFileClick: (File) -> Unit,
    onFileLongClick: (File) -> Unit,

    onFileInfoCloseClick: () -> Unit,
    onReadLaterClick: (File) -> Unit,
    onFavoriteClick: (File) -> Unit,
    onOpenFolderClick: (File) -> Unit,

    contentPadding: PaddingValues,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    ResponsiveScaffold(
        state = scaffoldState,
        topBar = {
            Column {
                SearchAppBar(
                    query = uiState.query,
                    onBackClick = onBackClick,
                    onQueryChange = onQueryChange,
                    contentPadding = contentPadding,
                    scrollBehavior = scrollBehavior
                )
                SearchConditions(
                    uiState = uiState.searchConditionsUiState,
                    onChangeSearchCondition = onChangeSearchCondition,
                    scrollBehavior = scrollBehavior,
                    modifier = Modifier
                        .windowInsetsPadding(
                            contentPadding
                                .asWindowInsets()
                                .only(WindowInsetsSides.Horizontal)
                        )
                        .padding(horizontal = ComicTheme.dimension.margin)
                        .padding(top = ComicTheme.dimension.padding * 2)
                )
            }
        },
        sideSheet = { file, innerPadding ->
        },
        bottomSheet = { file ->
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
