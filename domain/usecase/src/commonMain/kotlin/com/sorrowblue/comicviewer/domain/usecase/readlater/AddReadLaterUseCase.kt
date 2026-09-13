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
import dev.zacsweers.metro.Inject

@Inject
class AddReadLaterUseCase(
    private val readLaterFileRepository: ReadLaterFileRepository,
    private val sendFatalErrorUseCase: SendFatalErrorUseCase,
) : OneShotUseCase<AddReadLaterUseCase.Request, ReadLaterFile, Unit>() {
    data class Request private constructor(val readLaterFile: ReadLaterFile) :
        OneShotUseCase.Request {
        constructor(bookshelfId: BookshelfId, path: String) : this(ReadLaterFile(bookshelfId, path))
    }

    override suspend fun run(request: Request): Resource<ReadLaterFile, Unit> =
        when (val result = readLaterFileRepository.updateOrAdd(request.readLaterFile)) {
            is Resource.Success -> Resource.Success(request.readLaterFile)

            is Resource.Error -> {
                sendFatalErrorUseCase(SendFatalErrorUseCase.Request(result.error.throwable))
                Resource.Error(Unit)
            }
        }
}
