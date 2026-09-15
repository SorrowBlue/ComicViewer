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
class CreateCollectionUseCase(private val repository: CollectionRepository) :
    OneShotUseCase<CreateCollectionUseCase.Request, Collection, CreateCollectionUseCase.Error>() {
    data class Request(val collection: Collection) : OneShotUseCase.Request

    sealed interface Error : Resource.AppError {
        data object System : Error

        data object NotFound : Error
    }

    override suspend fun run(request: Request): Resource<Collection, Error> =
        Resource.Success(repository.create(request.collection))
}
