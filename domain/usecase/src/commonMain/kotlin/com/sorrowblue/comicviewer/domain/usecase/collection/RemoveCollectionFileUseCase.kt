/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.collection

import com.sorrowblue.comicviewer.domain.model.collection.CollectionFile
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.repository.CollectionFileRepository
import com.sorrowblue.comicviewer.domain.usecase.OneShotUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

abstract class RemoveCollectionFileUseCase : OneShotUseCase<CollectionFile, Unit, Unit>()

@Inject
@ContributesBinding(AppScope::class)
internal class RemoveCollectionFileUseCaseImpl(private val repository: CollectionFileRepository) :
    RemoveCollectionFileUseCase() {

    override suspend fun run(request: CollectionFile): Resource<Unit, Unit> {
        repository.remove(request)
        return Resource.Success(Unit)
    }
}
