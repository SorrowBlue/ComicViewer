/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.collection

import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.repository.CollectionRepository
import com.sorrowblue.comicviewer.domain.usecase.OneShotUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

abstract class DeleteCollectionUseCase : OneShotUseCase<CollectionId, Unit, Unit>()

@Inject
@ContributesBinding(AppScope::class)
internal class DeleteCollectionUseCaseImpl(private val repository: CollectionRepository) :
    DeleteCollectionUseCase() {

    override suspend fun run(request: CollectionId): Resource<Unit, Unit> {
        repository.delete(request)
        return Resource.Success(Unit)
    }
}
