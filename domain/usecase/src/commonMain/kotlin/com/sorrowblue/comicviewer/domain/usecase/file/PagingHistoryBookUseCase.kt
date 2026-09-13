/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.file

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.BaseRequest
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.usecase.PagingUseCase
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

@Inject
class PagingHistoryBookUseCase(private val fileRepository: FileRepository) :
    PagingUseCase<PagingHistoryBookUseCase.Request, Book>() {

    class Request(val pagingConfig: PagingConfig) : BaseRequest

    override fun run(request: Request): Flow<PagingData<Book>> =
        fileRepository.pagingHistoryBookSource(request.pagingConfig)
}
