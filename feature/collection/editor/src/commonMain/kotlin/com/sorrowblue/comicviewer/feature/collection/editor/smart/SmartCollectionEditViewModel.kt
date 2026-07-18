/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.collection.editor.smart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.EmptyRequest
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.collection.SmartCollection
import com.sorrowblue.comicviewer.domain.model.dataOrNull
import com.sorrowblue.comicviewer.domain.model.fold
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.FlowBookshelfListUseCase
import com.sorrowblue.comicviewer.domain.usecase.collection.GetCollectionUseCase
import com.sorrowblue.comicviewer.domain.usecase.collection.UpdateCollectionUseCase
import com.sorrowblue.comicviewer.feature.collection.editor.smart.section.SmartCollectionForm
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactoryKey
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch

@AssistedInject
internal class SmartCollectionEditViewModel(
    @Assisted private val collectionId: CollectionId,
    flowBookshelfListUseCase: FlowBookshelfListUseCase,
    getCollectionUseCase: GetCollectionUseCase,
    private val updateCollectionUseCase: UpdateCollectionUseCase,
) : ViewModel() {

    val event: SharedFlow<SmartCollectionEditViewModelEvent>
        field = MutableSharedFlow()

    val bookshelfListFlow = flowBookshelfListUseCase(EmptyRequest).map {
        it.dataOrNull()
    }.shareIn(viewModelScope, SharingStarted.Lazily, 1)

    val collectionFlow = getCollectionUseCase(GetCollectionUseCase.Request(collectionId))
        .map { it.dataOrNull() as? SmartCollection }
        .shareIn(viewModelScope, SharingStarted.Lazily, 1)

    fun submit(formData: SmartCollectionForm) {
        viewModelScope.launch {
            val collection = collectionFlow.first() ?: return@launch
            updateCollectionUseCase(
                UpdateCollectionUseCase.Request(
                    collection.copy(
                        name = formData.name,
                        bookshelfId = if (formData.bookshelfId == BookshelfId()) {
                            null
                        } else {
                            formData.bookshelfId
                        },
                        searchCondition = formData.searchCondition,
                    ),
                ),
            ).fold(
                onSuccess = {
                    event.emit(SmartCollectionEditViewModelEvent.Complete)
                },
                onError = {},
            )
        }
    }

    @AssistedFactory
    @ManualViewModelAssistedFactoryKey
    @ContributesIntoMap(AppScope::class)
    interface Factory : ManualViewModelAssistedFactory {
        fun create(collectionId: CollectionId): SmartCollectionEditViewModel
    }
}

internal sealed interface SmartCollectionEditViewModelEvent {
    data object Complete : SmartCollectionEditViewModelEvent
}
