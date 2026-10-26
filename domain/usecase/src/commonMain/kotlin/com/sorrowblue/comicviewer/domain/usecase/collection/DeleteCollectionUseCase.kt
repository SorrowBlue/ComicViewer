/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.collection

import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.repository.CollectionRepository
import com.sorrowblue.comicviewer.domain.usecase.OneShotUseCase
import com.sorrowblue.comicviewer.domain.usecase.runCatchingCancellable
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import logcat.LogPriority
import logcat.asLog
import logcat.logcat

abstract class DeleteCollectionUseCase : OneShotUseCase<CollectionId, Unit, Unit>()

@Inject
@ContributesBinding(AppScope::class)
internal class DeleteCollectionUseCaseImpl(private val repository: CollectionRepository) :
    DeleteCollectionUseCase() {

    override suspend fun run(request: CollectionId): Resource<Unit, Unit> = runCatchingCancellable {
        repository.delete(request)
    }.fold(
        onSuccess = { Resource.Success(Unit) },
        onFailure = {
            logcat(LogPriority.ERROR) { it.asLog() }
            Resource.Error(Unit)
        },
    )
}
