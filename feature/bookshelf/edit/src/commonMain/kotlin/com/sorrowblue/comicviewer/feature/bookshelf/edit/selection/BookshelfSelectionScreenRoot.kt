package com.sorrowblue.comicviewer.feature.bookshelf.edit.selection

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfType

@Composable
internal fun BookshelfSelectionScreenRoot(
    onBackClick: () -> Unit,
    onTypeClick: (BookshelfType) -> Unit,
) {
    BookshelfSelectionScreen(
        onBackClick = onBackClick,
        onTypeClick = onTypeClick,
        modifier = Modifier.testTag("BookshelfSelectionScreenRoot"),
    )
}
