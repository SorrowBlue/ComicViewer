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
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

abstract class ExistsReadLaterUseCase :
    UseCase<ExistsReadLaterUseCase.Request, Boolean, Unit>() {

    data class Request(val bookshelfId: BookshelfId, val path: String)
}

@Inject
@ContributesBinding(AppScope::class)
internal class ExistsReadLaterUseCaseImpl(
    private val readLaterFileRepository: ReadLaterFileRepository,
    private val sendFatalErrorUseCase: SendFatalErrorUseCase,
) : ExistsReadLaterUseCase() {

    override fun run(request: Request): Flow<Resource<Boolean, Unit>> = readLaterFileRepository
        .exists(ReadLaterFile(request.bookshelfId, request.path))
        .fold(
            onSuccess = { flow ->
                flow.map<Boolean, Resource<Boolean, Unit>> {
                    Resource.Success(it)
                }.catch { e ->
                    if (e is CancellationException) throw e
                    sendFatalErrorUseCase(SendFatalErrorUseCase.Request(e))
                    emit(Resource.Error(Unit))
                }
            },
            onError = {
                flow {
                    sendFatalErrorUseCase(SendFatalErrorUseCase.Request(it.throwable))
                    emit(Resource.Error(Unit))
                }
            },
        )
}
