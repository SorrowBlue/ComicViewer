package com.sorrowblue.comicviewer.feature.readlater

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
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
        onClearAllClick = state::onClearAllClick,
        onSettingsClick = onSettingsClick,
        onFileClick = onFileClick,
        onFileInfoClick = onFileInfoClick,
        modifier = Modifier.testTag("ReadLaterScreenRoot"),
    )
}
