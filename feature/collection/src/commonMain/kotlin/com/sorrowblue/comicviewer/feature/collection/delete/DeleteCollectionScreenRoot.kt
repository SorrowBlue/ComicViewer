package com.sorrowblue.comicviewer.feature.collection.delete

import androidx.compose.runtime.Composable
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId

@Composable
context(context: DeleteCollectionScreenContext)
fun DeleteCollectionScreenRoot(id: CollectionId, onBackClick: () -> Unit, onComplete: () -> Unit) {
    val state = rememberDeleteCollectionScreenState(id)
    DeleteCollectionScreen(
        uiState = state.uiState,
        onBackClick = onBackClick,
        onConfirm = { state.delete(onComplete) },
    )
}
