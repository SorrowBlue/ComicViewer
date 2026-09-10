/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.service.interactor.readlater

import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.model.common.fold
import com.sorrowblue.comicviewer.domain.repository.ReadLaterFileRepository
import com.sorrowblue.comicviewer.domain.usecase.SendFatalErrorUseCase
import com.sorrowblue.comicviewer.domain.usecase.readlater.DeleteAllReadLaterUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding

@ContributesBinding(AppScope::class)
internal class DeleteAllReadLaterInteractor(
    private val readLaterFileRepository: ReadLaterFileRepository,
    private val sendFatalErrorInteractor: SendFatalErrorUseCase,
) : DeleteAllReadLaterUseCase() {
    override suspend fun run(request: Request): Resource<Unit, Unit> =
        readLaterFileRepository.deleteAll().fold(
            onSuccess = { Resource.Success(Unit) },
            onError = {
                sendFatalErrorInteractor(SendFatalErrorUseCase.Request(it.throwable))
                Resource.Error(Unit)
            },
        )
}
