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
import androidx.compose.ui.unit.dp
import com.sorrowblue.cmpdestinations.DestinationStyle
import com.sorrowblue.cmpdestinations.annotation.Destination
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.favorite.Favorite
import com.sorrowblue.comicviewer.feature.favorite.add.component.FavoriteAddButton
import com.sorrowblue.comicviewer.feature.favorite.add.component.FavoriteAddTopAppBar
import com.sorrowblue.comicviewer.feature.favorite.add.section.RecentFavoriteSheet
import com.sorrowblue.comicviewer.feature.favorite.common.component.FavoriteListItem
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.ui.EventEffect
import com.sorrowblue.comicviewer.framework.ui.layout.plus
import com.sorrowblue.comicviewer.framework.ui.paging.LazyPagingItems
import com.sorrowblue.comicviewer.framework.ui.paging.collectAsLazyPagingItems
import com.sorrowblue.comicviewer.framework.ui.paging.itemKey
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject

@Serializable
data class FavoriteAdd(val bookshelfId: BookshelfId, val path: String)

interface FavoriteAddScreenNavigator {
    fun navigateUp()
    fun navigateToCreateFavorite(bookshelfId: BookshelfId, path: String)
}

@Destination<FavoriteAdd>(style = DestinationStyle.Dialog::class)
@Composable
internal fun FavoriteAddDialogScreen(
    route: FavoriteAdd,
    navigator: FavoriteAddScreenNavigator = koinInject(),
    state: FavoriteAddScreenState = rememberFavoriteAddScreenState(route),
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

    EventEffect(state.events) {
        when (it) {
            is FavoriteAddScreenStateEvent.AddClick ->
                navigator.navigateToCreateFavorite(it.bookshelfId, it.path)
        }
    }
}

@Composable
internal fun FavoriteAddDialogScreen(
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
                // TODO Scrolllbar
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
