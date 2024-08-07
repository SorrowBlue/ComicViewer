package com.sorrowblue.comicviewer.domain.service.interactor.favorite

import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.service.datasource.FavoriteFileLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.favorite.RemoveFavoriteFileUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class RemoveFavoriteFileInteractor @Inject constructor(
    private val favoriteFileLocalDataSource: FavoriteFileLocalDataSource,
) : RemoveFavoriteFileUseCase() {

    override fun run(request: Request): Flow<Resource<Unit, Resource.SystemError>> {
        return flow {
            when (val result = favoriteFileLocalDataSource.delete(request.favoriteFile)) {
                is Resource.Error -> emit(result)
                is Resource.Success -> emit(Resource.Success(Unit))
            }
        }
    }
}
