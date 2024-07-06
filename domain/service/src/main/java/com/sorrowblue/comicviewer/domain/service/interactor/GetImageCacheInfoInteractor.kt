package com.sorrowblue.comicviewer.domain.service.interactor

import com.sorrowblue.comicviewer.domain.model.ImageCacheInfo
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.service.datasource.ImageCacheDataSource
import com.sorrowblue.comicviewer.domain.usecase.ClearImageCacheUseCase
import com.sorrowblue.comicviewer.domain.usecase.GetImageCacheInfoUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class GetImageCacheInfoInteractor @Inject constructor(
    private val imageCacheDataSource: ImageCacheDataSource,
) : GetImageCacheInfoUseCase() {

    override fun run(request: Request): Flow<Resource<List<ImageCacheInfo>, Error>> {
        return flow {
            emit(Resource.Success(imageCacheDataSource.getImageCacheInfo()))
        }
    }
}

internal class ClearImageCacheInteractor @Inject constructor(
    private val imageCacheDataSource: ImageCacheDataSource,
) : ClearImageCacheUseCase() {

    override fun run(request: Request): Flow<Resource<Unit, Error>> {
        return flow {
            when (request) {
                is BookshelfRequest ->
                    imageCacheDataSource.clearImageCache(request.bookshelfId, request.type)

                FavoriteRequest ->
                    imageCacheDataSource.clearImageCache()
            }
            emit(Resource.Success(Unit))
        }
    }
}
