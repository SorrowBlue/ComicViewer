package com.sorrowblue.comicviewer.domain.service.interactor.favorite

import com.sorrowblue.comicviewer.domain.model.Result
import com.sorrowblue.comicviewer.domain.service.repository.FavoriteFileRepository
import com.sorrowblue.comicviewer.domain.usecase.favorite.RemoveFavoriteFileUseCase
import javax.inject.Inject

internal class RemoveFavoriteFileInteractor @Inject constructor(
    private val favoriteFileRepository: FavoriteFileRepository,
) : RemoveFavoriteFileUseCase() {
    override suspend fun run(request: Request): Result<Unit, Unit> {
        return Result.Success(favoriteFileRepository.delete(request.favoriteFile))
    }
}
