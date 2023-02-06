package com.sorrowblue.comicviewer.domain.interactor.favorite

import com.sorrowblue.comicviewer.domain.repository.FavoriteRepository
import com.sorrowblue.comicviewer.domain.usecase.favorite.AddFavoriteFileUseCase
import com.sorrowblue.comicviewer.framework.Result
import javax.inject.Inject

internal class AddFavoriteFileInteractor @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
) : AddFavoriteFileUseCase() {
    override suspend fun run(request: Request): Result<Unit, Unit> {
        return Result.Success(
            favoriteRepository.add(request.favoriteFile)
        )
    }
}
