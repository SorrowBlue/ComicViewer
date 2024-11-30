package com.sorrowblue.comicviewer.domain.usecase.paging

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.usecase.UseCase
import kotlinx.coroutines.flow.Flow

abstract class PagingFileUseCase :
    UseCase<PagingFileUseCase.Request, Flow<PagingData<File>>, PagingFileUseCase.Error>() {

    data class Request(
        val pagingConfig: PagingConfig,
        val bookshelfId: BookshelfId,
        val path: String,
    ) : UseCase.Request

    enum class Error : Resource.AppError {
        NOT_FOUND,
    }
}
