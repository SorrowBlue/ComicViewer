package com.sorrowblue.comicviewer.domain.service.interactor.collection

import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.collection.Collection
import com.sorrowblue.comicviewer.domain.service.datasource.CollectionLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.collection.GetCollectionUseCase
import com.sorrowblue.comicviewer.framework.common.scope.DataScope
import dev.zacsweers.metro.ContributesBinding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@ContributesBinding(DataScope::class)
internal class GetCollectionInteractor(private val dataSource: CollectionLocalDataSource) :
    GetCollectionUseCase() {
    override fun run(request: Request): Flow<Resource<Collection, Error>> =
        dataSource.flow(request.id).map {
            if (it == null) {
                Resource.Error(Error.NotFound)
            } else {
                Resource.Success(it)
            }
        }
}
