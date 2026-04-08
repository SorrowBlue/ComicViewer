package com.sorrowblue.comicviewer.domain.service.interactor.collection

import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.service.datasource.CollectionFileLocalDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.CollectionLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.collection.AddCollectionFileUseCase
import com.sorrowblue.comicviewer.framework.common.scope.DataScope
import dev.zacsweers.metro.ContributesBinding
import kotlinx.coroutines.flow.first

@ContributesBinding(DataScope::class)
internal class AddCollectionFileInteractor(
    private val collectionLocalDataSource: CollectionLocalDataSource,
    private val collectionFileLocalDataSource: CollectionFileLocalDataSource,
) : AddCollectionFileUseCase() {
    override suspend fun run(request: Request): Resource<Unit, Error> {
        collectionFileLocalDataSource.add(request.file)
        collectionLocalDataSource.flow(request.file.id).first()?.let {
            collectionLocalDataSource.update(it)
        }
        return Resource.Success(Unit)
    }
}
