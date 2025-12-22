package com.sorrowblue.comicviewer.feature.settings.imagecache

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

@Composable
context(context: ImageCacheScreenContext)
internal fun ImageCacheScreenRoot(onBackClick: () -> Unit) {
    val state = rememberImageCacheScreenState()
    ImageCacheScreen(
        uiState = state.uiState,
        snackbarHostState = state.snackbarHostState,
        onBackClick = onBackClick,
        onClick = state::onClick,
        modifier = Modifier.testTag("ImageCacheSettingsRoot")
    )
}
