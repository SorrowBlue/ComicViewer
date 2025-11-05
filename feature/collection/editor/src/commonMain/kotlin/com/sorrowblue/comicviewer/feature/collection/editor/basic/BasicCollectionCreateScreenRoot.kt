package com.sorrowblue.comicviewer.feature.collection.editor.basic

import androidx.compose.runtime.Composable
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.framework.ui.EventEffect

@Composable
context(context: BasicCollectionCreateScreenContext)
fun BasicCollectionCreateScreenRoot(
    bookshelfId: BookshelfId,
    path: String,
    onBackClick: () -> Unit,
    onComplete: () -> Unit,
) {
    val state = rememberBasicCollectionCreateScreenState(bookshelfId, path)
    BasicCollectionCreateScreen(
        uiState = state.uiState,
        form = state.form,
        onDismissRequest = onBackClick,
    )

    EventEffect(state.event) {
        when (it) {
            BasicCollectionCreateScreenStateEvent.CreateComplete -> onComplete()
        }
    }
}
