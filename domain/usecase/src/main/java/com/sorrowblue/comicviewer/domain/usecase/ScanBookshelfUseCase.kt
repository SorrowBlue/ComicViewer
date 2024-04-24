package com.sorrowblue.comicviewer.domain.usecase

import com.sorrowblue.comicviewer.domain.BaseRequest
import com.sorrowblue.comicviewer.domain.model.Scan
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.IFolder

abstract class ScanBookshelfUseCase :
    FlowOneUseCase<ScanBookshelfUseCase.Request, String, Unit>() {

    class Request(val bookshelfId: BookshelfId) : BaseRequest
}
