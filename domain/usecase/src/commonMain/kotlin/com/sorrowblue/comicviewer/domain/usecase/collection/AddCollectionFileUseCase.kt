/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.collection

import com.sorrowblue.comicviewer.domain.model.collection.CollectionFile
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.repository.CollectionFileRepository
import com.sorrowblue.comicviewer.domain.repository.CollectionRepository
import com.sorrowblue.comicviewer.domain.usecase.OneShotUseCase
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.first

@Inject
class AddCollectionFileUseCase(
    private val collectionRepository: CollectionRepository,
    private val collectionFileRepository: CollectionFileRepository,
) : OneShotUseCase<AddCollectionFileUseCase.Request, Unit, AddCollectionFileUseCase.Error>() {
    data class Request(val file: CollectionFile) : OneShotUseCase.Request

    sealed interface Error : Resource.AppError {
        data object System : Error
    }

    override suspend fun run(request: Request): Resource<Unit, Error> {
        collectionFileRepository.add(request.file)
        collectionRepository.flow(request.file.id).first()?.let {
            collectionRepository.update(it)
        }
        return Resource.Success(Unit)
    }
}
