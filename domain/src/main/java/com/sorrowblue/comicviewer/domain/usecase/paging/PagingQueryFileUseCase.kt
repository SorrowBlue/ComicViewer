package com.sorrowblue.comicviewer.domain.usecase.paging

import androidx.paging.PagingConfig
import com.sorrowblue.comicviewer.domain.entity.SearchCondition
import com.sorrowblue.comicviewer.domain.entity.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.entity.file.File
import com.sorrowblue.comicviewer.domain.entity.settings.SortType
import com.sorrowblue.comicviewer.domain.request.BaseRequest

abstract class PagingQueryFileUseCase : PagingUseCase<PagingQueryFileUseCase.Request, File>() {

    class Request(
        val pagingConfig: PagingConfig,
        val bookshelf: Bookshelf,
        val searchCondition: SearchCondition,
        val sortType: () -> SortType
    ) : BaseRequest {

        override fun validate(): Boolean {
            return true
        }
    }
}
