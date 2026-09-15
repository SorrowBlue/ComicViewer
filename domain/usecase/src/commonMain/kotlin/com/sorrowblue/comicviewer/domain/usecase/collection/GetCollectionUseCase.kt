/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.collection

import com.sorrowblue.comicviewer.domain.model.collection.Collection
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.repository.CollectionRepository
import com.sorrowblue.comicviewer.domain.usecase.UseCase
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Inject
class GetCollectionUseCase(private val repository: CollectionRepository) :
    UseCase<GetCollectionUseCase.Request, Collection, GetCollectionUseCase.Error>() {
    data class Request(val id: CollectionId) : UseCase.Request

    sealed interface Error : Resource.AppError {
        data object System : Error

        data object NotFound : Error
    }

    override fun run(request: Request): Flow<Resource<Collection, Error>> =
        repository.flow(request.id).map {
            if (it == null) {
                Resource.Error(Error.NotFound)
            } else {
                Resource.Success(it)
            }
        }
}
