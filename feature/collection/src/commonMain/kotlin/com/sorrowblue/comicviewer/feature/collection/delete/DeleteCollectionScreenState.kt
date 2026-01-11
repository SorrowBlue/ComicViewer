package com.sorrowblue.comicviewer.feature.collection.delete

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.collection.Collection
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.usecase.collection.DeleteCollectionUseCase
import com.sorrowblue.comicviewer.domain.usecase.collection.GetCollectionUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

internal interface DeleteCollectionScreenState {
    val uiState: DeleteCollectionScreenUiState

    fun delete(onComplete: () -> Unit)
}

@Composable
context(context: DeleteCollectionScreenContext)
internal fun rememberDeleteCollectionScreenState(id: CollectionId): DeleteCollectionScreenState {
    val coroutineScope = rememberCoroutineScope()
    return remember(id) {
        DeleteCollectionScreenStateImpl(
            id = id,
            coroutineScope = coroutineScope,
            deleteCollectionUseCase = context.deleteCollectionUseCase,
            getCollectionUseCase = context.getCollectionUseCase,
        )
    }
}

private class DeleteCollectionScreenStateImpl(
    private val id: CollectionId,
    private val coroutineScope: CoroutineScope,
    getCollectionUseCase: GetCollectionUseCase,
    private val deleteCollectionUseCase: DeleteCollectionUseCase,
) : DeleteCollectionScreenState {
    override var uiState by mutableStateOf(DeleteCollectionScreenUiState())

    init {
        coroutineScope.launch {
            getCollectionUseCase(GetCollectionUseCase.Request(id))
                .first().let {
                    when (it) {
                        is Resource.Success<Collection> -> {
                            uiState = uiState.copy(name = it.data.name)
                        }

                        is Resource.Error<GetCollectionUseCase.Error> -> {
                            // TODO()
                        }
                    }
                }
        }
    }

    override fun delete(onComplete: () -> Unit) {
        coroutineScope.launch {
            deleteCollectionUseCase(DeleteCollectionUseCase.Request(id))
            onComplete()
        }
    }
}
