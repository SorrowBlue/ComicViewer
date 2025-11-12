package com.sorrowblue.comicviewer.feature.readlater

import androidx.compose.runtime.Composable
import com.sorrowblue.comicviewer.domain.model.file.File

@Composable
context(context: ReadLaterScreenContext)
fun ReadLaterScreenRoot(
    onSettingsClick: () -> Unit,
    onFileClick: (File) -> Unit,
    onFileInfoClick: (File) -> Unit,
) {
    val state = rememberReadLaterScreenState()
    state.scaffoldState.ReadLaterScreen(
        lazyPagingItems = state.lazyPagingItems,
        lazyGridState = state.lazyGridState,
        onSettingsClick = onSettingsClick,
        onClearAllClick = state::onClearAllClick,
        onFileClick = onFileClick,
        onFileInfoClick = onFileInfoClick,
    )
}
