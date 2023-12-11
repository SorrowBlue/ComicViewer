package com.sorrowblue.comicviewer.bookshelf.section

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.sorrowblue.comicviewer.bookshelf.component.Bookshelf
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme

@Composable
internal fun BookshelfListContents(
    lazyGridState: LazyStaggeredGridState,
    innerPadding: PaddingValues,
    lazyPagingItems: LazyPagingItems<BookshelfFolder>,
    onBookshelfClick: (BookshelfId, String) -> Unit,
    onBookshelfInfoClick: (BookshelfFolder) -> Unit,
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Adaptive(180.dp),
        state = lazyGridState,
        contentPadding = innerPadding,
        verticalItemSpacing = ComicTheme.dimension.padding * 2,
        horizontalArrangement = Arrangement.spacedBy(
            ComicTheme.dimension.padding * 2,
            alignment = Alignment.Start
        ),
        modifier = Modifier.fillMaxSize()
    ) {
        items(
            count = lazyPagingItems.itemCount,
            key = lazyPagingItems.itemKey { it.bookshelf.id.value },
            contentType = { lazyPagingItems.itemContentType { "contentType" } }
        ) {
            val item = lazyPagingItems[it]
            if (item != null) {
                Bookshelf(
                    bookshelfFolder = item,
                    onClick = { onBookshelfClick(item.bookshelf.id, item.folder.path) },
                    onInfoClick = { onBookshelfInfoClick(item) },
                )
            }
        }
    }
}
