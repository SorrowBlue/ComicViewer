package com.sorrowblue.comicviewer.feature.favorite.add

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.ExternalModuleGraph
import com.ramcosta.composedestinations.spec.DestinationStyle
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.favorite.Favorite
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.feature.favorite.common.component.FavoriteItem
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.add
import com.sorrowblue.comicviewer.framework.ui.material3.drawVerticalScrollbar
import kotlinx.coroutines.flow.flowOf

class FavoriteAddArgs(
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
    navArgs: FavoriteAddArgs,
    navigator: FavoriteAddScreenNavigator,
) {
    FavoriteAddDialogScreen(
        navArgs = navArgs,
        navigator = navigator,
        state = rememberFavoriteAddScreenState()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FavoriteAddDialogScreen(
    navArgs: FavoriteAddArgs,
    navigator: FavoriteAddScreenNavigator,
    state: FavoriteAddScreenState,
) {
    val lazyPagingItems = state.pagingDataFlow.collectAsLazyPagingItems()
    FavoriteAddDialogScreen(
        lazyPagingItems = lazyPagingItems,
        onDismissRequest = navigator::navigateUp,
        onClick = state::onFavoriteClick,
        onNewFavoriteClick = {
            navigator.navigateToCreateFavorite(navArgs.bookshelfId, navArgs.path)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FavoriteAddDialogScreen(
    lazyPagingItems: LazyPagingItems<Favorite>,
    onDismissRequest: () -> Unit,
    onClick: (Favorite) -> Unit,
    onNewFavoriteClick: () -> Unit,
    lazyListState: LazyListState = rememberLazyListState(),
    sheetState: SheetState = rememberModalBottomSheetState(true),
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        dragHandle = null,
        contentWindowInsets = { WindowInsets(0) }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(
                horizontal = ComicTheme.dimension.margin,
                vertical = ComicTheme.dimension.margin / 2
            )
        ) {
            Text(
                text = stringResource(id = R.string.favorite_add_title),
                style = ComicTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = onDismissRequest) {
                Icon(imageVector = ComicIcons.Close, contentDescription = null)
            }
        }
        HorizontalDivider()
        Box {
            LazyColumn(
                state = lazyListState,
                contentPadding = WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom)
                    .asPaddingValues()
                    .add(
                        paddingValues = PaddingValues(
                            bottom = ButtonDefaults.MinHeight + ComicTheme.dimension.margin * 2
                        )
                    ),
                modifier = Modifier
                    .drawVerticalScrollbar(lazyListState)
            ) {
                items(
                    count = lazyPagingItems.itemCount,
                    key = lazyPagingItems.itemKey { it.id.value }
                ) {
                    val item = lazyPagingItems[it]
                    if (item != null) {
                        FavoriteItem(
                            favorite = item,
                            onClick = { onClick(item) },
                            color = ListItemDefaults.colors(containerColor = Color.Transparent)
                        )
                    }
                }
            }
            Button(
                onClick = onNewFavoriteClick,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom))
                    .padding(
                        end = ComicTheme.dimension.margin,
                        bottom = ComicTheme.dimension.margin
                    )
            ) {
                Icon(imageVector = ComicIcons.Add, contentDescription = null)
                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                Text(text = stringResource(id = R.string.favorite_add_btn_add))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
private fun PreviewFavoriteAddDialogScreen() {
    val list = List(20) {
        Favorite(id = FavoriteId(), name = "お気に入り$it", count = it * 5, exist = true)
    }
    val lazyPagingItems = flowOf(PagingData.from(list)).collectAsLazyPagingItems()
    FavoriteAddDialogScreen(
        lazyPagingItems = lazyPagingItems,
        onDismissRequest = {},
        onClick = {},
        onNewFavoriteClick = {}
    )
}
