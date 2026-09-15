/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.collection

import androidx.paging.PagingConfig
import com.sorrowblue.comicviewer.domain.BaseRequest
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.collection.Collection
import com.sorrowblue.comicviewer.domain.model.collection.CollectionCriteria
import com.sorrowblue.comicviewer.domain.model.collection.CollectionType
import com.sorrowblue.comicviewer.domain.repository.CollectionRepository
import com.sorrowblue.comicviewer.domain.usecase.PagingUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.CollectionSettingsUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

fun interface PagingCollectionExistUseCase :
    PagingUseCase<PagingCollectionExistUseCase.Request, Pair<Collection, Boolean>> {

    data class Request(
        val pagingConfig: PagingConfig,
        val bookshelfId: BookshelfId,
        val path: String,
        val collectionType: CollectionType,
    ) : BaseRequest
}

@Inject
@ContributesBinding(AppScope::class)
internal class PagingCollectionExistUseCaseImpl(
    private val collectionRepository: CollectionRepository,
    private val collectionSettingsUseCase: CollectionSettingsUseCase,
) : PagingCollectionExistUseCase {
    override fun invoke(request: PagingCollectionExistUseCase.Request) =
        collectionRepository.pagingDataFlow(
            request.pagingConfig,
            request.bookshelfId,
            request.path,
        ) {
            val collectionSettings = runBlocking { collectionSettingsUseCase.settings.first() }
            CollectionCriteria(type = request.collectionType, recent = collectionSettings.recent)
        }
}
