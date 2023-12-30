package com.sorrowblue.comicviewer.feature.bookshelf.selection

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf

@OptIn(ExperimentalMaterial3Api::class)
internal interface BookshelfSelectionScreenState {
    val uiState: BookshelfSelectionScreenUiState
    val windowSizeClass: WindowSizeClass
    val scrollBehavior: TopAppBarScrollBehavior
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
internal fun rememberBookshelfSelectionScreenState(
    windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass,
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
): BookshelfSelectionScreenState =
    BookshelfSelectionScreenStateImpl(
        windowSizeClass = windowSizeClass,
        scrollBehavior = scrollBehavior
    )

@OptIn(ExperimentalMaterial3Api::class)
private class BookshelfSelectionScreenStateImpl(
    override val windowSizeClass: WindowSizeClass,
    override val scrollBehavior: TopAppBarScrollBehavior,
) : BookshelfSelectionScreenState {

    override val uiState by mutableStateOf(BookshelfSelectionScreenUiState())
}
