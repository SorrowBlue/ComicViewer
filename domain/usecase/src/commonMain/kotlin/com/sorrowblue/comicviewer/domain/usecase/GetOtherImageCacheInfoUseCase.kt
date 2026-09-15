/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase

import com.sorrowblue.comicviewer.domain.model.cache.OtherImageCache
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.model.common.fold
import com.sorrowblue.comicviewer.domain.repository.ImageCacheRepository
import dev.zacsweers.metro.Inject

@Inject
class GetOtherImageCacheInfoUseCase(
    private val imageCacheRepository: ImageCacheRepository,
    private val sendFatalErrorUseCase: SendFatalErrorUseCase,
) : OneShotUseCase<GetOtherImageCacheInfoUseCase.Request, OtherImageCache, Unit>() {
    data object Request : OneShotUseCase.Request

    override suspend fun run(request: Request): Resource<OtherImageCache, Unit> =
        imageCacheRepository.getOtherImageCache().fold(
            onSuccess = {
                Resource.Success(it)
            },
            onError = {
                sendFatalErrorUseCase(SendFatalErrorUseCase.Request(it.throwable))
                Resource.Error(Unit)
            },
        )
}
