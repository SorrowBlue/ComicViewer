package com.sorrowblue.comicviewer.domain.usecase

import com.sorrowblue.comicviewer.domain.model.BookshelfImageCacheInfo

abstract class GetBookshelfImageCacheInfoUseCase :
    UseCase<GetBookshelfImageCacheInfoUseCase.Request, List<BookshelfImageCacheInfo>, Unit>() {
    object Request : UseCase.Request
}
