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
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest

fun interface PagingCollectionFileUseCase :
    PagingUseCase<PagingCollectionFileUseCase.Request, File> {

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
            .flatMapLatest { collection ->
                when (collection) {
                    null -> emptyFlow()

                    is BasicCollection -> {
                        settingsRepository.folderDisplaySettings.flatMapLatest { settings ->
                            collectionFileRepository.pagingDataFlow(
                                request.collectionId,
                                request.pagingConfig,
                            ) {
                                settings.sortType
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
