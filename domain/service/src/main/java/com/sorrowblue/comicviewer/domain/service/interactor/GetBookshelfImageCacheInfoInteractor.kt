package com.sorrowblue.comicviewer.domain.service.interactor

import com.sorrowblue.comicviewer.domain.model.BookshelfImageCacheInfo
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.dataOrNull
import com.sorrowblue.comicviewer.domain.model.fold
import com.sorrowblue.comicviewer.domain.service.datasource.BookshelfLocalDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.ImageCacheDataSource
import com.sorrowblue.comicviewer.domain.usecase.ClearImageCacheUseCase
import com.sorrowblue.comicviewer.domain.usecase.GetBookshelfImageCacheInfoUseCase
import com.sorrowblue.comicviewer.domain.usecase.SendFatalErrorUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

internal class GetBookshelfImageCacheInfoInteractor @Inject constructor(
    private val bookshelfLocalDataSource: BookshelfLocalDataSource,
    private val imageCacheDataSource: ImageCacheDataSource,
    private val sendFatalErrorUseCase: SendFatalErrorUseCase,
) : GetBookshelfImageCacheInfoUseCase() {

    override fun run(request: Request): Flow<Resource<List<BookshelfImageCacheInfo>, Unit>> {
        return bookshelfLocalDataSource.allBookshelf().fold(
            onSuccess = { flow -> flow.map { Resource.Success(imageCacheInfoList(it)) } },
            onError = {
                flow {
                    sendFatalErrorUseCase(SendFatalErrorUseCase.Request(it.throwable))
                    emit(Resource.Error(Unit))
                }
            }
        )
    }

    private fun imageCacheInfoList(list: List<Bookshelf>): List<BookshelfImageCacheInfo> {
        return list.mapNotNull {
            imageCacheDataSource.getBookshelfImageCacheInfo(it).dataOrNull()
        }
    }
}

internal class ClearImageCacheInteractor @Inject constructor(
    private val imageCacheDataSource: ImageCacheDataSource,
) : ClearImageCacheUseCase() {

    override suspend fun run(request: Request): Resource<Unit, Unit> {
        when (request) {
            is BookshelfRequest ->
                imageCacheDataSource.clearImageCache(request.bookshelfId, request.imageCache)

            OtherRequest ->
                imageCacheDataSource.clearImageCache()
        }
        return Resource.Success(Unit)
    }
}