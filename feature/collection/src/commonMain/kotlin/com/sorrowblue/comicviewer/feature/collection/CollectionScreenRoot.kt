package com.sorrowblue.comicviewer.feature.collection

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.sorrowblue.comicviewer.domain.model.collection.Collection
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.file.File

@Composable
context(context: CollectionScreenContext)
internal fun CollectionScreenRoot(
    id: CollectionId,
    onBackClick: () -> Unit,
    onFileClick: (File) -> Unit,
    onFileInfoClick: (File) -> Unit,
    onEditClick: (Collection) -> Unit,
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
        onEditClick = { onEditClick(state.collection) },
        onSettingsClick = onSettingsClick,
        onFileClick = onFileClick,
        onFileInfoClick = onFileInfoClick,
        modifier = Modifier.testTag("CollectionScreenRoot"),
    )
}
