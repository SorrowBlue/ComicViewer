package com.sorrowblue.comicviewer.domain.service.interactor.favorite

import com.sorrowblue.comicviewer.domain.model.Result
import com.sorrowblue.comicviewer.domain.model.favorite.Favorite
import com.sorrowblue.comicviewer.domain.service.repository.FavoriteRepository
import com.sorrowblue.comicviewer.domain.usecase.favorite.UpdateFavoriteUseCase
import javax.inject.Inject

internal class UpdateFavoriteInteractor @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
) : UpdateFavoriteUseCase() {
    override suspend fun run(request: Request): Result<Favorite, Unit> {
        return Result.Success(favoriteRepository.update(request.favorite))
    }
}
