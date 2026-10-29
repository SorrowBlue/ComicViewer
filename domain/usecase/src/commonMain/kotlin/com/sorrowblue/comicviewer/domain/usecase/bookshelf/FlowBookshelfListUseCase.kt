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
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import logcat.LogPriority
import logcat.asLog
import logcat.logcat

/**
 * Observes the list of all registered [Bookshelf] items.
 */
abstract class FlowBookshelfListUseCase :
    UseCase<Unit, List<Bookshelf>, FlowBookshelfListUseCase.Error>() {

    /**
     * Errors that can occur when observing the bookshelf list.
     */
    sealed interface Error : Resource.AppError {
        /**
         * Indicates that a system or database error occurred.
         */
        data object System : Error
    }
}

@Inject
@ContributesBinding(AppScope::class)
internal class FlowBookshelfListUseCaseImpl(private val bookshelfRepository: BookshelfRepository) :
    FlowBookshelfListUseCase() {

    override fun run(request: Unit): Flow<Resource<List<Bookshelf>, Error>> = flow {
        bookshelfRepository.allBookshelf().fold(
            onSuccess = { bookshelfFlow ->
                emitAll(
                    bookshelfFlow
                        .distinctUntilChanged()
                        .map<List<Bookshelf>, Resource<List<Bookshelf>, Error>> {
                            Resource.Success(it)
                        },
                )
            },
            onError = {
                if (it.throwable is CancellationException) throw it.throwable
                logcat(LogPriority.ERROR) { it.throwable.asLog() }
                emit(Resource.Error(Error.System))
            },
        )
    }.catch { e ->
        if (e is CancellationException) throw e
        logcat(LogPriority.ERROR) { e.asLog() }
        emit(Resource.Error(Error.System))
    }
}
