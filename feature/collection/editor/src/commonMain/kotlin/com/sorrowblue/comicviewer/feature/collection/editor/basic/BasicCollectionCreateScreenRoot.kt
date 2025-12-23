package com.sorrowblue.comicviewer.feature.collection.editor.basic

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
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
        modifier = Modifier.testTag("BasicCollectionCreateScreenRoot"),
    )

    EventEffect(state.event) {
        when (it) {
            BasicCollectionCreateScreenStateEvent.CreateComplete -> onComplete()
        }
    }
}
