package com.sorrowblue.comicviewer.feature.bookshelf

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.sorrowblue.comicviewer.domain.model.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.PathString

@Composable
context(context: BookshelfScreenContext)
internal fun BookshelfScreenRoot(
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
        modifier = Modifier.testTag("BookshelfScreenRoot"),
    )
}
