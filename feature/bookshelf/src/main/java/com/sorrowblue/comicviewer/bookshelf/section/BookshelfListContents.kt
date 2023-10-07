package com.sorrowblue.comicviewer.bookshelf.section

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
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
    lazyGridState: LazyGridState,
    innerPadding: PaddingValues,
    lazyPagingItems: LazyPagingItems<BookshelfFolder>,
    onBookshelfClick: (BookshelfId, String) -> Unit,
    onBookshelfLongClick: (BookshelfFolder) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(300.dp),
        state = lazyGridState,
        contentPadding = innerPadding,
        verticalArrangement = Arrangement.spacedBy(ComicTheme.dimension.padding * 2),
        horizontalArrangement = Arrangement.spacedBy(ComicTheme.dimension.padding * 2)
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
                    onLongClick = { onBookshelfLongClick(item) },
                )
            }
        }
    }
}
