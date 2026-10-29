/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase

import com.sorrowblue.comicviewer.domain.model.cache.OtherImageCache
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.model.common.fold
import com.sorrowblue.comicviewer.domain.repository.ImageCacheRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlin.coroutines.cancellation.CancellationException

/**
 * Retrieves information about image caches not associated with a specific bookshelf.
 */
abstract class GetOtherImageCacheInfoUseCase : OneShotUseCase<Unit, OtherImageCache, Unit>()

@Inject
@ContributesBinding(AppScope::class)
internal class GetOtherImageCacheInfoUseCaseImpl(
    private val imageCacheRepository: ImageCacheRepository,
    private val sendFatalErrorUseCase: SendFatalErrorUseCase,
) : GetOtherImageCacheInfoUseCase() {

    override suspend fun run(request: Unit): Resource<OtherImageCache, Unit> =
        runCatchingCancellable {
            imageCacheRepository.getOtherImageCache().fold(
                onSuccess = {
                    Resource.Success(it)
                },
                onError = {
                    if (it.throwable is CancellationException) throw it.throwable
                    sendFatalErrorUseCase(SendFatalErrorUseCase.Request(it.throwable))
                    Resource.Error(Unit)
                },
            )
        }.getOrElse {
            sendFatalErrorUseCase(SendFatalErrorUseCase.Request(it))
            Resource.Error(Unit)
        }
}
