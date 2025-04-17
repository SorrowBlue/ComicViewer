package com.sorrowblue.comicviewer.feature.bookshelf.selection

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

internal interface BookshelfSelectionScreenState {
    val uiState: BookshelfSelectionScreenUiState
}

@Composable
internal fun rememberBookshelfSelectionScreenState(): BookshelfSelectionScreenState = remember {
    BookshelfSelectionScreenStateImpl()
}

private class BookshelfSelectionScreenStateImpl : BookshelfSelectionScreenState {

    override val uiState by mutableStateOf(BookshelfSelectionScreenUiState())
}
