package com.sorrowblue.comicviewer.domain.service.interactor.collection

import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.collection.Collection
import com.sorrowblue.comicviewer.domain.service.datasource.CollectionLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.collection.PagingCollectionUseCase
import com.sorrowblue.comicviewer.framework.common.scope.DataScope
import dev.zacsweers.metro.ContributesBinding
import kotlinx.coroutines.flow.Flow

@ContributesBinding(DataScope::class)
internal class PagingCollectionInteractor(private val dataSource: CollectionLocalDataSource) :
    PagingCollectionUseCase() {
    override fun run(request: Request): Flow<PagingData<Collection>> =
        dataSource.pagingDataFlow(request.pagingConfig)
}
