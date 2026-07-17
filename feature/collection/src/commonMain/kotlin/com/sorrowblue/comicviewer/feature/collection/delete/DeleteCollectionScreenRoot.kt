/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.collection.delete

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId

@Composable
internal fun DeleteCollectionScreenRoot(
    id: CollectionId,
    onBackClick: () -> Unit,
    onComplete: () -> Unit,
) {
    val state = rememberDeleteCollectionScreenState(id)
    DeleteCollectionScreen(
        uiState = state.uiState,
        onBackClick = onBackClick,
        onConfirm = { state.delete(onComplete) },
        modifier = Modifier.testTag("DeleteCollectionScreenRoot"),
    )
}
