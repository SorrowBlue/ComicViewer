package com.sorrowblue.comicviewer.bookshelf.section

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.sorrowblue.comicviewer.bookshelf.component.BookshelfCard
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.feature.bookshelf.R
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.undraw.UndrawBookshelves
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.ui.EmptyContent
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.LocalNavigationState
import com.sorrowblue.comicviewer.framework.ui.adaptive.navigation.NavigationState
import com.sorrowblue.comicviewer.framework.ui.material3.drawVerticalScrollbar
import com.sorrowblue.comicviewer.framework.ui.paging.isEmptyData

@Composable
internal fun BookshelfMainSheet(
    lazyPagingItems: LazyPagingItems<BookshelfFolder>,
    lazyGridState: LazyGridState,
    onBookshelfClick: (BookshelfId, String) -> Unit,
    onBookshelfInfoClick: (BookshelfFolder) -> Unit,
    contentPadding: PaddingValues,
) {
    Box {
        if (lazyPagingItems.isEmptyData) {
            EmptyContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
                imageVector = ComicIcons.UndrawBookshelves,
                text = stringResource(id = R.string.bookshelf_list_message_no_bookshelves_added_yet)
            )
        } else {
            BookshelfListContents(
                lazyGridState = lazyGridState,
                lazyPagingItems = lazyPagingItems,
                onBookshelfClick = onBookshelfClick,
                onBookshelfInfoClick = onBookshelfInfoClick,
                contentPadding = contentPadding
            )
        }
        if (lazyPagingItems.loadState.refresh is LoadState.Loading) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = contentPadding.calculateTopPadding())
            )
        }
    }
}

@Composable
private fun BookshelfListContents(
    lazyGridState: LazyGridState,
    lazyPagingItems: LazyPagingItems<BookshelfFolder>,
    onBookshelfClick: (BookshelfId, String) -> Unit,
    onBookshelfInfoClick: (BookshelfFolder) -> Unit,
    contentPadding: PaddingValues,
) {
    val navigationState = LocalNavigationState.current
    val gridCells by remember(navigationState) {
        mutableStateOf(
            if (navigationState is NavigationState.NavigationBar) {
                GridCells.Fixed(1)
            } else {
                GridCells.Adaptive(280.dp)
            }
        )
    }
    var spanCount by remember { mutableIntStateOf(1) }
    LazyVerticalGrid(
        columns = gridCells,
        state = lazyGridState,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(
            ComicTheme.dimension.minPadding * 2,
            alignment = Alignment.Top
        ),
        horizontalArrangement = Arrangement.spacedBy(
            ComicTheme.dimension.minPadding * 2,
            alignment = Alignment.Start
        ),
        modifier = Modifier
            .fillMaxSize()
            .drawVerticalScrollbar(lazyGridState, spanCount)
    ) {
        items(
            count = lazyPagingItems.itemCount,
            span = {
                spanCount = maxLineSpan
                GridItemSpan(1)
            },
            key = lazyPagingItems.itemKey { it.bookshelf.id.value }
        ) {
            lazyPagingItems[it]?.let { item ->
                BookshelfCard(
                    state = navigationState,
                    bookshelfFolder = item,
                    onClick = { onBookshelfClick(item.bookshelf.id, item.folder.path) },
                    onInfoClick = { onBookshelfInfoClick(item) },
                )
            }
        }
    }
}
