package com.sorrowblue.comicviewer.feature.collection.delete

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.usecase.collection.DeleteCollectionUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

internal interface DeleteCollectionScreenState {
    fun delete(id: CollectionId, onComplete: () -> Unit)
}

@Composable
internal fun rememberDeleteCollectionScreenState(
    deleteCollectionUseCase: DeleteCollectionUseCase = koinInject(),
): DeleteCollectionScreenState = remember {
    DeleteCollectionScreenStateImpl(
        deleteCollectionUseCase = deleteCollectionUseCase
    )
}.apply {
    scope = rememberCoroutineScope()
}

private class DeleteCollectionScreenStateImpl(
    private val deleteCollectionUseCase: DeleteCollectionUseCase,
) : DeleteCollectionScreenState {

    lateinit var scope: CoroutineScope

    override fun delete(id: CollectionId, onComplete: () -> Unit) {
        scope.launch {
            deleteCollectionUseCase(DeleteCollectionUseCase.Request(id))
            onComplete()
        }
    }
}
