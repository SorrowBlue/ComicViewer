/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.collection.editor.basic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.collection.BasicCollection
import com.sorrowblue.comicviewer.domain.model.collection.Collection
import com.sorrowblue.comicviewer.domain.model.collection.CollectionFile
import com.sorrowblue.comicviewer.domain.model.fold
import com.sorrowblue.comicviewer.domain.usecase.collection.AddCollectionFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.collection.CreateCollectionUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactoryKey
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import logcat.LogPriority
import logcat.logcat

@AssistedInject
internal class BasicCollectionCreateViewModel(
    @Assisted private val bookshelfId: BookshelfId,
    @Assisted private val path: String,
    private val createCollectionUseCase: CreateCollectionUseCase,
    private val addCollectionFileUseCase: AddCollectionFileUseCase,
) : ViewModel() {

    val event: SharedFlow<BasicCollectionCreateViewModelEvent>
        field = MutableSharedFlow()

    fun submitForm(formData: BasicCollectionForm) {
        viewModelScope.launch {
            createCollectionUseCase(CreateCollectionUseCase.Request(BasicCollection(formData.name)))
                .fold(
                    onSuccess = { collection ->
                        if (bookshelfId != BookshelfId() && path.isNotEmpty()) {
                            addCollectionFile(
                                collection = collection,
                                bookshelfId = bookshelfId,
                                path = path,
                            )
                        } else {
                            event.emit(
                                BasicCollectionCreateViewModelEvent.CreateSuccess(collection.name),
                            )
                        }
                    },
                    onError = {
                        this@BasicCollectionCreateViewModel.logcat(
                            LogPriority.ERROR,
                        ) { "#submitForm error: $it" }
                    },
                )
        }
    }

    private suspend fun addCollectionFile(
        collection: Collection,
        bookshelfId: BookshelfId,
        path: String,
    ) {
        addCollectionFileUseCase(
            AddCollectionFileUseCase.Request(CollectionFile(collection.id, bookshelfId, path)),
        ).fold(
            onSuccess = {
                event.emit(BasicCollectionCreateViewModelEvent.CreateAddSuccess(collection.name))
            },
            onError = {
                this@BasicCollectionCreateViewModel.logcat(
                    LogPriority.ERROR,
                ) { "#addCollectionFile error: $it" }
            },
        )
    }

    @AssistedFactory
    @ManualViewModelAssistedFactoryKey
    @ContributesIntoMap(AppScope::class)
    interface Factory : ManualViewModelAssistedFactory {
        fun create(bookshelfId: BookshelfId, path: String): BasicCollectionCreateViewModel
    }
}

internal sealed interface BasicCollectionCreateViewModelEvent {
    data class CreateSuccess(val name: String) : BasicCollectionCreateViewModelEvent
    data class CreateAddSuccess(val name: String) : BasicCollectionCreateViewModelEvent
}
