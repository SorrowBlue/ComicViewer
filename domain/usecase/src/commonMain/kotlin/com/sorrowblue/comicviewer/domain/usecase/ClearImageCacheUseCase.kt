package com.sorrowblue.comicviewer.domain.usecase

import com.sorrowblue.comicviewer.domain.model.ImageCache
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId

abstract class ClearImageCacheUseCase :
    OneShotUseCase<ClearImageCacheUseCase.Request, Unit, Unit>() {

    sealed interface Request : OneShotUseCase.Request

    data class BookshelfRequest(
        val bookshelfId: BookshelfId,
        val imageCache: ImageCache,
    ) : Request

    data object OtherRequest : Request
}
