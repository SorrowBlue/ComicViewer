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

abstract class DeleteReadLaterUseCase :
    OneShotUseCase<DeleteReadLaterUseCase.Request, Unit, Unit>() {

    data class Request(val bookshelfId: BookshelfId, val path: String)
}

@Inject
@ContributesBinding(AppScope::class)
internal class DeleteReadLaterUseCaseImpl(
    private val readLaterFileRepository: ReadLaterFileRepository,
    private val sendFatalErrorUseCase: SendFatalErrorUseCase,
) : DeleteReadLaterUseCase() {

    override suspend fun run(request: Request): Resource<Unit, Unit> = runCatchingCancellable {
        when (
            val result = readLaterFileRepository.delete(
                ReadLaterFile(request.bookshelfId, request.path),
            )
        ) {
            is Resource.Success -> Resource.Success(Unit)

            is Resource.Error -> {
                sendFatalErrorUseCase(SendFatalErrorUseCase.Request(result.error.throwable))
                Resource.Error(Unit)
            }
        }
    }.getOrElse {
        sendFatalErrorUseCase(SendFatalErrorUseCase.Request(it))
        Resource.Error(Unit)
    }
}
