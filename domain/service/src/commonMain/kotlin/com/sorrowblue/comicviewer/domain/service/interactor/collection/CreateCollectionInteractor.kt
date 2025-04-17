package com.sorrowblue.comicviewer.domain.service.interactor.collection

import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.collection.Collection
import com.sorrowblue.comicviewer.domain.service.datasource.CollectionLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.collection.CreateCollectionUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Factory

@Factory
internal class CreateCollectionInteractor(
    private val dataSource: CollectionLocalDataSource,
) : CreateCollectionUseCase() {

    override fun run(request: Request): Flow<Resource<Collection, Error>> {
        return flow {
            emit(Resource.Success(dataSource.create(request.collection)))
        }
    }
}
