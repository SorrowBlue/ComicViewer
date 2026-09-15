/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.readlater

import androidx.paging.PagingConfig
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.repository.ReadLaterFileRepository
import com.sorrowblue.comicviewer.domain.usecase.PagingUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

fun interface PagingReadLaterFileUseCase : PagingUseCase<PagingConfig, File>

@Inject
@ContributesBinding(AppScope::class)
internal class PagingReadLaterFileUseCaseImpl(private val repository: ReadLaterFileRepository) :
    PagingReadLaterFileUseCase {

    override fun invoke(request: PagingConfig) = repository.pagingDataFlow(request)
}
