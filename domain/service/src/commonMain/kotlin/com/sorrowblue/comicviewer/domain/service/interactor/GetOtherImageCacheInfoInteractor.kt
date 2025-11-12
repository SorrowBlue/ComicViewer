package com.sorrowblue.comicviewer.domain.service.interactor

import com.sorrowblue.comicviewer.domain.model.OtherImageCache
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.fold
import com.sorrowblue.comicviewer.domain.service.datasource.ImageCacheDataSource
import com.sorrowblue.comicviewer.domain.usecase.GetOtherImageCacheInfoUseCase
import com.sorrowblue.comicviewer.domain.usecase.SendFatalErrorUseCase
import dev.zacsweers.metro.Inject

@Inject
internal class GetOtherImageCacheInfoInteractor(
    private val imageCacheDataSource: ImageCacheDataSource,
    private val sendFatalErrorUseCase: SendFatalErrorUseCase,
) : GetOtherImageCacheInfoUseCase() {
    override suspend fun run(request: Request): Resource<OtherImageCache, Unit> =
        imageCacheDataSource.getOtherImageCache().fold(
            onSuccess = {
                Resource.Success(it)
            },
            onError = {
                sendFatalErrorUseCase(SendFatalErrorUseCase.Request(it.throwable))
                Resource.Error(Unit)
            },
        )
}
