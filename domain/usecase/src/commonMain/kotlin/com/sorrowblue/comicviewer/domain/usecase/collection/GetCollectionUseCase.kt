/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.collection

import com.sorrowblue.comicviewer.domain.model.collection.Collection
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.repository.CollectionRepository
import com.sorrowblue.comicviewer.domain.usecase.UseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

abstract class GetCollectionUseCase :
    UseCase<CollectionId, Collection, GetCollectionUseCase.Error>() {

    sealed interface Error : Resource.AppError {
        data object System : Error

        data object NotFound : Error
    }
}

@Inject
@ContributesBinding(AppScope::class)
internal class GetCollectionUseCaseImpl(private val repository: CollectionRepository) :
    GetCollectionUseCase() {

    override fun run(request: CollectionId): Flow<Resource<Collection, Error>> =
        repository.flow(request).map {
            if (it == null) {
                Resource.Error(Error.NotFound)
            } else {
                Resource.Success(it)
            }
        }
}
