package com.sorrowblue.comicviewer.domain.service.interactor.favorite

import com.sorrowblue.comicviewer.domain.model.Result
import com.sorrowblue.comicviewer.domain.service.datasource.FavoriteFileLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.favorite.RemoveFavoriteFileUseCase
import javax.inject.Inject

internal class RemoveFavoriteFileInteractor @Inject constructor(
    private val favoriteFileLocalDataSource: FavoriteFileLocalDataSource,
) : RemoveFavoriteFileUseCase() {
    override suspend fun run(request: Request): Result<Unit, Unit> {
        return Result.Success(favoriteFileLocalDataSource.delete(request.favoriteFile))
    }
}
