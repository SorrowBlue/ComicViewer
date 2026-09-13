/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.usecase.collection

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.BaseRequest
import com.sorrowblue.comicviewer.domain.model.collection.BasicCollection
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.collection.SmartCollection
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.repository.CollectionFileRepository
import com.sorrowblue.comicviewer.domain.repository.CollectionRepository
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.repository.SettingsRepository
import com.sorrowblue.comicviewer.domain.usecase.PagingUseCase
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest

@Inject
class PagingCollectionFileUseCase(
    private val collectionRepository: CollectionRepository,
    private val collectionFileRepository: CollectionFileRepository,
    private val settingsRepository: SettingsRepository,
    private val fileRepository: FileRepository,
) : PagingUseCase<PagingCollectionFileUseCase.Request, File>() {
    class Request(val pagingConfig: PagingConfig, val collectionId: CollectionId) : BaseRequest

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun run(request: Request): Flow<PagingData<File>> = collectionRepository.flow(
        request.collectionId,
    ).filterNotNull().flatMapLatest { collection ->
        when (collection) {
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
