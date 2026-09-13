/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.file

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.BaseRequest
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.usecase.PagingUseCase
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

class PagingBookshelfBookUseCase(
    private val action: (Request) -> Flow<PagingData<BookThumbnail>>,
) : PagingUseCase<PagingBookshelfBookUseCase.Request, BookThumbnail>() {

    @Inject
    constructor(fileRepository: FileRepository) : this({ request ->
        fileRepository.pagingSource(request.bookshelfId, request.pagingConfig)
    })

    class Request(val bookshelfId: BookshelfId, val pagingConfig: PagingConfig) : BaseRequest

    override fun run(request: Request): Flow<PagingData<BookThumbnail>> = action(request)
}
