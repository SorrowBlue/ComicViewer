package com.sorrowblue.comicviewer.feature.bookshelf.section

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.compose.LazyPagingItems
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.feature.bookshelf.component.BookshelfListItem
import com.sorrowblue.comicviewer.framework.ui.paging.LazyPagingColumn

@Composable
internal fun BookshelfGridList(
    lazyPagingItems: LazyPagingItems<BookshelfFolder>,
    lazyGridState: LazyGridState,
    onBookshelfClick: (BookshelfId, String) -> Unit,
    onBookshelfInfoClick: (BookshelfFolder) -> Unit,
    contentPadding: PaddingValues,
) {
    LazyPagingColumn(
        contentPadding = contentPadding,
        lazyPagingItems = lazyPagingItems,
        state = lazyGridState,
        type = LazyPagingColumn.Grid(400),
        modifier = Modifier.fillMaxSize(),
    ) { _, item ->
        BookshelfListItem(
            bookshelfFolder = item,
            onClick = { onBookshelfClick(item.bookshelf.id, item.folder.path) },
            onInfoClick = { onBookshelfInfoClick(item) },
            modifier = Modifier.animateItem(),
        )
    }
}
