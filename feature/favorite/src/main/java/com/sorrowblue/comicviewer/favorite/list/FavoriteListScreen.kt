package com.sorrowblue.comicviewer.favorite.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.sorrowblue.comicviewer.domain.model.favorite.Favorite
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.favorite.section.FavoriteListAppBar
import com.sorrowblue.comicviewer.feature.favorite.R
import com.sorrowblue.comicviewer.feature.favorite.common.component.FavoriteItem
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.undraw.UndrawNoData
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.paging.isEmptyData
import com.sorrowblue.comicviewer.framework.ui.plus
import com.sorrowblue.comicviewer.framework.ui.toWindowInsets
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
    Scaffold(
        topBar = {
            FavoriteListAppBar(
                topAppBarScrollBehavior = appBarScrollBehavior,
                onSettingsClick = onSettingsClick
            )
        },
        contentWindowInsets = contentPadding.toWindowInsets(),
        modifier = Modifier.nestedScroll(appBarScrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        if (lazyPagingItems.isEmptyData) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    imageVector = ComicIcons.UndrawNoData,
                    modifier = Modifier
                        .widthIn(min = 200.dp)
                        .fillMaxWidth(0.5f),
                    contentDescription = null
                )
                Text(
                    text = stringResource(id = R.string.favorite_list_label_no_favorites),
                    style = MaterialTheme.typography.titleLarge
                )
            }
        } else {
            LazyColumn(
                contentPadding = innerPadding.plus(paddingValues = PaddingValues(bottom = 60.dp)),
                state = lazyListState
            ) {
                items(lazyPagingItems.itemCount, key = lazyPagingItems.itemKey { it.id.value }) {
                    val item = lazyPagingItems[it]
                    if (item != null) {
                        FavoriteItem(favorite = item, onClick = { onFavoriteClick(item.id) })
                    }
                }
            }
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
