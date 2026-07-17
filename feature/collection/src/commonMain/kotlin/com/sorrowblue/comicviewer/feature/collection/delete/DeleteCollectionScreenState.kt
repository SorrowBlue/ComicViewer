/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.collection.delete

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.sorrowblue.comicviewer.domain.model.collection.Collection
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

internal interface DeleteCollectionScreenState {
    val uiState: DeleteCollectionScreenUiState

    fun delete(onComplete: () -> Unit)
}

@Composable
internal fun rememberDeleteCollectionScreenState(
    id: CollectionId,
    viewModel: DeleteCollectionViewModel =
        assistedMetroViewModel<DeleteCollectionViewModel, DeleteCollectionViewModel.Factory> {
            create(id)
        },
): DeleteCollectionScreenState {
    val coroutineScope = rememberCoroutineScope()
    return remember(coroutineScope) {
        DeleteCollectionScreenStateImpl(
            coroutineScope = coroutineScope,
            collectionFlow = viewModel.collectionFlow,
            deleteCollection = viewModel::delete,
        )
    }
}

private class DeleteCollectionScreenStateImpl(
    coroutineScope: CoroutineScope,
    collectionFlow: SharedFlow<Collection>,
    private val deleteCollection: (() -> Unit) -> Unit,
) : DeleteCollectionScreenState {

    override var uiState by mutableStateOf(DeleteCollectionScreenUiState())

    init {
        collectionFlow.onEach {
            uiState = uiState.copy(name = it.name)
        }.launchIn(coroutineScope)
    }

    override fun delete(onComplete: () -> Unit) {
        deleteCollection(onComplete)
    }
}
