package com.sorrowblue.comicviewer.feature.bookshelf.selection

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf

internal interface BookshelfSelectionScreenState {
    val uiState: BookshelfSelectionScreenUiState
    val windowSizeClass: WindowSizeClass
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
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
