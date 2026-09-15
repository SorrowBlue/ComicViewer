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
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

abstract class RemoveBookshelfUseCase : OneShotUseCase<BookshelfId, Unit, Unit>()

@Inject
@ContributesBinding(AppScope::class)
internal class RemoveBookshelfUseCaseImpl(
    private val bookshelfRepository: BookshelfRepository,
    private val imageCacheRepository: ImageCacheRepository,
    private val sendFatalErrorUseCase: SendFatalErrorUseCase,
) : RemoveBookshelfUseCase() {

    override suspend fun run(request: BookshelfId): Resource<Unit, Unit> =
        bookshelfRepository.delete(request).fold(
            onSuccess = { _ ->
                val pageResult = imageCacheRepository.clearImageCache(
                    request,
                    BookPageImageCache(0, 0),
                )
                val thumbnailResult = imageCacheRepository.clearImageCache(
                    request,
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
