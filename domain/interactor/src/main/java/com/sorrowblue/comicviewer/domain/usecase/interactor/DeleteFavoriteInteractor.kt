package com.sorrowblue.comicviewer.domain.usecase.interactor

import com.sorrowblue.comicviewer.domain.repository.FavoriteRepository
import com.sorrowblue.comicviewer.domain.usecase.DeleteFavoriteUseCase
import com.sorrowblue.comicviewer.framework.Result
import javax.inject.Inject

internal class DeleteFavoriteInteractor @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
) : DeleteFavoriteUseCase() {
    override suspend fun run(request: Request): Result<Unit, Unit> {
        return Result.Success(favoriteRepository.delete(request.favoriteId))
    }
}
