package com.sorrowblue.comicviewer.domain.service.interactor.collection

import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.service.datasource.CollectionLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.collection.UpdateCollectionUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Factory

@Factory
internal class UpdateCollectionInteractor(
    private val dataSource: CollectionLocalDataSource,
) : UpdateCollectionUseCase() {

    override fun run(request: Request): Flow<Resource<Unit, Error>> {
        return flow {
            dataSource.update(request.collection)
            emit(Resource.Success(Unit))
        }
    }
}
