package com.sorrowblue.comicviewer.domain.service.interactor.favorite

import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.favorite.Favorite
import com.sorrowblue.comicviewer.domain.service.datasource.FavoriteLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.favorite.UpdateFavoriteUseCase
import di.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Singleton

@Singleton
internal class UpdateFavoriteInteractor @Inject constructor(
    private val favoriteLocalDataSource: FavoriteLocalDataSource,
) : UpdateFavoriteUseCase() {

    override fun run(request: Request): Flow<Resource<Favorite, Unit>> {
        return flow { emit(Resource.Success(favoriteLocalDataSource.update(request.favorite))) }
    }
}
