/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.bookshelf

import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.model.common.fold
import com.sorrowblue.comicviewer.domain.repository.BookshelfRepository
import com.sorrowblue.comicviewer.domain.usecase.UseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import logcat.LogPriority
import logcat.asLog
import logcat.logcat

abstract class FlowBookshelfListUseCase :
    UseCase<Unit, List<Bookshelf>, FlowBookshelfListUseCase.Error>() {

    sealed interface Error : Resource.AppError {
        data object System : Error
    }
}

@Inject
@ContributesBinding(AppScope::class)
internal class FlowBookshelfListUseCaseImpl(private val bookshelfRepository: BookshelfRepository) :
    FlowBookshelfListUseCase() {

    override fun run(request: Unit): Flow<Resource<List<Bookshelf>, Error>> =
        bookshelfRepository.allBookshelf().fold(
            onSuccess = { flow ->
                flow.map<List<Bookshelf>, Resource<List<Bookshelf>, Error>> {
                    Resource.Success(it)
                }.catch { e ->
                    if (e is CancellationException) throw e
                    logcat(LogPriority.ERROR) { e.asLog() }
                    emit(Resource.Error(Error.System))
                }
            },
            onError = {
                logcat(LogPriority.ERROR) { it.throwable.asLog() }
                flowOf(Resource.Error(Error.System))
            },
        )
}
