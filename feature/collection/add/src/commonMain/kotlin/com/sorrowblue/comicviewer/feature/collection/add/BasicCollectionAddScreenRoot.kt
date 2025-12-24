package com.sorrowblue.comicviewer.feature.collection.add

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.PathString

@Composable
context(context: BasicCollectionAddScreenContext)
internal fun BasicCollectionAddScreenRoot(
    bookshelfId: BookshelfId,
    path: String,
    onBackClick: () -> Unit,
    onCollectionCreateClick: (BookshelfId, PathString) -> Unit,
) {
    val state = rememberBasicCollectionAddScreenState(bookshelfId, path)
    BasicCollectionAddScreen(
        uiState = state.uiState,
        lazyPagingItems = state.lazyPagingItems,
        lazyListState = state.lazyListState,
        onDismissRequest = onBackClick,
        onClick = state::onCollectionClick,
        onClickCollectionSort = state::onClickCollectionSort,
        onCollectionCreateClick = {
            onCollectionCreateClick(bookshelfId, path)
        },
        modifier = Modifier.testTag("BasicCollectionAddScreenRoot"),
    )
}
