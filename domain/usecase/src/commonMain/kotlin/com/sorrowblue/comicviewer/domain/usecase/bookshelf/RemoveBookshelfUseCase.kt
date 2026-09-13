/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.bookshelf

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.cache.BookPageImageCache
import com.sorrowblue.comicviewer.domain.model.cache.ThumbnailImageCache
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.model.common.fold
import com.sorrowblue.comicviewer.domain.model.common.isSuccess
import com.sorrowblue.comicviewer.domain.model.common.onError
import com.sorrowblue.comicviewer.domain.repository.BookshelfRepository
import com.sorrowblue.comicviewer.domain.repository.ImageCacheRepository
import com.sorrowblue.comicviewer.domain.usecase.OneShotUseCase
import com.sorrowblue.comicviewer.domain.usecase.SendFatalErrorUseCase
import dev.zacsweers.metro.Inject

@Inject
class RemoveBookshelfUseCase(
    private val bookshelfRepository: BookshelfRepository,
    private val imageCacheRepository: ImageCacheRepository,
    private val sendFatalErrorUseCase: SendFatalErrorUseCase,
) : OneShotUseCase<RemoveBookshelfUseCase.Request, Unit, Unit>() {

    data class Request(val bookshelfId: BookshelfId) : OneShotUseCase.Request

    override suspend fun run(request: Request): Resource<Unit, Unit> =
        bookshelfRepository.delete(request.bookshelfId).fold(
            onSuccess = { _ ->
                val pageResult = imageCacheRepository.clearImageCache(
                    request.bookshelfId,
                    BookPageImageCache(0, 0),
                )
                val thumbnailResult = imageCacheRepository.clearImageCache(
                    request.bookshelfId,
                    ThumbnailImageCache(0, 0),
                )
                if (pageResult.isSuccess && thumbnailResult.isSuccess) {
                    Resource.Success(Unit)
                } else {
                    pageResult.onError {
                        sendFatalErrorUseCase(SendFatalErrorUseCase.Request(it.throwable))
                    }
                    thumbnailResult.onError {
                        sendFatalErrorUseCase(SendFatalErrorUseCase.Request(it.throwable))
                    }
                    Resource.Error(Unit)
                }
            },
            onError = {
                sendFatalErrorUseCase(SendFatalErrorUseCase.Request(it.throwable))
                Resource.Error(Unit)
            },
        )
}
