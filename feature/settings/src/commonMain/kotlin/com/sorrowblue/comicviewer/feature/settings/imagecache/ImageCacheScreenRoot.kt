package com.sorrowblue.comicviewer.feature.settings.imagecache

import androidx.compose.runtime.Composable

@Composable
context(context: ImageCacheScreenContext)
fun ImageCacheScreenRoot(onBackClick: () -> Unit) {
    val state = rememberImageCacheScreenState()
    ImageCacheScreen(
        uiState = state.uiState,
        snackbarHostState = state.snackbarHostState,
        onBackClick = onBackClick,
        onClick = state::onClick,
    )
}
