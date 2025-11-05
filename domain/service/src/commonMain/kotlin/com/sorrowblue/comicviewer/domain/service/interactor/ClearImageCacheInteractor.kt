package com.sorrowblue.comicviewer.domain.service.interactor

import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.service.datasource.FileLocalDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.ImageCacheDataSource
import com.sorrowblue.comicviewer.domain.usecase.ClearImageCacheUseCase
import dev.zacsweers.metro.Inject

@Inject
internal class ClearImageCacheInteractor(
    private val imageCacheDataSource: ImageCacheDataSource,
    private val localDataSource: FileLocalDataSource,
) : ClearImageCacheUseCase() {

    override suspend fun run(request: Request): Resource<Unit, Unit> {
        when (request) {
            is BookshelfRequest -> {
                localDataSource.clearCacheKey(request.bookshelfId)
                imageCacheDataSource.clearImageCache(request.bookshelfId, request.imageCache)
            }

            OtherRequest ->
                imageCacheDataSource.clearImageCache()
        }
        return Resource.Success(Unit)
    }
}
