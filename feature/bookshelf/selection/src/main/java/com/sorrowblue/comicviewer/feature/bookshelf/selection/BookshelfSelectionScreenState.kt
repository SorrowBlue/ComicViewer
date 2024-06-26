package com.sorrowblue.comicviewer.feature.bookshelf.selection

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.window.core.layout.WindowSizeClass

internal interface BookshelfSelectionScreenState {
    val uiState: BookshelfSelectionScreenUiState
    val windowSizeClass: WindowSizeClass
}

@Composable
internal fun rememberBookshelfSelectionScreenState(
    windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass,
): BookshelfSelectionScreenState = BookshelfSelectionScreenStateImpl(
    windowSizeClass = windowSizeClass
)

private class BookshelfSelectionScreenStateImpl(
    override val windowSizeClass: WindowSizeClass,
) : BookshelfSelectionScreenState {

    override val uiState by mutableStateOf(BookshelfSelectionScreenUiState())
}
