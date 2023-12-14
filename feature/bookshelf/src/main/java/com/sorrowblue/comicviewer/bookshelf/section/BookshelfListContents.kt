package com.sorrowblue.comicviewer.bookshelf.section

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
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
import com.sorrowblue.comicviewer.framework.designsystem.theme.LocalWindowSize

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
