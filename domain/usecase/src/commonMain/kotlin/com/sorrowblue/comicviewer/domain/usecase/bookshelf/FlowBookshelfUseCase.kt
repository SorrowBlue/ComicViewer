/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.bookshelf

import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.repository.BookshelfRepository
import com.sorrowblue.comicviewer.domain.usecase.UseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import logcat.LogPriority
import logcat.asLog
import logcat.logcat

/**
 * UseCase to observe a single [Bookshelf] by [BookshelfId].
 */
abstract class FlowBookshelfUseCase :
    UseCase<BookshelfId, Bookshelf?, FlowBookshelfUseCase.Error>() {

    /**
     * Errors that can occur when observing a bookshelf.
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
internal class FlowBookshelfUseCaseImpl(private val bookshelfRepository: BookshelfRepository) :
    FlowBookshelfUseCase() {

    override fun run(request: BookshelfId): Flow<Resource<Bookshelf?, Error>> =
        bookshelfRepository.flow(request)
            .distinctUntilChanged()
            .map<Bookshelf?, Resource<Bookshelf?, Error>> {
                Resource.Success(it)
            }.catch { e ->
                if (e is CancellationException) throw e
                logcat(LogPriority.ERROR) { e.asLog() }
                emit(Resource.Error(Error.System))
            }
}
