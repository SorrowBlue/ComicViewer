package com.sorrowblue.comicviewer.feature.search

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.github.skydoves.navgraph.annotations.NavDestination
import com.github.skydoves.navgraph.annotations.NavEdge
import com.github.skydoves.navgraph.annotations.NavPreview
import com.sorrowblue.comicviewer.domain.model.SearchCondition
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import com.sorrowblue.comicviewer.feature.search.component.SearchTopAppBar
import com.sorrowblue.comicviewer.feature.search.navigation.SearchNavKey
import com.sorrowblue.comicviewer.feature.search.section.SearchList
import com.sorrowblue.comicviewer.feature.search.section.SearchListUiState
import com.sorrowblue.comicviewer.file.navigation.FileInfoNavKey
import com.sorrowblue.comicviewer.folder.navigation.FolderNavKey
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.adaptive.isNavigationRail
import com.sorrowblue.comicviewer.framework.ui.layout.plus
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeBookFile
import com.sorrowblue.comicviewer.framework.ui.preview.fake.flowData

@NavDestination(SearchNavKey::class)
@NavEdge(FolderNavKey::class)
@NavEdge(FileInfoNavKey::class)
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
    modifier: Modifier = Modifier,
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
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { contentPadding ->
        val navigationSuiteType =
            NavigationSuiteScaffoldDefaults.navigationSuiteType(currentWindowAdaptiveInfoV2())
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

@NavPreview(SearchNavKey::class)
@Preview
@Composable
private fun SearchScreenPreview() = PreviewTheme {
    SearchScreen(
        uiState = SearchScreenUiState(
            searchCondition = SearchCondition(),
            searchContentsUiState = SearchListUiState(),
        ),
        lazyPagingItems = PagingData.flowData<File> { fakeBookFile() }
            .collectAsLazyPagingItems(),
        lazyGridState = LazyGridState(),
        onBackClick = {},
        onSmartCollectionClick = {},
        onSettingsClick = {},
        onQueryChange = {},
        onRangeClick = {},
        onPeriodClick = {},
        onSortTypeClick = {},
        onShowHiddenClick = {},
        onItemClick = {},
        onItemInfoClick = {},
    )
}
