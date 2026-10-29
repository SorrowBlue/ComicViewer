/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.bookshelf

import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfFolder
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.repository.BookshelfRepository
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.usecase.SendFatalErrorUseCase
import com.sorrowblue.comicviewer.domain.usecase.UseCase
import com.sorrowblue.comicviewer.domain.usecase.runCatchingCancellable
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

/**
 * Observes a [BookshelfFolder] combining a [Bookshelf] and its root folder by [BookshelfId].
 */
abstract class GetBookshelfInfoUseCase :
    UseCase<BookshelfId, BookshelfFolder, GetBookshelfInfoUseCase.Error>() {

    /**
     * Errors that can occur when retrieving bookshelf information.
     */
    sealed interface Error : Resource.AppError {
        /**
         * Indicates that the bookshelf or its root folder was not found.
         */
        data object NotFound : Error

        /**
         * Indicates that a system error occurred.
         */
        data object System : Error
    }
}

@Inject
@ContributesBinding(AppScope::class)
internal class GetBookshelfInfoUseCaseImpl(
    private val bookshelfRepository: BookshelfRepository,
    private val fileRepository: FileRepository,
    private val sendFatalErrorUseCase: SendFatalErrorUseCase,
) : GetBookshelfInfoUseCase() {

    override fun run(request: BookshelfId) = bookshelfRepository.flow(request)
        .distinctUntilChanged()
        .map<Bookshelf?, Resource<BookshelfFolder, Error>> {
            if (it == null) {
                return@map Resource.Error(Error.NotFound)
            }
            val folder = runCatchingCancellable {
                fileRepository.root(request)
            }.getOrElse { e ->
                sendFatalErrorUseCase(SendFatalErrorUseCase.Request(e))
                return@map Resource.Error(Error.System)
            }
            if (folder != null) {
                Resource.Success(BookshelfFolder(it, folder))
            } else {
                Resource.Error(Error.NotFound)
            }
        }.catch { e ->
            if (e is CancellationException) throw e
            sendFatalErrorUseCase(SendFatalErrorUseCase.Request(e))
            emit(Resource.Error(Error.System))
        }
}
