package com.sorrowblue.comicviewer.domain.usecase.interactor

import com.sorrowblue.comicviewer.domain.repository.FavoriteRepository
import com.sorrowblue.comicviewer.domain.usecase.CreateFavoriteUseCase
import com.sorrowblue.comicviewer.framework.Result
import javax.inject.Inject

internal class CreateFavoriteInteractor @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
) : CreateFavoriteUseCase() {
    override suspend fun run(request: Request): Result<Unit, Unit> {
        return Result.Success(
            favoriteRepository.create(request.title)
        )
    }
}
