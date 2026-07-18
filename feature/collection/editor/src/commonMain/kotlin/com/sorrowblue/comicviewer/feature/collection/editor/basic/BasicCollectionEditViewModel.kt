/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.collection.editor.basic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.model.collection.BasicCollection
import com.sorrowblue.comicviewer.domain.model.collection.CollectionFile
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.dataOrNull
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.fold
import com.sorrowblue.comicviewer.domain.usecase.collection.GetCollectionUseCase
import com.sorrowblue.comicviewer.domain.usecase.collection.PagingCollectionFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.collection.RemoveCollectionFileUseCase
import com.sorrowblue.comicviewer.domain.usecase.collection.UpdateCollectionUseCase
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
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import logcat.logcat

@AssistedInject
internal class BasicCollectionEditViewModel(
    @Assisted private val collectionId: CollectionId,
    pagingCollectionFileUseCase: PagingCollectionFileUseCase,
    private val getCollectionUseCase: GetCollectionUseCase,
    private val updateCollectionUseCase: UpdateCollectionUseCase,
    private val removeCollectionFileUseCase: RemoveCollectionFileUseCase,
) : ViewModel() {

    val event: SharedFlow<BasicCollectionEditViewModelEvent>
        field = MutableSharedFlow()

    val pagingDataFlow = pagingCollectionFileUseCase(
        PagingCollectionFileUseCase.Request(
            pagingConfig = PagingConfig(PageSize),
            collectionId = collectionId,
        ),
    ).cachedIn(viewModelScope)

    val collectionFlow = getCollectionUseCase(GetCollectionUseCase.Request(collectionId))
        .mapNotNull { it.dataOrNull() as? BasicCollection }
        .shareIn(viewModelScope, SharingStarted.Eagerly, 1)

    fun onDeleteClick(file: File) {
        viewModelScope.launch {
            removeCollectionFileUseCase(
                RemoveCollectionFileUseCase.Request(
                    CollectionFile(
                        collectionId,
                        file.bookshelfId,
                        file.path,
                    ),
                ),
            )
        }
    }

    fun onSubmit(formData: BasicCollectionForm) {
        viewModelScope.launch {
            val collection = getCollectionUseCase(GetCollectionUseCase.Request(collectionId))
                .mapNotNull { it.dataOrNull() as? BasicCollection }
                .first()
            updateCollectionUseCase(
                UpdateCollectionUseCase.Request(collection.copy(name = formData.name)),
            ).fold(
                onSuccess = {
                    event.emit(BasicCollectionEditViewModelEvent.EditComplete)
                },
                onError = {
                    this@BasicCollectionEditViewModel.logcat { "#onSubmit error: $it" }
                },
            )
        }
    }

    @AssistedFactory
    @ManualViewModelAssistedFactoryKey
    @ContributesIntoMap(AppScope::class)
    interface Factory : ManualViewModelAssistedFactory {
        fun create(collectionId: CollectionId): BasicCollectionEditViewModel
    }
}

internal sealed interface BasicCollectionEditViewModelEvent {
    data object EditComplete : BasicCollectionEditViewModelEvent
}

private const val PageSize = 20
