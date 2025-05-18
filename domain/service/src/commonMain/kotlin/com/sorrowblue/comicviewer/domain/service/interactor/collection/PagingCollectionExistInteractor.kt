package com.sorrowblue.comicviewer.domain.service.interactor.collection

import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.collection.Collection
import com.sorrowblue.comicviewer.domain.model.collection.CollectionCriteria
import com.sorrowblue.comicviewer.domain.service.datasource.CollectionLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.collection.PagingCollectionExistUseCase
import com.sorrowblue.comicviewer.domain.usecase.settings.CollectionSettingsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.koin.core.annotation.Factory

@Factory
internal class PagingCollectionExistInteractor(
    private val dataSource: CollectionLocalDataSource,
    private val collectionSettingsUseCase: CollectionSettingsUseCase,
) : PagingCollectionExistUseCase() {

    override fun run(request: Request): Flow<PagingData<Pair<Collection, Boolean>>> {
        return dataSource.pagingDataFlow(
            request.pagingConfig,
            request.bookshelfId,
            request.path,
        ) {
            val collectionSettings = runBlocking { collectionSettingsUseCase.settings.first() }
            CollectionCriteria(type = request.collectionType, recent = collectionSettings.recent)
        }
    }
}
