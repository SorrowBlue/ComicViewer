/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.collection

import com.sorrowblue.comicviewer.domain.model.collection.Collection
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.repository.CollectionRepository
import com.sorrowblue.comicviewer.domain.usecase.OneShotUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

abstract class CreateCollectionUseCase : OneShotUseCase<Collection, Collection, Unit>()

@Inject
@ContributesBinding(AppScope::class)
internal class CreateCollectionUseCaseImpl(private val repository: CollectionRepository) :
    CreateCollectionUseCase() {

    override suspend fun run(request: Collection): Resource<Collection, Unit> =
        Resource.Success(repository.create(request))
}
