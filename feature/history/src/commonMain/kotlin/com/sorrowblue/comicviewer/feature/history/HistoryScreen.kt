package com.sorrowblue.comicviewer.feature.history

import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
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
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.feature.history.navigation.HistoryClearAllNavKey
import com.sorrowblue.comicviewer.feature.history.navigation.HistoryNavKey
import com.sorrowblue.comicviewer.feature.history.section.HistoryBookList
import com.sorrowblue.comicviewer.feature.history.section.HistoryTopAppBar
import com.sorrowblue.comicviewer.file.navigation.FileInfoNavKey
import com.sorrowblue.comicviewer.folder.navigation.FolderNavKey
import com.sorrowblue.comicviewer.framework.ui.adaptive.AdaptiveNavigationSuiteScaffold
import com.sorrowblue.comicviewer.framework.ui.adaptive.AdaptiveNavigationSuiteScaffoldState
import com.sorrowblue.comicviewer.framework.ui.adaptive.rememberAdaptiveNavigationSuiteScaffoldState
import com.sorrowblue.comicviewer.framework.ui.preview.PreviewTheme
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeBookFile
import com.sorrowblue.comicviewer.framework.ui.preview.fake.flowData

@NavEdge(FolderNavKey::class)
@NavEdge(FileInfoNavKey::class)
@NavEdge(HistoryClearAllNavKey::class)
@NavDestination(HistoryNavKey::class)
@Composable
internal fun AdaptiveNavigationSuiteScaffoldState.HistoryScreen(
    lazyPagingItems: LazyPagingItems<Book>,
    lazyGridState: LazyGridState,
    onDeleteAllClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onBookClick: (Book) -> Unit,
    onBookInfoClick: (Book) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    AdaptiveNavigationSuiteScaffold(
        modifier = modifier,
    ) {
        Scaffold(
            topBar = {
                HistoryTopAppBar(
                    onDeleteAllClick = onDeleteAllClick,
                    onSettingsClick = onSettingsClick,
                    scrollBehavior = scrollBehavior,
                )
            },
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        ) { contentPadding ->
            HistoryBookList(
                lazyPagingItems = lazyPagingItems,
                onItemClick = onBookClick,
                onItemInfoClick = onBookInfoClick,
                lazyGridState = lazyGridState,
                contentPadding = contentPadding,
            )
        }
    }
}

@NavPreview(HistoryNavKey::class, primary = true)
@Preview
@Composable
private fun HistoryScreenPreview() = PreviewTheme {
    val state = rememberAdaptiveNavigationSuiteScaffoldState()
    state.HistoryScreen(
        lazyPagingItems = PagingData.flowData<Book> { fakeBookFile() }.collectAsLazyPagingItems(),
        lazyGridState = LazyGridState(),
        onDeleteAllClick = {},
        onSettingsClick = {},
        onBookClick = {},
        onBookInfoClick = {},
    )
}
