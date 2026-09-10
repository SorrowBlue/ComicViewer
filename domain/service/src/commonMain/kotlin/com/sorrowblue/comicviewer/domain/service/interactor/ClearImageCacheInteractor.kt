/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.service.interactor

import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.repository.ImageCacheRepository
import com.sorrowblue.comicviewer.domain.usecase.ClearImageCacheUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding

@ContributesBinding(AppScope::class)
internal class ClearImageCacheInteractor(
    private val imageCacheRepository: ImageCacheRepository,
    private val fileRepository: FileRepository,
) : ClearImageCacheUseCase() {
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
