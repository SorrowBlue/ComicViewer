/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.collection

import androidx.paging.PagingConfig
import com.sorrowblue.comicviewer.domain.model.collection.BasicCollection
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.collection.SmartCollection
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.repository.CollectionFileRepository
import com.sorrowblue.comicviewer.domain.repository.CollectionRepository
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.repository.SettingsRepository
import com.sorrowblue.comicviewer.domain.usecase.PagingUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

/**
 * Observes paginated [File] items belonging to a collection.
 */
fun interface PagingCollectionFileUseCase :
    PagingUseCase<PagingCollectionFileUseCase.Request, File> {

    /**
     * Request parameters for [PagingCollectionFileUseCase].
     *
     * @property collectionId The ID of the collection whose files are queried.
     * @property pagingConfig Configuration for pagination.
     */
    data class Request(val collectionId: CollectionId, val pagingConfig: PagingConfig)
}

@Inject
@ContributesBinding(AppScope::class)
internal class PagingCollectionFileUseCaseImpl(
    private val collectionRepository: CollectionRepository,
    private val collectionFileRepository: CollectionFileRepository,
    private val settingsRepository: SettingsRepository,
    private val fileRepository: FileRepository,
) : PagingCollectionFileUseCase {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun invoke(request: PagingCollectionFileUseCase.Request) =
        collectionRepository.flow(request.collectionId)
            .distinctUntilChanged { old, new ->
                when {
                    old is BasicCollection && new is BasicCollection -> old.id == new.id

                    old is SmartCollection && new is SmartCollection ->
                        old.id == new.id &&
                            old.bookshelfId == new.bookshelfId &&
                            old.searchCondition == new.searchCondition

                    else -> old == new
                }
            }
            .flatMapLatest { collection ->
                when (collection) {
                    null -> emptyFlow()

                    is BasicCollection -> {
                        settingsRepository.folderDisplaySettings
                            .map { it.sortType }
                            .distinctUntilChanged()
                            .flatMapLatest { sortType ->
                                collectionFileRepository.pagingDataFlow(
                                    request.collectionId,
                                    request.pagingConfig,
                                ) {
                                    sortType
                                }
                            }
                    }

                    is SmartCollection -> fileRepository.pagingDataFlow(
                        request.pagingConfig,
                        collection.bookshelfId,
                        collection::searchCondition,
                    )
                }
            }
}
