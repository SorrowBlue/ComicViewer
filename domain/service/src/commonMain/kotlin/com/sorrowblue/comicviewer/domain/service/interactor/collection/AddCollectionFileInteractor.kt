package com.sorrowblue.comicviewer.domain.service.interactor.collection

import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.service.datasource.CollectionFileLocalDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.CollectionLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.collection.AddCollectionFileUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Factory

@Factory
internal class AddCollectionFileInteractor(
    private val collectionLocalDataSource: CollectionLocalDataSource,
    private val collectionFileLocalDataSource: CollectionFileLocalDataSource,
) : AddCollectionFileUseCase() {

    override fun run(request: Request): Flow<Resource<Unit, Error>> {
        return flow {
            collectionFileLocalDataSource.add(request.file)
            collectionLocalDataSource.flow(request.file.id).first()?.let {
                collectionLocalDataSource.update(it)
            }
            emit(Resource.Success(Unit))
        }
    }
}
