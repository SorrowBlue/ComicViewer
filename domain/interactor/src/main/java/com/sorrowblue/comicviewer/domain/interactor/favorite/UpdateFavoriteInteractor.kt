package com.sorrowblue.comicviewer.domain.interactor.favorite

import com.sorrowblue.comicviewer.domain.entity.favorite.Favorite
import com.sorrowblue.comicviewer.domain.repository.FavoriteRepository
import com.sorrowblue.comicviewer.domain.usecase.favorite.UpdateFavoriteUseCase
import com.sorrowblue.comicviewer.framework.Result
import javax.inject.Inject

internal class UpdateFavoriteInteractor @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
) : UpdateFavoriteUseCase() {
    override suspend fun run(request: Request): Result<Favorite, Unit> {
        return Result.Success(favoriteRepository.update(request.favorite))
    }
}
