package com.sorrowblue.comicviewer.domain.service.interactor.favorite

import com.sorrowblue.comicviewer.domain.model.Result
import com.sorrowblue.comicviewer.domain.service.datasource.FavoriteFileLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.favorite.AddFavoriteFileUseCase
import javax.inject.Inject

internal class AddFavoriteFileInteractor @Inject constructor(
    private val favoriteFileLocalDataSource: FavoriteFileLocalDataSource,
) : AddFavoriteFileUseCase() {
    override suspend fun run(request: Request): Result<Unit, Unit> {
        return Result.Success(favoriteFileLocalDataSource.add(request.favoriteFile))
    }
}
