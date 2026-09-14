/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.collection

import androidx.paging.PagingConfig
import com.sorrowblue.comicviewer.domain.model.collection.Collection
import com.sorrowblue.comicviewer.domain.repository.CollectionRepository
import com.sorrowblue.comicviewer.domain.usecase.PagingUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject

fun interface PagingCollectionUseCase : PagingUseCase<PagingConfig, Collection>

@Inject
@ContributesBinding(AppScope::class)
internal class PagingCollectionUseCaseImpl(private val repository: CollectionRepository) :
    PagingCollectionUseCase {

    override fun invoke(request: PagingConfig) = repository.pagingDataFlow(request)
}
