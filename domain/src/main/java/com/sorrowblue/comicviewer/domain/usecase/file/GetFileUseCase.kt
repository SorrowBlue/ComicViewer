package com.sorrowblue.comicviewer.domain.usecase.file

import com.sorrowblue.comicviewer.domain.entity.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.request.BaseRequest
import com.sorrowblue.comicviewer.domain.usecase.FlowOneUseCase
import com.sorrowblue.comicviewer.domain.usecase.GetLibraryInfoError

abstract class GetFileUseCase :
    FlowOneUseCase<GetFileUseCase.Request, File, GetLibraryInfoError>() {
    class Request(val bookshelfId: BookshelfId, val path: String) : BaseRequest {
        override fun validate(): Boolean {
            return true
        }
    }
}
