package com.sorrowblue.comicviewer.domain.service.interactor.bookshelf

import com.sorrowblue.comicviewer.domain.model.BookPageImageCache
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.ThumbnailImageCache
import com.sorrowblue.comicviewer.domain.model.fold
import com.sorrowblue.comicviewer.domain.model.isSuccess
import com.sorrowblue.comicviewer.domain.model.onError
import com.sorrowblue.comicviewer.domain.service.datasource.BookshelfLocalDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.ImageCacheDataSource
import com.sorrowblue.comicviewer.domain.usecase.SendFatalErrorUseCase
import com.sorrowblue.comicviewer.domain.usecase.bookshelf.RemoveBookshelfUseCase
import javax.inject.Inject

internal class RemoveBookshelfInteractor @Inject constructor(
    private val bookshelfLocalDataSource: BookshelfLocalDataSource,
    private val imageCacheDataSource: ImageCacheDataSource,
    private val sendFatalErrorUseCase: SendFatalErrorUseCase,
) : RemoveBookshelfUseCase() {

    override suspend fun run(request: Request): Resource<Unit, Unit> {
        return bookshelfLocalDataSource.delete(request.bookshelfId).fold(
            onSuccess = { _ ->
                val pageResult = imageCacheDataSource.clearImageCache(
                    request.bookshelfId,
                    BookPageImageCache(0, 0)
                )
                val thumbnailResult = imageCacheDataSource.clearImageCache(
                    request.bookshelfId,
                    ThumbnailImageCache(0, 0)
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
            }
        )
    }
}
