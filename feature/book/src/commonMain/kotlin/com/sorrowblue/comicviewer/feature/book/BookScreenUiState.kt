/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.book

import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.feature.book.section.BookSheetUiState

internal sealed interface BookScreenUiState {
    data class Loading(val name: String) : BookScreenUiState

    data class Error(val name: String) : BookScreenUiState

    data class Loaded(
        val book: Book,
        val collectionId: CollectionId,
        val bookSheetUiState: BookSheetUiState,
        val isVisibleTooltip: Boolean = true,
        val alwaysOpenFromFirstPage: Boolean,
    ) : BookScreenUiState
}
