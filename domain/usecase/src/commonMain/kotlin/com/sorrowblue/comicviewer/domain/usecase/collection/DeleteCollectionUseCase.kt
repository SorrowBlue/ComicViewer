/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.collection

import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.repository.CollectionRepository
import com.sorrowblue.comicviewer.domain.usecase.OneShotUseCase
import dev.zacsweers.metro.Inject

@Inject
class DeleteCollectionUseCase(private val repository: CollectionRepository) :
    OneShotUseCase<DeleteCollectionUseCase.Request, Unit, Unit>() {
    data class Request(val id: CollectionId) : OneShotUseCase.Request

    override suspend fun run(request: Request): Resource<Unit, Unit> {
        repository.delete(request.id)
        return Resource.Success(Unit)
    }
}
