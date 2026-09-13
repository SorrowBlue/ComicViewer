/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase

import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.cache.ImageCache
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.repository.ImageCacheRepository
import dev.zacsweers.metro.Inject

@Inject
class ClearImageCacheUseCase(
    private val imageCacheRepository: ImageCacheRepository,
    private val fileRepository: FileRepository,
) : OneShotUseCase<ClearImageCacheUseCase.Request, Unit, Unit>() {
    sealed interface Request : OneShotUseCase.Request

    data class BookshelfRequest(val bookshelfId: BookshelfId, val imageCache: ImageCache) : Request

    data object OtherRequest : Request

    override suspend fun run(request: Request): Resource<Unit, Unit> {
        when (request) {
            is BookshelfRequest -> {
                fileRepository.clearCacheKey(request.bookshelfId)
                imageCacheRepository.clearImageCache(request.bookshelfId, request.imageCache)
            }

            OtherRequest ->
                imageCacheRepository.clearImageCache()
        }
        return Resource.Success(Unit)
    }
}
