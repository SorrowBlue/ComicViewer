package com.sorrowblue.comicviewer.domain.service.interactor.favorite

import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.service.datasource.FavoriteLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.favorite.DeleteFavoriteUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class DeleteFavoriteInteractor @Inject constructor(
    private val favoriteLocalDataSource: FavoriteLocalDataSource,
) : DeleteFavoriteUseCase() {

    override fun run(request: Request): Flow<Resource<Unit, Unit>> {
        return flow {
            emit(Resource.Success(favoriteLocalDataSource.delete(request.favoriteId)))
        }
    }
}
