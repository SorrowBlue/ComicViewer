/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.service.interactor.bookshelf

import com.sorrowblue.comicviewer.domain.model.cache.BookPageImageCache
import com.sorrowblue.comicviewer.domain.model.cache.ThumbnailImageCache
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.model.common.fold
import com.sorrowblue.comicviewer.domain.model.common.isSuccess
import com.sorrowblue.comicviewer.domain.model.common.onError
import com.sorrowblue.comicviewer.domain.repository.BookshelfRepository
import com.sorrowblue.comicviewer.domain.service.datasource.ImageCacheDataSource
import com.sorrowblue.comicviewer.domain.usecase.SendFatalErrorUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.RemoveBookshelfUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding

@ContributesBinding(AppScope::class)
internal class RemoveBookshelfInteractor(
    private val bookshelfRepository: BookshelfRepository,
    private val imageCacheDataSource: ImageCacheDataSource,
    private val sendFatalErrorUseCase: SendFatalErrorUseCase,
) : RemoveBookshelfUseCase() {
    override suspend fun run(request: Request): Resource<Unit, Unit> =
        bookshelfRepository.delete(request.bookshelfId).fold(
            onSuccess = { _ ->
                val pageResult = imageCacheDataSource.clearImageCache(
                    request.bookshelfId,
                    BookPageImageCache(0, 0),
                )
                val thumbnailResult = imageCacheDataSource.clearImageCache(
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
