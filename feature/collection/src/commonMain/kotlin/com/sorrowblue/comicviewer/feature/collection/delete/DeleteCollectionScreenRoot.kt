/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.collection.delete

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.dropUnlessResumed
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel

@Composable
internal fun DeleteCollectionScreenRoot(
    id: CollectionId,
    onBackClick: () -> Unit,
    onComplete: () -> Unit,
) {
    val viewModel =
        assistedMetroViewModel<DeleteCollectionViewModel, DeleteCollectionViewModel.Factory> {
            create(id)
        }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    DeleteCollectionScreen(
        uiState = uiState,
        onBackClick = dropUnlessResumed(block = onBackClick),
        onConfirm = { viewModel.delete(onComplete) },
        modifier = Modifier.testTag("DeleteCollectionScreenRoot"),
    )
}
