/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.feature.collection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.dataOrNull
import com.sorrowblue.comicviewer.domain.usecase.collection.GetCollectionUseCase
import com.sorrowblue.comicviewer.domain.usecase.collection.PagingCollectionFileUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactory
import dev.zacsweers.metrox.viewmodel.ManualViewModelAssistedFactoryKey
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.shareIn

@AssistedInject
internal class CollectionViewModel(
    @Assisted val id: CollectionId,
    getCollectionUseCase: GetCollectionUseCase,
    pagingCollectionFileUseCase: PagingCollectionFileUseCase,
) : ViewModel() {

    val pagingDataFlow = pagingCollectionFileUseCase(
        PagingCollectionFileUseCase.Request(PagingConfig(20), id),
    ).cachedIn(viewModelScope)

    val collectionFlow =
        getCollectionUseCase(GetCollectionUseCase.Request(id)).mapNotNull { it.dataOrNull() }
            .shareIn(viewModelScope, SharingStarted.Eagerly, 1)

    @AssistedFactory
    @ManualViewModelAssistedFactoryKey
    @ContributesIntoMap(AppScope::class)
    interface Factory : ManualViewModelAssistedFactory {
        fun create(id: CollectionId): CollectionViewModel
    }
}
