package com.sorrowblue.comicviewer.domain.service.interactor.favorite

import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.service.datasource.FavoriteFileLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.favorite.AddFavoriteFileUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class AddFavoriteFileInteractor @Inject constructor(
    private val favoriteFileLocalDataSource: FavoriteFileLocalDataSource,
) : AddFavoriteFileUseCase() {

    override fun run(request: Request): Flow<Resource<Unit, Error>> {
        return flow {
            favoriteFileLocalDataSource.add(request.favoriteFile)
            emit(Resource.Success(Unit))
        }
    }
}
