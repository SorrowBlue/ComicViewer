package com.sorrowblue.comicviewer.domain.service.interactor.collection

import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.service.datasource.CollectionLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.collection.UpdateCollectionUseCase
import org.koin.core.annotation.Factory

@Factory
internal class UpdateCollectionInteractor(
    private val dataSource: CollectionLocalDataSource,
) : UpdateCollectionUseCase() {

    override suspend fun run(request: Request): Resource<Unit, Error> {
        dataSource.update(request.collection)
        return Resource.Success(Unit)
    }
}
