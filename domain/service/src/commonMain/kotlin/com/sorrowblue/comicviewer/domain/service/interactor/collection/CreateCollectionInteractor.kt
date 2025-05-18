package com.sorrowblue.comicviewer.domain.service.interactor.collection

import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.collection.Collection
import com.sorrowblue.comicviewer.domain.service.datasource.CollectionLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.collection.CreateCollectionUseCase
import org.koin.core.annotation.Factory

@Factory
internal class CreateCollectionInteractor(
    private val dataSource: CollectionLocalDataSource,
) : CreateCollectionUseCase() {

    override suspend fun run(request: Request): Resource<Collection, Error> {
        return Resource.Success(dataSource.create(request.collection))
    }
}
