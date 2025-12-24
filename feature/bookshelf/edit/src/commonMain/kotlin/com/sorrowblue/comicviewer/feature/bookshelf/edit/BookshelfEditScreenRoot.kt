package com.sorrowblue.comicviewer.feature.bookshelf.edit

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

@Composable
context(context: BookshelfEditScreenContext)
internal fun BookshelfEditScreenRoot(
    type: BookshelfEditType,
    onBackClick: () -> Unit,
    discardConfirm: () -> Unit,
    onEditComplete: () -> Unit,
) {
    val state = rememberBookshelfEditScreenState(editType = type)
    BookshelfEditScreen(
        state = state,
        onBackClick = onBackClick,
        discardConfirm = discardConfirm,
        onEditComplete = onEditComplete,
        modifier = Modifier.testTag("BookshelfEditScreenRoot"),
    )
}
