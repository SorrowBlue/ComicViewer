/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.collection

import com.sorrowblue.comicviewer.domain.model.collection.CollectionFile
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.repository.CollectionFileRepository
import com.sorrowblue.comicviewer.domain.usecase.OneShotUseCase
import dev.zacsweers.metro.Inject

@Inject
class RemoveCollectionFileUseCase(private val repository: CollectionFileRepository) :
    OneShotUseCase<RemoveCollectionFileUseCase.Request, Unit, RemoveCollectionFileUseCase.Error>() {
    data class Request(val file: CollectionFile) : OneShotUseCase.Request

    sealed interface Error : Resource.AppError {
        data object System : Error
    }

    override suspend fun run(request: Request): Resource<Unit, Error> {
        repository.remove(request.file)
        return Resource.Success(Unit)
    }
}
