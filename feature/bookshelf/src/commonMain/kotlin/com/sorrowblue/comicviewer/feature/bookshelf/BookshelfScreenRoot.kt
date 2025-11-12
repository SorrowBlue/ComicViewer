package com.sorrowblue.comicviewer.feature.bookshelf

import androidx.compose.runtime.Composable
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.PathString

@Composable
context(context: BookshelfScreenContext)
fun BookshelfScreenRoot(
    onSettingsClick: () -> Unit,
    onFabClick: () -> Unit,
    onBookshelfClick: (BookshelfId, PathString) -> Unit,
    onBookshelfInfoClick: (BookshelfFolder) -> Unit,
) {
    val state = rememberBookshelfScreenState()
    state.scaffoldState.BookshelfScreen(
        lazyPagingItems = state.lazyPagingItems,
        lazyGridState = state.lazyGridState,
        onFabClick = onFabClick,
        onSettingsClick = onSettingsClick,
        onBookshelfClick = onBookshelfClick,
        onBookshelfInfoClick = onBookshelfInfoClick,
    )
}
