package com.sorrowblue.comicviewer.feature.book.menu

import androidx.compose.runtime.Composable

@Composable
context(context: BookMenuScreenContext)
internal fun BookMenuScreenRoot(onDismissRequest: () -> Unit) {
    val state = rememberBookMenuScreenState()
    BookMenuScreen(
        uiState = state.uiState,
        onDismissRequest = onDismissRequest,
        onPageFormatChange = state::onPageFormatChange,
        onPageScaleChange = state::onPageScaleChange,
    )
}
