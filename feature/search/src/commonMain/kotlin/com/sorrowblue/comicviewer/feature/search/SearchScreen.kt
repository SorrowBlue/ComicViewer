package com.sorrowblue.comicviewer.feature.search

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.paging.compose.LazyPagingItems
import com.sorrowblue.comicviewer.domain.model.SearchCondition
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import com.sorrowblue.comicviewer.feature.search.component.SearchTopAppBar
import com.sorrowblue.comicviewer.feature.search.section.SearchList
import com.sorrowblue.comicviewer.feature.search.section.SearchListUiState
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.canonical.isNavigationRail
import com.sorrowblue.comicviewer.framework.ui.layout.plus
import kotlinx.serialization.Serializable

@Serializable
internal data class SearchScreenUiState(
    val searchCondition: SearchCondition = SearchCondition(),
    val searchContentsUiState: SearchListUiState = SearchListUiState(),
)

@Composable
internal fun SearchScreen(
    uiState: SearchScreenUiState,
    lazyPagingItems: LazyPagingItems<File>,
    lazyGridState: LazyGridState,
    onBackClick: () -> Unit,
    onSmartCollectionClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onQueryChange: (String) -> Unit,
    onRangeClick: (SearchCondition.Range) -> Unit,
    onPeriodClick: (SearchCondition.Period) -> Unit,
    onSortTypeClick: (SortType) -> Unit,
    onShowHiddenClick: () -> Unit,
    onItemClick: (File) -> Unit,
    onItemInfoClick: (File) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            SearchTopAppBar(
                searchCondition = uiState.searchCondition,
                scrollBehavior = scrollBehavior,
                onBackClick = onBackClick,
                onSmartCollectionClick = onSmartCollectionClick,
                onSettingsClick = onSettingsClick,
                onQueryChange = onQueryChange,
                onRangeClick = onRangeClick,
                onPeriodClick = onPeriodClick,
                onSortTypeClick = onSortTypeClick,
                onShowHiddenClick = onShowHiddenClick,
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { contentPadding ->
        val navigationSuiteType =
            NavigationSuiteScaffoldDefaults.navigationSuiteType(currentWindowAdaptiveInfo())
        val additionalPaddings = if (navigationSuiteType.isNavigationRail) {
            PaddingValues(ComicTheme.dimension.margin)
        } else {
            PaddingValues(
                start = ComicTheme.dimension.margin,
                end = ComicTheme.dimension.margin,
                bottom = ComicTheme.dimension.margin,
            )
        }
        SearchList(
            uiState = uiState.searchContentsUiState,
            lazyPagingItems = lazyPagingItems,
            lazyListState = lazyGridState,
            onItemClick = onItemClick,
            onItemInfoClick = onItemInfoClick,
            contentPadding = contentPadding + additionalPaddings,
        )
    }
}
