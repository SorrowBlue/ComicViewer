package com.sorrowblue.comicviewer.domain.usecase.paging

import androidx.paging.PagingConfig
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.entity.file.Folder
import com.sorrowblue.comicviewer.domain.entity.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.request.BaseRequest

abstract class PagingFileUseCase : PagingUseCase<PagingFileUseCase.Request, File>() {

    class Request(val pagingConfig: PagingConfig, val bookshelf: Bookshelf, val folder: Folder) :
        BaseRequest {
        override fun validate(): Boolean {
            return true
        }
    }
}
