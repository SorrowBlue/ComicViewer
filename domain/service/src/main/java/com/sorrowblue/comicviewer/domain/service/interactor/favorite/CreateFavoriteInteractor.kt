package com.sorrowblue.comicviewer.domain.service.interactor.favorite

import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.favorite.Favorite
import com.sorrowblue.comicviewer.domain.model.fold
import com.sorrowblue.comicviewer.domain.service.datasource.FavoriteLocalDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.FavoriteLocalDataSourceError
import com.sorrowblue.comicviewer.domain.usecase.favorite.CreateFavoriteUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class CreateFavoriteInteractor @Inject constructor(
    private val favoriteLocalDataSource: FavoriteLocalDataSource,
) : CreateFavoriteUseCase() {

    override fun run(request: Request): Flow<Resource<Favorite, Error>> {
        return flow {
            favoriteLocalDataSource.create(Favorite(request.title)).fold({
                emit(Resource.Success(it))
            }, {
                when (it) {
                    FavoriteLocalDataSourceError.NotFound -> emit(Resource.Error(Error.NotFound))
                    is FavoriteLocalDataSourceError.System -> emit(Resource.Error(Error.System))
                }
            })
        }
    }
}
