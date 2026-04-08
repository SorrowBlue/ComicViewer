package com.sorrowblue.comicviewer.domain.service.interactor.collection

import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.service.datasource.CollectionLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.collection.DeleteCollectionUseCase
import com.sorrowblue.comicviewer.framework.common.scope.DataScope
import dev.zacsweers.metro.ContributesBinding

@ContributesBinding(DataScope::class)
internal class DeleteCollectionInteractor(private val dataSource: CollectionLocalDataSource) :
    DeleteCollectionUseCase() {
    override suspend fun run(request: Request): Resource<Unit, Unit> {
        dataSource.delete(request.id)
        return Resource.Success(Unit)
    }
}
