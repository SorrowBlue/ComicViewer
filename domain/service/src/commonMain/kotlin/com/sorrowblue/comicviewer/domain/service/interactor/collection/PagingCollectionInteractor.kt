/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.service.interactor.collection

import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.collection.Collection
import com.sorrowblue.comicviewer.domain.repository.CollectionRepository
import com.sorrowblue.comicviewer.domain.usecase.collection.PagingCollectionUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import kotlinx.coroutines.flow.Flow

@ContributesBinding(AppScope::class)
internal class PagingCollectionInteractor(private val repository: CollectionRepository) :
    PagingCollectionUseCase() {
    override fun run(request: Request): Flow<PagingData<Collection>> =
        repository.pagingDataFlow(request.pagingConfig)
}
