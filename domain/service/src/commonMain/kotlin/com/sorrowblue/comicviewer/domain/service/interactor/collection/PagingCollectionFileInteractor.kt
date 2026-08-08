/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.domain.service.interactor.collection

import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.collection.BasicCollection
import com.sorrowblue.comicviewer.domain.model.collection.SmartCollection
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.service.datasource.CollectionFileLocalDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.CollectionLocalDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.DatastoreDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.FileLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.collection.PagingCollectionFileUseCase
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest

@ContributesBinding(AppScope::class)
internal class PagingCollectionFileInteractor(
    private val dataSource: CollectionLocalDataSource,
    private val collectionFileLocalDataSource: CollectionFileLocalDataSource,
    private val datastoreDataSource: DatastoreDataSource,
    private val fileLocalDataSource: FileLocalDataSource,
) : PagingCollectionFileUseCase() {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun run(request: Request): Flow<PagingData<File>> =
        dataSource.flow(request.collectionId).filterNotNull().flatMapLatest { collection ->
            when (collection) {
                is BasicCollection -> {
                    datastoreDataSource.folderDisplaySettings.flatMapLatest { settings ->
                        collectionFileLocalDataSource.pagingDataFlow(
                            request.collectionId,
                            request.pagingConfig,
                        ) {
                            settings.sortType
                        }
                    }
                }

                is SmartCollection -> fileLocalDataSource.pagingDataFlow(
                    request.pagingConfig,
                    collection.bookshelfId,
                    collection::searchCondition,
                )
            }
        }
}
