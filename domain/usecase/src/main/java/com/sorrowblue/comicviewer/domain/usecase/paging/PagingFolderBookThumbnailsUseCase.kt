package com.sorrowblue.comicviewer.domain.usecase.paging

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.domain.usecase.UseCase
import kotlinx.coroutines.flow.Flow

abstract class PagingFolderBookThumbnailsUseCase :
    UseCase<PagingFolderBookThumbnailsUseCase.Request, Flow<PagingData<BookThumbnail>>, PagingFolderBookThumbnailsUseCase.Error>() {

    class Request(val bookshelfId: BookshelfId, val path: String, val pagingConfig: PagingConfig) :
        UseCase.Request

    enum class Error : Resource.AppError {
        NOT_FOUND,
    }
}
