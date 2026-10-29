/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.collection

import androidx.paging.PagingConfig
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.collection.Collection
import com.sorrowblue.comicviewer.domain.model.collection.CollectionCriteria
import com.sorrowblue.comicviewer.domain.model.collection.CollectionType
import com.sorrowblue.comicviewer.domain.repository.CollectionRepository
import com.sorrowblue.comicviewer.domain.repository.SettingsRepository
import com.sorrowblue.comicviewer.domain.usecase.PagingUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

/**
 * Observes paginated [Collection] items along with a boolean flag indicating whether
 * the specified file exists in each collection.
 */
fun interface PagingCollectionExistUseCase :
    PagingUseCase<PagingCollectionExistUseCase.Request, Pair<Collection, Boolean>> {

    /**
     * Request parameters for [PagingCollectionExistUseCase].
     *
     * @property pagingConfig Configuration for pagination.
     * @property bookshelfId The bookshelf ID of the file to check.
     * @property path The path of the file to check.
     * @property collectionType The type of collections to query.
     */
    data class Request(
        val pagingConfig: PagingConfig,
        val bookshelfId: BookshelfId,
        val path: String,
        val collectionType: CollectionType,
    )
}

@Inject
@ContributesBinding(AppScope::class)
internal class PagingCollectionExistUseCaseImpl(
    private val collectionRepository: CollectionRepository,
    private val settingsRepository: SettingsRepository,
) : PagingCollectionExistUseCase {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun invoke(request: PagingCollectionExistUseCase.Request) =
        settingsRepository.collectionSettings
            .map { it.recent }
            .distinctUntilChanged()
            .flatMapLatest { recent ->
                collectionRepository.pagingDataFlow(
                    request.pagingConfig,
                    request.bookshelfId,
                    request.path,
                ) {
                    CollectionCriteria(
                        type = request.collectionType,
                        recent = recent,
                    )
                }
            }
}
