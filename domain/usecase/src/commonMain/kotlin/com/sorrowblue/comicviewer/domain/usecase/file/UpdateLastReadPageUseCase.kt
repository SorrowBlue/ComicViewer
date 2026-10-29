/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.file

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.usecase.OneShotUseCase
import com.sorrowblue.comicviewer.domain.usecase.runCatchingCancellable
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlin.time.Clock
import logcat.LogPriority
import logcat.asLog
import logcat.logcat

/**
 * Updates the last read page and reading timestamp for a book in the local database.
 */
abstract class UpdateLastReadPageUseCase :
    OneShotUseCase<UpdateLastReadPageUseCase.Request, Unit, Unit>() {

    /**
     * Request parameters for [UpdateLastReadPageUseCase].
     *
     * @property bookshelfId The ID of the bookshelf containing the book.
     * @property path The path of the book.
     * @property lastReadPage The index of the last read page.
     */
    data class Request(val bookshelfId: BookshelfId, val path: String, val lastReadPage: Int)
}

@ContributesBinding(AppScope::class)
internal class UpdateLastReadPageUseCaseImpl(
    private val fileRepository: FileRepository,
    private val clock: Clock,
) : UpdateLastReadPageUseCase() {

    @Inject
    constructor(
        fileRepository: FileRepository,
    ) : this(
        fileRepository = fileRepository,
        clock = Clock.System,
    )

    override suspend fun run(request: Request): Resource<Unit, Unit> = runCatchingCancellable {
        fileRepository.updateHistory(
            request.path,
            request.bookshelfId,
            request.lastReadPage,
            clock.now().toEpochMilliseconds(),
        )
    }.fold(
        onSuccess = { Resource.Success(Unit) },
        onFailure = {
            logcat(LogPriority.ERROR) { it.asLog() }
            Resource.Error(Unit)
        },
    )
}
