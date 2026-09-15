/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.file

import androidx.paging.PagingConfig
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.usecase.PagingUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

fun interface PagingHistoryBookUseCase : PagingUseCase<PagingConfig, Book>

@Inject
@ContributesBinding(AppScope::class)
internal class PagingHistoryBookUseCaseImpl(private val fileRepository: FileRepository) :
    PagingHistoryBookUseCase {

    override fun invoke(request: PagingConfig) = fileRepository.pagingHistoryBookSource(request)
}
