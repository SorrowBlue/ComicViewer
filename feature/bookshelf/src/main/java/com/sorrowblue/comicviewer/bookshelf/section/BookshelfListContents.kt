package com.sorrowblue.comicviewer.bookshelf.section

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.sorrowblue.comicviewer.bookshelf.component.Bookshelf
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.framework.designsystem.theme.ComicTheme
import com.sorrowblue.comicviewer.framework.designsystem.theme.LocalWindowSize
import com.sorrowblue.comicviewer.framework.ui.material3.drawVerticalScrollbar

@Composable
internal fun BookshelfListContents(
    lazyGridState: LazyGridState,
    lazyPagingItems: LazyPagingItems<BookshelfFolder>,
    onBookshelfClick: (BookshelfId, String) -> Unit,
    onBookshelfInfoClick: (BookshelfFolder) -> Unit,
    contentPadding: PaddingValues,
) {
    val gridCells = if (LocalWindowSize.current.widthSizeClass == WindowWidthSizeClass.Compact) {
        GridCells.Fixed(1)
    } else {
        GridCells.Adaptive(200.dp)
    }
    var spanCount by remember { mutableIntStateOf(1) }
    LazyVerticalGrid(
        columns = gridCells,
        state = lazyGridState,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(
            ComicTheme.dimension.padding * 2,
            alignment = Alignment.Top
        ),
        horizontalArrangement = Arrangement.spacedBy(
            ComicTheme.dimension.padding * 2,
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
