package com.sorrowblue.comicviewer.feature.bookshelf.section

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.feature.bookshelf.component.BookshelfListItem
import com.sorrowblue.comicviewer.framework.ui.layout.asWindowInsets
import com.sorrowblue.comicviewer.framework.ui.layout.union
import com.sorrowblue.comicviewer.framework.ui.paging.LazyPagingColumn
import com.sorrowblue.comicviewer.framework.ui.paging.LazyPagingColumnType
import com.sorrowblue.comicviewer.framework.ui.paging.LazyPagingItems
import com.sorrowblue.comicviewer.framework.ui.scrollbar.ScrollbarBox

@Composable
internal fun BookshelfGridList(
    lazyPagingItems: LazyPagingItems<BookshelfFolder>,
    lazyGridState: LazyGridState,
    onBookshelfClick: (BookshelfId, String) -> Unit,
    onBookshelfInfoClick: (BookshelfFolder) -> Unit,
    contentPadding: PaddingValues,
) {
    ScrollbarBox(
        state = lazyGridState,
        scrollbarWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Vertical + WindowInsetsSides.End)
            union contentPadding.asWindowInsets().only(WindowInsetsSides.Top)
    ) {
        LazyPagingColumn(
            autoPadding = false,
            contentPadding = contentPadding,
            lazyPagingItems = lazyPagingItems,
            state = lazyGridState,
            type = LazyPagingColumnType.Grid(400),
            modifier = Modifier.fillMaxSize()
        ) { _, item ->
            BookshelfListItem(
                bookshelfFolder = item,
                onClick = { onBookshelfClick(item.bookshelf.id, item.folder.path) },
                onInfoClick = { onBookshelfInfoClick(item) },
                modifier = Modifier.animateItem()
            )
        }
    }
}
