package com.sorrowblue.comicviewer.domain.service.interactor.favorite

import com.sorrowblue.comicviewer.domain.service.repository.FavoriteFileRepository
import com.sorrowblue.comicviewer.domain.usecase.favorite.AddFavoriteFileUseCase
import com.sorrowblue.comicviewer.framework.Result
import javax.inject.Inject

internal class AddFavoriteFileInteractor @Inject constructor(
    private val favoriteFileRepository: FavoriteFileRepository,
) : AddFavoriteFileUseCase() {
    override suspend fun run(request: Request): Result<Unit, Unit> {
        return Result.Success(
            favoriteFileRepository.add(request.favoriteFile)
        )
    }
}
