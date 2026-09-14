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
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

abstract class DeleteReadLaterUseCase :
    OneShotUseCase<DeleteReadLaterUseCase.Request, Unit, Unit>() {

    data class Request private constructor(val readLaterFile: ReadLaterFile) {
        constructor(bookshelfId: BookshelfId, path: String) : this(ReadLaterFile(bookshelfId, path))
    }
}

@Inject
@ContributesBinding(AppScope::class)
internal class DeleteReadLaterUseCaseImpl(
    private val readLaterFileRepository: ReadLaterFileRepository,
    private val sendFatalErrorUseCase: SendFatalErrorUseCase,
) : DeleteReadLaterUseCase() {

    override suspend fun run(request: Request): Resource<Unit, Unit> =
        when (val result = readLaterFileRepository.delete(request.readLaterFile)) {
            is Resource.Success -> Resource.Success(Unit)

            is Resource.Error -> {
                sendFatalErrorUseCase(SendFatalErrorUseCase.Request(result.error.throwable))
                Resource.Error(Unit)
            }
        }
}
