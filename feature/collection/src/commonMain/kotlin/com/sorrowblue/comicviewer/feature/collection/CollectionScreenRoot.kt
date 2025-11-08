package com.sorrowblue.comicviewer.feature.collection

import androidx.compose.runtime.Composable
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.file.File

@Composable
context(context: CollectionScreenContext)
fun CollectionScreenRoot(
    id: CollectionId,
    onBackClick: () -> Unit,
    onFileClick: (File) -> Unit,
    onFileInfoClick: (File) -> Unit,
    onEditClick: (CollectionId) -> Unit,
    onDeleteClick: (CollectionId) -> Unit,
    onSettingsClick: () -> Unit,
) {
    val state = rememberCollectionScreenState(id)
    state.scaffoldState.CollectionScreen(
        uiState = state.uiState,
        lazyPagingItems = state.lazyPagingItems,
        lazyGridState = state.lazyGridState,
        onBackClick = onBackClick,
        onDeleteClick = { onDeleteClick(id) },
        onEditClick = { onEditClick(id) },
        onSettingsClick = onSettingsClick,
        onFileClick = onFileClick,
        onFileInfoClick = onFileInfoClick,
    )
}
