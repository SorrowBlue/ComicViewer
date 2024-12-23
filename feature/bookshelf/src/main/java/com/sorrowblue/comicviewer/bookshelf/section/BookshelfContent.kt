package com.sorrowblue.comicviewer.bookshelf.section

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.sorrowblue.comicviewer.bookshelf.component.BookshelfListItem
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.feature.bookshelf.R
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.undraw.UndrawBookshelves
import com.sorrowblue.comicviewer.framework.ui.EmptyContent
import com.sorrowblue.comicviewer.framework.ui.adaptive.LazyPagingColumn
import com.sorrowblue.comicviewer.framework.ui.adaptive.LazyPagingColumnType
import com.sorrowblue.comicviewer.framework.ui.adaptive.animateMainContentPaddingValues
import com.sorrowblue.comicviewer.framework.ui.adaptive.union
import com.sorrowblue.comicviewer.framework.ui.add
import com.sorrowblue.comicviewer.framework.ui.asWindowInsets
import com.sorrowblue.comicviewer.framework.ui.paging.isEmptyData
import com.sorrowblue.comicviewer.framework.ui.scrollbar.ScrollbarBox

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
    val addPadding by animateMainContentPaddingValues(false)
    ScrollbarBox(
        state = lazyGridState,
        itemsAvailable = lazyPagingItems.itemCount,
        scrollbarWindowInsets =
        WindowInsets.safeDrawing.only(WindowInsetsSides.Vertical + WindowInsetsSides.End)
            union contentPadding.asWindowInsets().only(WindowInsetsSides.Top)
    ) {
        LazyPagingColumn(
            contentPadding = contentPadding.add(addPadding),
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
