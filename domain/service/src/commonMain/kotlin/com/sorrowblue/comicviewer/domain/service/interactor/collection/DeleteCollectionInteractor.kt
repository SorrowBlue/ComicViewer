package com.sorrowblue.comicviewer.domain.service.interactor.collection

import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.service.datasource.CollectionLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.collection.DeleteCollectionUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Singleton

@Singleton
internal class DeleteCollectionInteractor(
    private val dataSource: CollectionLocalDataSource,
) : DeleteCollectionUseCase() {

    override fun run(request: Request): Flow<Resource<Unit, Unit>> {
        return flow {
            dataSource.delete(request.id)
            emit(Resource.Success(Unit))
        }
    }
}
