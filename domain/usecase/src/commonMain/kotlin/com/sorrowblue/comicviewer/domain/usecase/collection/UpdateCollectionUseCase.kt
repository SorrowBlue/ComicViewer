/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.collection

import com.sorrowblue.comicviewer.domain.model.collection.Collection
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.repository.CollectionRepository
import com.sorrowblue.comicviewer.domain.usecase.OneShotUseCase
import dev.zacsweers.metro.Inject

@Inject
class UpdateCollectionUseCase(private val repository: CollectionRepository) :
    OneShotUseCase<UpdateCollectionUseCase.Request, Unit, UpdateCollectionUseCase.Error>() {
    data class Request(val collection: Collection) : OneShotUseCase.Request

    sealed interface Error : Resource.AppError {
        data object System : Error
    }

    override suspend fun run(request: Request): Resource<Unit, Error> {
        repository.update(request.collection)
        return Resource.Success(Unit)
    }
}
