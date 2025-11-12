package com.sorrowblue.comicviewer.feature.collection.editor.basic

import androidx.compose.runtime.Composable
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.framework.ui.EventEffect

@Composable
context(context: BasicCollectionEditScreenContext)
fun BasicCollectionEditScreenRoot(
    collectionId: CollectionId,
    onBackClick: () -> Unit,
    onComplete: () -> Unit,
) {
    val state = rememberBasicCollectionEditScreenState(collectionId)
    BasicCollectionEditScreen(
        uiState = state.uiState,
        form = state.form,
        lazyPagingItems = state.lazyPagingItems,
        onBackClick = onBackClick,
        onDeleteClick = state::onDeleteClick,
    )
    EventEffect(state.events) {
        when (it) {
            BasicCollectionEditScreenStateEvent.EditComplete -> onComplete()
        }
    }
}
