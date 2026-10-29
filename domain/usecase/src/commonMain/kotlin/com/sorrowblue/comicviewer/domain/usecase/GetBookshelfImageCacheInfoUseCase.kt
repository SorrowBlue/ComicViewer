/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase

import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.cache.BookshelfImageCacheInfo
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.model.common.fold
import com.sorrowblue.comicviewer.domain.repository.BookshelfRepository
import com.sorrowblue.comicviewer.domain.repository.ImageCacheRepository
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

/**
 * Observes image cache information for all registered bookshelves.
 */
abstract class GetBookshelfImageCacheInfoUseCase :
    UseCase<Unit, List<BookshelfImageCacheInfo>, Unit>()

@Inject
@ContributesBinding(AppScope::class)
internal class GetBookshelfImageCacheInfoUseCaseImpl(
    private val bookshelfRepository: BookshelfRepository,
    private val imageCacheRepository: ImageCacheRepository,
    private val sendFatalErrorUseCase: SendFatalErrorUseCase,
) : GetBookshelfImageCacheInfoUseCase() {

    override fun run(request: Unit): Flow<Resource<List<BookshelfImageCacheInfo>, Unit>> = flow {
        bookshelfRepository.allBookshelf().fold(
            onSuccess = { bookshelfFlow ->
                emitAll(
                    bookshelfFlow
                        .distinctUntilChanged()
                        .map<List<Bookshelf>, Resource<List<BookshelfImageCacheInfo>, Unit>> {
                            Resource.Success(imageCacheInfoList(it))
                        },
                )
            },
            onError = {
                if (it.throwable is CancellationException) throw it.throwable
                sendFatalErrorUseCase(SendFatalErrorUseCase.Request(it.throwable))
                emit(Resource.Error(Unit))
            },
        )
    }.catch { e ->
        if (e is CancellationException) throw e
        sendFatalErrorUseCase(SendFatalErrorUseCase.Request(e))
        emit(Resource.Error(Unit))
    }

    private suspend fun imageCacheInfoList(list: List<Bookshelf>): List<BookshelfImageCacheInfo> =
        list.mapNotNull { bookshelf ->
            imageCacheRepository.getBookshelfImageCacheInfo(bookshelf).fold(
                onSuccess = { it },
                onError = {
                    if (it.throwable is CancellationException) throw it.throwable
                    sendFatalErrorUseCase(SendFatalErrorUseCase.Request(it.throwable))
                    null
                },
            )
        }
}
