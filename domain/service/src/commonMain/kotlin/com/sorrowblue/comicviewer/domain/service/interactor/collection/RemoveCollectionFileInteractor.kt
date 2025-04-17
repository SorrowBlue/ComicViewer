package com.sorrowblue.comicviewer.domain.service.interactor.collection

import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.service.datasource.CollectionFileLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.collection.RemoveCollectionFileUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Factory

@Factory
internal class RemoveCollectionFileInteractor(
    private val dataSource: CollectionFileLocalDataSource,
) : RemoveCollectionFileUseCase() {

    override fun run(request: Request): Flow<Resource<Unit, Error>> {
        return flow {
            dataSource.remove(request.file)
            emit(Resource.Success(Unit))
        }
    }
}
