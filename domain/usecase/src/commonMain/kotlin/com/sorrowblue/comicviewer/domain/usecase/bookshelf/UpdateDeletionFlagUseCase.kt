/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.bookshelf

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.repository.BookshelfRepository
import com.sorrowblue.comicviewer.domain.usecase.OneShotUseCase
import com.sorrowblue.comicviewer.domain.usecase.runCatchingCancellable
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import logcat.LogPriority
import logcat.asLog
import logcat.logcat

/**
 * Updates the soft-deletion flag of a bookshelf.
 */
abstract class UpdateDeletionFlagUseCase :
    OneShotUseCase<UpdateDeletionFlagUseCase.Request, Unit, Unit>() {

    /**
     * Request parameters for [UpdateDeletionFlagUseCase].
     *
     * @property bookshelfId The ID of the bookshelf to update.
     * @property deleted Whether the bookshelf is marked as deleted.
     */
    data class Request(val bookshelfId: BookshelfId, val deleted: Boolean)
}

@Inject
@ContributesBinding(AppScope::class)
internal class UpdateDeletionFlagUseCaseImpl(
    private val bookshelfRepository: BookshelfRepository,
) : UpdateDeletionFlagUseCase() {

    override suspend fun run(request: Request): Resource<Unit, Unit> = runCatchingCancellable {
        bookshelfRepository.updateDeleted(request.bookshelfId, request.deleted)
    }.fold(
        onSuccess = { Resource.Success(Unit) },
        onFailure = {
            logcat(LogPriority.ERROR) { it.asLog() }
            Resource.Error(Unit)
        },
    )
}
