/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.collection.delete

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.dataOrNull
import com.sorrowblue.comicviewer.domain.usecase.collection.DeleteCollectionUseCase
import com.sorrowblue.comicviewer.domain.usecase.collection.GetCollectionUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactoryKey
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@AssistedInject
internal class DeleteCollectionViewModel(
    @Assisted val id: CollectionId,
    getCollectionUseCase: GetCollectionUseCase,
    private val deleteCollectionUseCase: DeleteCollectionUseCase,
) : ViewModel() {

    val uiState =
        getCollectionUseCase(GetCollectionUseCase.Request(id)).mapNotNull { it.dataOrNull() }
            .map {
                DeleteCollectionScreenUiState(name = it.name)
            }.stateIn(viewModelScope, SharingStarted.Eagerly, DeleteCollectionScreenUiState())

    fun delete(onComplete: () -> Unit) {
        viewModelScope.launch {
            deleteCollectionUseCase(DeleteCollectionUseCase.Request(id))
            onComplete()
        }
    }

    @AssistedFactory
    @ManualViewModelAssistedFactoryKey
    @ContributesIntoMap(AppScope::class)
    interface Factory : ManualViewModelAssistedFactory {
        fun create(id: CollectionId): DeleteCollectionViewModel
    }
}
