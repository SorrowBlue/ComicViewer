package com.sorrowblue.comicviewer.domain.service.interactor.favorite

import com.sorrowblue.comicviewer.domain.model.Result
import com.sorrowblue.comicviewer.domain.model.favorite.Favorite
import com.sorrowblue.comicviewer.domain.service.datasource.FavoriteLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.favorite.GetFavoriteUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class GetFavoriteInteractor @Inject constructor(
    private val favoriteLocalDataSource: FavoriteLocalDataSource,
) : GetFavoriteUseCase() {

    override fun run(request: Request): Flow<Result<Favorite, Unit>> {
        return favoriteLocalDataSource.flow(request.favoriteId).map { Result.Success(it) }
    }
}
