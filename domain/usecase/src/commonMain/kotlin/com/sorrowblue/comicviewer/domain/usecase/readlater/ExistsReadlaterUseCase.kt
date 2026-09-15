/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.readlater

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.model.common.fold
import com.sorrowblue.comicviewer.domain.model.readlater.ReadLaterFile
import com.sorrowblue.comicviewer.domain.repository.ReadLaterFileRepository
import com.sorrowblue.comicviewer.domain.usecase.SendFatalErrorUseCase
import com.sorrowblue.comicviewer.domain.usecase.UseCase
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

@Inject
class ExistsReadlaterUseCase(
    private val readLaterFileRepository: ReadLaterFileRepository,
    private val sendFatalErrorUseCase: SendFatalErrorUseCase,
) : UseCase<ExistsReadlaterUseCase.Request, Boolean, Unit>() {
    data class Request(val bookshelfId: BookshelfId, val path: String) : UseCase.Request

    override fun run(request: Request): Flow<Resource<Boolean, Unit>> = readLaterFileRepository
        .exists(ReadLaterFile(request.bookshelfId, request.path))
        .fold(
            onSuccess = { flow ->
                flow.map { Resource.Success(it) }
            },
            onError = {
                flow {
                    sendFatalErrorUseCase(SendFatalErrorUseCase.Request(it.throwable))
                    emit(Resource.Error(Unit))
                }
            },
        )
}
