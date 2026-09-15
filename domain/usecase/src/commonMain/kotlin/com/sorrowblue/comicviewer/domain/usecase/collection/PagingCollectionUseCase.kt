/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.collection

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.BaseRequest
import com.sorrowblue.comicviewer.domain.model.collection.Collection
import com.sorrowblue.comicviewer.domain.repository.CollectionRepository
import com.sorrowblue.comicviewer.domain.usecase.PagingUseCase
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

@Inject
class PagingCollectionUseCase(private val repository: CollectionRepository) :
    PagingUseCase<PagingCollectionUseCase.Request, Collection>() {
    data class Request(val pagingConfig: PagingConfig) : BaseRequest

    override fun run(request: Request): Flow<PagingData<Collection>> =
        repository.pagingDataFlow(request.pagingConfig)
}
