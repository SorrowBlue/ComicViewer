package com.sorrowblue.comicviewer.domain.service.interactor.favorite

import com.sorrowblue.comicviewer.domain.model.Result
import com.sorrowblue.comicviewer.domain.service.datasource.FavoriteLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.favorite.DeleteFavoriteUseCase
import javax.inject.Inject

internal class DeleteFavoriteInteractor @Inject constructor(
    private val favoriteLocalDataSource: FavoriteLocalDataSource,
) : DeleteFavoriteUseCase() {
    override suspend fun run(request: Request): Result<Unit, Unit> {
        return Result.Success(favoriteLocalDataSource.delete(request.favoriteId))
    }
}
