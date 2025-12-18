package com.sorrowblue.comicviewer.feature.book.receive

import androidx.compose.runtime.Composable
import com.sorrowblue.comicviewer.feature.book.BookScreen
import com.sorrowblue.comicviewer.feature.book.BookScreenUiState

@Composable
context(context: ReceiveBookScreenContext)
fun ReceiveBookScreenRoot(uri: String, onBackClick: () -> Unit) {
    val state: ReceiveBookScreenState = rememberReceiveBookScreenState(uri = uri)
    if (state.uiState is BookScreenUiState.Loaded) {
        val uiState = state.uiState as BookScreenUiState.Loaded
        BookScreen(
            uiState = uiState,
            pagerState = state.pagerState,
            currentList = state.currentList,
            onBackClick = onBackClick,
            onNextBookClick = { },
            onContainerClick = state::toggleTooltip,
            onContainerLongClick = {},
            onPageChange = state::onPageChange,
            onSettingsClick = {},
            onPageLoad = state::onPageLoaded,
        )
    }
}
