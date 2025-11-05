package com.sorrowblue.comicviewer.domain.service.interactor.collection

import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.service.datasource.CollectionFileLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.collection.RemoveCollectionFileUseCase
import dev.zacsweers.metro.Inject

@Inject
internal class RemoveCollectionFileInteractor(
    private val dataSource: CollectionFileLocalDataSource,
) : RemoveCollectionFileUseCase() {

    override suspend fun run(request: Request): Resource<Unit, Error> {
        dataSource.remove(request.file)
        return Resource.Success(Unit)
    }
}
