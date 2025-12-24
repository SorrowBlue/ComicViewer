package com.sorrowblue.comicviewer.feature.bookshelf.edit

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

@Composable
internal fun BookshelfDiscardConfirmScreenRoot(
    onBackClick: () -> Unit,
    onDiscard: () -> Unit,
    onKeep: () -> Unit,
) {
    BookshelfDiscardConfirmScreen(
        onBackClick = onBackClick,
        onDiscard = onDiscard,
        onKeep = onKeep,
        modifier = Modifier.testTag("BookshelfDiscardConfirmScreenRoot"),
    )
}
