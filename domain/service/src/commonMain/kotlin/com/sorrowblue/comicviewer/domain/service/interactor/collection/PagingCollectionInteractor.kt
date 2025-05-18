package com.sorrowblue.comicviewer.domain.service.interactor.collection

import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.collection.Collection
import com.sorrowblue.comicviewer.domain.service.datasource.CollectionLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.collection.PagingCollectionUseCase
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory

@Factory
internal class PagingCollectionInteractor(
    private val dataSource: CollectionLocalDataSource,
) : PagingCollectionUseCase() {

    override fun run(request: Request): Flow<PagingData<Collection>> {
        return dataSource.pagingDataFlow(request.pagingConfig)
    }
}
