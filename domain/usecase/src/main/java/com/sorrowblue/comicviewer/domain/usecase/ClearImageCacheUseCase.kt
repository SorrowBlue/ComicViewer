package com.sorrowblue.comicviewer.domain.usecase

import com.sorrowblue.comicviewer.domain.model.BookshelfImageCacheInfo
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId

abstract class ClearImageCacheUseCase :
    UseCase<ClearImageCacheUseCase.Request, Unit, ClearImageCacheUseCase.Error>() {

    sealed interface Request : UseCase.Request

    data class BookshelfRequest(
        val bookshelfId: BookshelfId,
        val type: BookshelfImageCacheInfo.Type,
    ) : Request

    data object FavoriteRequest : Request

    enum class Error : Resource.AppError {
        System,
    }
}
