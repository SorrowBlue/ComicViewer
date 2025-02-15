package com.sorrowblue.comicviewer.feature.favorite.add

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.ExternalModuleGraph
import com.ramcosta.composedestinations.spec.DestinationStyle
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.favorite.Favorite
import com.sorrowblue.comicviewer.feature.favorite.add.component.FavoriteAddButton
import com.sorrowblue.comicviewer.feature.favorite.add.component.FavoriteAddTopAppBar
import com.sorrowblue.comicviewer.feature.favorite.add.section.RecentFavoriteSheet
import com.sorrowblue.comicviewer.feature.favorite.common.component.FavoriteListItem
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.LaunchedEventEffect
import com.sorrowblue.comicviewer.framework.ui.material3.drawVerticalScrollbar
import com.sorrowblue.comicviewer.framework.ui.plus
import com.sorrowblue.comicviewer.framework.ui.preview.fake.fakeFavorite
import com.sorrowblue.comicviewer.framework.ui.preview.fake.flowData

data class FavoriteAddArgs(
    val bookshelfId: BookshelfId,
    val path: String,
)

interface FavoriteAddScreenNavigator {
    fun navigateUp()
    fun navigateToCreateFavorite(bookshelfId: BookshelfId, path: String)
}

@Destination<ExternalModuleGraph>(
    navArgs = FavoriteAddArgs::class,
    style = DestinationStyle.Dialog::class
)
@Composable
internal fun FavoriteAddDialogScreen(
    navBackStackEntry: NavBackStackEntry,
    navigator: FavoriteAddScreenNavigator,
) {
    FavoriteAddDialogScreen(
        navigator = navigator,
        state = rememberFavoriteAddScreenState(navBackStackEntry)
    )
}

@Composable
private fun FavoriteAddDialogScreen(
    navigator: FavoriteAddScreenNavigator,
    state: FavoriteAddScreenState,
) {
    val lazyPagingItems = state.pagingDataFlow.collectAsLazyPagingItems()
    val recentFavorites = state.recentFavoritesFlow.collectAsLazyPagingItems()
    FavoriteAddDialogScreen(
        lazyPagingItems = lazyPagingItems,
        recentFavorites = recentFavorites,
        onDismissRequest = navigator::navigateUp,
        onClick = state::onFavoriteClick,
        onNewFavoriteClick = state::onNewFavoriteClick
    )

    LaunchedEventEffect(event = state.event) {
        when (it) {
            is FavoriteAddScreenStateEvent.AddClick ->
                navigator.navigateToCreateFavorite(it.bookshelfId, it.path)
        }
    }
}

@Composable
private fun FavoriteAddDialogScreen(
    lazyPagingItems: LazyPagingItems<Favorite>,
    recentFavorites: LazyPagingItems<Favorite>,
    onDismissRequest: () -> Unit,
    onClick: (Favorite) -> Unit,
    onNewFavoriteClick: () -> Unit,
    lazyListState: LazyListState = rememberLazyListState(),
    sheetState: SheetState = rememberModalBottomSheetState(4 < lazyPagingItems.itemCount),
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        dragHandle = null,
        contentWindowInsets = { WindowInsets(0) },
        modifier = Modifier.statusBarsPadding()
    ) {
        Scaffold(
            topBar = { FavoriteAddTopAppBar(onCloseClick = onDismissRequest) },
            floatingActionButton = { FavoriteAddButton(onClick = onNewFavoriteClick) },
            contentWindowInsets = WindowInsets(0),
        ) { contentPadding ->
            LazyColumn(
                state = lazyListState,
                contentPadding = contentPadding + PaddingValues(bottom = BottomButtonMargin * 2 + ButtonDefaults.MinHeight),
                modifier = Modifier
                    .fillMaxSize()
                    .drawVerticalScrollbar(lazyListState)
            ) {
                item("recentFavorites") {
                    if (recentFavorites.itemCount > 0) {
                        RecentFavoriteSheet(
                            lazyPagingItems = recentFavorites,
                            onClick = onClick,
                            modifier = Modifier.animateItem()
                        )
                    }
                }
                items(
                    count = lazyPagingItems.itemCount,
                    key = lazyPagingItems.itemKey { it.id.value }
                ) { index ->
                    lazyPagingItems[index]?.let { item ->
                        FavoriteListItem(favorite = item, onClick = { onClick(item) }) {
                            if (item.exist) {
                                Icon(imageVector = ComicIcons.Check, contentDescription = null)
                            }
                        }
                    }
                }
            }
        }
    }
}

private val BottomButtonMargin = 16.dp

@Composable
@Preview
private fun PreviewFavoriteAddDialogScreen() {
    val lazyPagingItems = PagingData.flowData { fakeFavorite(it).copy(exist = it % 3 == 0) }
        .collectAsLazyPagingItems()
    FavoriteAddDialogScreen(
        lazyPagingItems = lazyPagingItems,
        recentFavorites = lazyPagingItems,
        onDismissRequest = {},
        onClick = {},
        onNewFavoriteClick = {}
    )
}
