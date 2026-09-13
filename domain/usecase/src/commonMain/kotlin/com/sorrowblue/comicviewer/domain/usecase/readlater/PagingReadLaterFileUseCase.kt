/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.readlater

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.BaseRequest
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.repository.ReadLaterFileRepository
import com.sorrowblue.comicviewer.domain.usecase.PagingUseCase
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

@Inject
class PagingReadLaterFileUseCase(private val readLaterFileRepository: ReadLaterFileRepository) :
    PagingUseCase<PagingReadLaterFileUseCase.Request, File>() {
    class Request(val pagingConfig: PagingConfig) : BaseRequest

    override fun run(request: Request): Flow<PagingData<File>> =
        readLaterFileRepository.pagingDataFlow(request.pagingConfig)
}
