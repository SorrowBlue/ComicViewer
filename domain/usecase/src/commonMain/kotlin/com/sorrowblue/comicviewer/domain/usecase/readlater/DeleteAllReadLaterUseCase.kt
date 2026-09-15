/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.readlater

import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.model.common.fold
import com.sorrowblue.comicviewer.domain.repository.ReadLaterFileRepository
import com.sorrowblue.comicviewer.domain.usecase.OneShotUseCase
import com.sorrowblue.comicviewer.domain.usecase.SendFatalErrorUseCase
import dev.zacsweers.metro.Inject

@Inject
class DeleteAllReadLaterUseCase(
    private val readLaterFileRepository: ReadLaterFileRepository,
    private val sendFatalErrorUseCase: SendFatalErrorUseCase,
) : OneShotUseCase<DeleteAllReadLaterUseCase.Request, Unit, Unit>() {
    data object Request : OneShotUseCase.Request

    override suspend fun run(request: Request): Resource<Unit, Unit> =
        readLaterFileRepository.deleteAll().fold(
            onSuccess = { Resource.Success(Unit) },
            onError = {
                sendFatalErrorUseCase(SendFatalErrorUseCase.Request(it.throwable))
                Resource.Error(Unit)
            },
        )
}
