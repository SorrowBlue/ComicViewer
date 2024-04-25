package com.sorrowblue.comicviewer.domain.service.interactor.favorite

import com.sorrowblue.comicviewer.domain.model.Result
import com.sorrowblue.comicviewer.domain.model.favorite.Favorite
import com.sorrowblue.comicviewer.domain.service.datasource.FavoriteLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.favorite.CreateFavoriteUseCase
import javax.inject.Inject

internal class CreateFavoriteInteractor @Inject constructor(
    private val favoriteLocalDataSource: FavoriteLocalDataSource,
) : CreateFavoriteUseCase() {
    override suspend fun run(request: Request): Result<Unit, Unit> {
        return Result.Success(favoriteLocalDataSource.create(Favorite(request.title)))
    }
}
