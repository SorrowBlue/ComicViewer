package com.sorrowblue.comicviewer.domain.usecase.bookshelf

import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.usecase.UseCase

@Deprecated("Use ScanBookshelfUseCase2 instead")
abstract class ScanBookshelfUseCase :
    UseCase<ScanBookshelfUseCase.Request, List<File>, ScanBookshelfUseCase.Error>() {

    class Request(
        val bookshelfId: BookshelfId,
        val process: suspend (Bookshelf, File) -> Unit,
    ) : UseCase.Request

    enum class Error : Resource.AppError {
        System,
    }
}
