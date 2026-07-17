/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.book

import androidx.compose.runtime.Composable
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.file.Book as BookFile
import com.sorrowblue.comicviewer.feature.book.wrapper.BookScreenWrapper

@Composable
internal fun BookScreenRoot(
    bookshelfId: BookshelfId,
    path: String,
    name: String,
    collectionId: CollectionId,
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onNextBookClick: (BookFile, CollectionId) -> Unit,
    onContainerLongClick: () -> Unit,
) {
    BookScreenWrapper(
        bookshelfId = bookshelfId,
        path = path,
        name = name,
        collectionId = collectionId,
        onBackClick = onBackClick,
    ) { uiState ->
        val state = rememberBookScreenState(initialUiState = uiState)
        BookScreen(
            uiState = state.uiState,
            pagerState = state.pagerState,
            currentList = state.currentList,
            onBackClick = onBackClick,
            onNextBookClick = { onNextBookClick(it, collectionId) },
            onContainerClick = state::toggleTooltip,
            onContainerLongClick = onContainerLongClick,
            onPageChange = state::onPageChange,
            onSettingsClick = onSettingsClick,
            onPageLoad = state::onPageLoad,
        )
    }
}
