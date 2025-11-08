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
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.runBlocking

@Inject
internal class PagingCollectionFileInteractor(
    private val dataSource: CollectionLocalDataSource,
    private val collectionFileLocalDataSource: CollectionFileLocalDataSource,
    private val datastoreDataSource: DatastoreDataSource,
    private val fileLocalDataSource: FileLocalDataSource,
) : PagingCollectionFileUseCase() {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun run(request: Request): Flow<PagingData<File>> =
        dataSource.flow(request.collectionId).filterNotNull().flatMapLatest {
            when (it) {
                is BasicCollection -> collectionFileLocalDataSource.pagingDataFlow(
                    request.collectionId,
                    request.pagingConfig,
                ) {
                    runBlocking { datastoreDataSource.folderDisplaySettings.first() }.sortType
                }

                is SmartCollection -> fileLocalDataSource.pagingDataFlow(
                    request.pagingConfig,
                    it.bookshelfId,
                    it::searchCondition,
                )
            }
        }
}
