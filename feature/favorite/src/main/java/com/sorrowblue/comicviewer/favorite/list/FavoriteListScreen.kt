package com.sorrowblue.comicviewer.favorite.list

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.domain.model.favorite.Favorite
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.favorite.section.FavoriteListAppBar
import com.sorrowblue.comicviewer.favorite.section.FavoriteListEmptySheet
import com.sorrowblue.comicviewer.favorite.section.FavoriteListSheet
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.asWindowInsets
import com.sorrowblue.comicviewer.framework.ui.paging.isEmptyData
import com.sorrowblue.comicviewer.framework.ui.responsive.AppScaffold
import com.sorrowblue.comicviewer.framework.ui.responsive.rememberAppScaffold
import kotlinx.coroutines.flow.flowOf

@Composable
internal fun FavoriteListRoute(
    contentPadding: PaddingValues,
    onSettingsClick: () -> Unit,
    onFavoriteClick: (FavoriteId) -> Unit,
    viewModel: FavoriteListViewModel = hiltViewModel(),
) {
    val lazyPagingItems = viewModel.pagingDataFlow.collectAsLazyPagingItems()
    FavoriteListScreen(
        contentPadding = contentPadding,
        lazyPagingItems = lazyPagingItems,
        onSettingsClick = onSettingsClick,
        onFavoriteClick = onFavoriteClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FavoriteListScreen(
    lazyPagingItems: LazyPagingItems<Favorite>,
    onSettingsClick: () -> Unit = {},
    onFavoriteClick: (FavoriteId) -> Unit = {},
    contentPadding: PaddingValues = PaddingValues(),
) {
    val appBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val lazyListState = rememberLazyListState()
    AppScaffold(
        state = rememberAppScaffold(true),
        topBar = {
            FavoriteListAppBar(
                topAppBarScrollBehavior = appBarScrollBehavior,
                onSettingsClick = onSettingsClick
            )
        },
        contentWindowInsets = contentPadding.asWindowInsets(),
        modifier = Modifier.nestedScroll(appBarScrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        if (lazyPagingItems.isEmptyData) {
            FavoriteListEmptySheet(contentPadding = innerPadding)
        } else {
            FavoriteListSheet(
                lazyPagingItems = lazyPagingItems,
                innerPadding = innerPadding,
                lazyListState = lazyListState,
                onFavoriteClick = onFavoriteClick
            )
        }
    }
}

@Preview
@Composable
private fun PreviewFavoriteListScreen() {
    ComicTheme {
        val items = List(20) {
            Favorite(FavoriteId(it), "Favorite name $it", it * 100)
        }
        FavoriteListScreen(
            lazyPagingItems = flowOf(PagingData.from(items)).collectAsLazyPagingItems(),
        )
    }
}
