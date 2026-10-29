/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.readlater

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.model.readlater.ReadLaterFile
import com.sorrowblue.comicviewer.domain.repository.ReadLaterFileRepository
import com.sorrowblue.comicviewer.domain.usecase.OneShotUseCase
import com.sorrowblue.comicviewer.domain.usecase.SendFatalErrorUseCase
import com.sorrowblue.comicviewer.domain.usecase.runCatchingCancellable
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Clock

/**
 * Adds or updates a file in the "read later" list.
 */
abstract class AddReadLaterUseCase :
    OneShotUseCase<AddReadLaterUseCase.Request, ReadLaterFile, Unit>() {

    /**
     * Request parameters for [AddReadLaterUseCase].
     *
     * @property bookshelfId The ID of the bookshelf containing the file.
     * @property path The path of the file to add to "read later".
     */
    data class Request(val bookshelfId: BookshelfId, val path: String)
}

@ContributesBinding(AppScope::class)
internal class AddReadLaterUseCaseImpl(
    private val readLaterFileRepository: ReadLaterFileRepository,
    private val sendFatalErrorUseCase: SendFatalErrorUseCase,
    private val clock: Clock,
) : AddReadLaterUseCase() {

    @Inject
    constructor(
        readLaterFileRepository: ReadLaterFileRepository,
        sendFatalErrorUseCase: SendFatalErrorUseCase,
    ) : this(
        readLaterFileRepository = readLaterFileRepository,
        sendFatalErrorUseCase = sendFatalErrorUseCase,
        clock = Clock.System,
    )

    override suspend fun run(request: Request): Resource<ReadLaterFile, Unit> =
        runCatchingCancellable {
            when (
                val result = readLaterFileRepository.updateOrAdd(
                    ReadLaterFile(
                        bookshelfId = request.bookshelfId,
                        path = request.path,
                        modifiedDate = clock.now().toEpochMilliseconds(),
                    ),
                )
            ) {
                is Resource.Success -> Resource.Success(result.data)

                is Resource.Error -> {
                    if (result.error.throwable is CancellationException) {
                        throw result.error.throwable
                    }
                    sendFatalErrorUseCase(SendFatalErrorUseCase.Request(result.error.throwable))
                    Resource.Error(Unit)
                }
            }
        }.getOrElse {
            sendFatalErrorUseCase(SendFatalErrorUseCase.Request(it))
            Resource.Error(Unit)
        }
}
