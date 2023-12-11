package com.sorrowblue.comicviewer.bookshelf.section

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.framework.ui.paging.isEmptyData

@Composable
internal fun BookshelfMainSheet(
    lazyPagingItems: LazyPagingItems<BookshelfFolder>,
    innerPadding: PaddingValues,
    lazyGridState: LazyStaggeredGridState,
    onBookshelfClick: (BookshelfId, String) -> Unit,
    onBookshelfInfoClick: (BookshelfFolder) -> Unit,
) {
    if (lazyPagingItems.isEmptyData) {
        BookshelfEmptyContents(innerPadding = innerPadding)
    } else {
        BookshelfListContents(
            lazyGridState = lazyGridState,
            innerPadding = innerPadding,
            lazyPagingItems = lazyPagingItems,
            onBookshelfClick = onBookshelfClick,
            onBookshelfInfoClick = onBookshelfInfoClick
        )
    }
    if (lazyPagingItems.loadState.refresh is LoadState.Loading) {
        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = innerPadding.calculateTopPadding())
        )
    }
}
