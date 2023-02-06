package com.sorrowblue.comicviewer.domain.interactor.favorite

import com.sorrowblue.comicviewer.domain.repository.FavoriteRepository
import com.sorrowblue.comicviewer.domain.usecase.favorite.RemoveFavoriteFileUseCase
import com.sorrowblue.comicviewer.framework.Result
import javax.inject.Inject

internal class RemoveFavoriteFileInteractor @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
) : RemoveFavoriteFileUseCase() {
    override suspend fun run(request: Request): Result<Unit, Unit> {
        return Result.Success(
            favoriteRepository.remove(request.favoriteFile)
        )
    }
}
