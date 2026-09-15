/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.collection

import com.sorrowblue.comicviewer.domain.model.collection.CollectionFile
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.repository.CollectionFileRepository
import com.sorrowblue.comicviewer.domain.repository.CollectionRepository
import com.sorrowblue.comicviewer.domain.usecase.OneShotUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.first

abstract class AddCollectionFileUseCase : OneShotUseCase<CollectionFile, Unit, Unit>()

@Inject
@ContributesBinding(AppScope::class)
internal class AddCollectionFileUseCaseImpl(
    private val collectionRepository: CollectionRepository,
    private val collectionFileRepository: CollectionFileRepository,
) : AddCollectionFileUseCase() {

    override suspend fun run(request: CollectionFile): Resource<Unit, Unit> {
        collectionFileRepository.add(request)
        collectionRepository.flow(request.id).first()?.let {
            collectionRepository.update(it)
        }
        return Resource.Success(Unit)
    }
}
