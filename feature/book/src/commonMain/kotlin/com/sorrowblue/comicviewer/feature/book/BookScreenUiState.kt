package com.sorrowblue.comicviewer.feature.book

import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.feature.book.section.BookSheetUiState

internal sealed interface BookScreenUiState {
    data class Loading(val name: String) : BookScreenUiState

    data class Error(val name: String) : BookScreenUiState

    data class PluginError(val name: String, val error: String) : BookScreenUiState

    data class Loaded(
        val book: Book,
        val collectionId: CollectionId,
        val bookSheetUiState: BookSheetUiState,
        val isVisibleTooltip: Boolean = true,
    ) : BookScreenUiState
}
