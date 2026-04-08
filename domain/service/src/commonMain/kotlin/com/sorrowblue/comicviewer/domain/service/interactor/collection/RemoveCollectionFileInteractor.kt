package com.sorrowblue.comicviewer.domain.service.interactor.collection

import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.service.datasource.CollectionFileLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.collection.RemoveCollectionFileUseCase
import com.sorrowblue.comicviewer.framework.common.scope.DataScope
import dev.zacsweers.metro.ContributesBinding

@ContributesBinding(DataScope::class)
internal class RemoveCollectionFileInteractor(
    private val dataSource: CollectionFileLocalDataSource,
) : RemoveCollectionFileUseCase() {
    override suspend fun run(request: Request): Resource<Unit, Error> {
        dataSource.remove(request.file)
        return Resource.Success(Unit)
    }
}
