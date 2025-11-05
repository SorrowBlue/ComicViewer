package com.sorrowblue.comicviewer.feature.collection.add

import androidx.compose.runtime.Composable
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.PathString

@Composable
context(context: BasicCollectionAddScreenContext)
fun BasicCollectionAddScreenRoot(
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
        }
    )
}
