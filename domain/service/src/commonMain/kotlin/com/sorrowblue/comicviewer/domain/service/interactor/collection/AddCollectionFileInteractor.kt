/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.service.interactor.collection

import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.repository.CollectionFileRepository
import com.sorrowblue.comicviewer.domain.repository.CollectionRepository
import com.sorrowblue.comicviewer.domain.usecase.collection.AddCollectionFileUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import kotlinx.coroutines.flow.first

@ContributesBinding(AppScope::class)
internal class AddCollectionFileInteractor(
    private val collectionRepository: CollectionRepository,
    private val collectionFileRepository: CollectionFileRepository,
) : AddCollectionFileUseCase() {
    override suspend fun run(request: Request): Resource<Unit, Error> {
        collectionFileRepository.add(request.file)
        collectionRepository.flow(request.file.id).first()?.let {
            collectionRepository.update(it)
        }
        return Resource.Success(Unit)
    }
}
