package com.sorrowblue.comicviewer.feature.bookshelf.info.delete

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId

@Composable
context(context: BookshelfDeleteScreenContext)
internal fun BookshelfDeleteScreenRoot(
    bookshelfId: BookshelfId,
    onBackClick: () -> Unit,
    onComplete: () -> Unit,
) {
    val state = rememberBookshelfDeleteScreenState(
        bookshelfId = bookshelfId,
        getBookshelfInfoUseCase = context.getBookshelfInfoUseCase,
        updateDeletionFlagUseCase = context.updateDeletionFlagUseCase,
    )
    BookshelfDeleteScreen(
        uiState = state.uiState,
        onDismissRequest = onBackClick,
        onDismissClick = onBackClick,
        onConfirmClick = { state.onConfirmClick(onComplete) },
        modifier = Modifier.testTag("BookshelfDeleteScreenRoot"),
    )
}
