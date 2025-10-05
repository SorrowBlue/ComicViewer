package com.sorrowblue.comicviewer.feature.bookshelf.section

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.framework.designsystem.icon.ComicIcons
import com.sorrowblue.comicviewer.framework.designsystem.icon.undraw.UndrawBookshelves
import com.sorrowblue.comicviewer.framework.ui.EmptyContent
import com.sorrowblue.comicviewer.framework.ui.paging.isEmptyData
import comicviewer.feature.bookshelf.generated.resources.Res
import comicviewer.feature.bookshelf.generated.resources.bookshelf_label_no_bookshelf
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun BookshelfSheet(
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
                text = stringResource(Res.string.bookshelf_label_no_bookshelf)
            )
        } else {
            BookshelfGridList(
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
