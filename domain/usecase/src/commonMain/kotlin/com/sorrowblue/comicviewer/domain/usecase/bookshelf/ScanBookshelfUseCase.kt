package com.sorrowblue.comicviewer.domain.usecase.bookshelf

import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.usecase.OneShotUseCase

abstract class ScanBookshelfUseCase :
    OneShotUseCase<ScanBookshelfUseCase.Request, List<File>, ScanBookshelfUseCase.Error>() {

    class Request(
        val bookshelfId: BookshelfId,
        val process: suspend (Bookshelf, File) -> Unit,
    ) : OneShotUseCase.Request

    enum class Error : Resource.AppError {
        System,
    }
}
