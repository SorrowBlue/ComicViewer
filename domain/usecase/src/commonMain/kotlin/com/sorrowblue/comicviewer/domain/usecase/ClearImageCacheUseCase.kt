/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.cache.ImageCache
import com.sorrowblue.comicviewer.domain.model.cache.ThumbnailImageCache
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.model.common.fold
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.repository.ImageCacheRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlin.coroutines.cancellation.CancellationException
import logcat.LogPriority
import logcat.asLog
import logcat.logcat

/**
 * Clears cached images for a specific bookshelf or other non-bookshelf caches.
 */
abstract class ClearImageCacheUseCase :
    OneShotUseCase<ClearImageCacheUseCase.Request, Unit, Unit>() {

    /**
     * Request parameters for [ClearImageCacheUseCase].
     */
    sealed interface Request

    /**
     * Request to clear a specific [imageCache] associated with a [bookshelfId].
     *
     * @property bookshelfId The ID of the bookshelf whose cache should be cleared.
     * @property imageCache The image cache metadata to clear.
     */
    data class BookshelfRequest(val bookshelfId: BookshelfId, val imageCache: ImageCache) : Request

    /**
     * Request to clear other image caches not tied to a specific bookshelf.
     */
    data object OtherRequest : Request
}

@Inject
@ContributesBinding(AppScope::class)
internal class ClearImageCacheUseCaseImpl(
    private val imageCacheRepository: ImageCacheRepository,
    private val fileRepository: FileRepository,
) : ClearImageCacheUseCase() {

    override suspend fun run(request: Request): Resource<Unit, Unit> = when (request) {
        is BookshelfRequest -> runCatchingCancellable {
            imageCacheRepository.clearImageCache(request.bookshelfId, request.imageCache).fold(
                onSuccess = {
                    if (request.imageCache is ThumbnailImageCache) {
                        fileRepository.clearCacheKey(request.bookshelfId)
                    }
                    Resource.Success(Unit)
                },
                onError = {
                    if (it.throwable is CancellationException) throw it.throwable
                    logcat(LogPriority.ERROR) { it.throwable.asLog() }
                    Resource.Error(Unit)
                },
            )
        }.getOrElse {
            logcat(LogPriority.ERROR) { it.asLog() }
            Resource.Error(Unit)
        }

        OtherRequest -> runCatchingCancellable {
            imageCacheRepository.clearImageCache()
        }.fold(
            onSuccess = { Resource.Success(Unit) },
            onFailure = {
                logcat(LogPriority.ERROR) { it.asLog() }
                Resource.Error(Unit)
            },
        )
    }
}
