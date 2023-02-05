package com.sorrowblue.comicviewer.domain.usecase.interactor

import com.sorrowblue.comicviewer.domain.repository.FavoriteRepository
import com.sorrowblue.comicviewer.domain.usecase.RemoveFavoriteBookUseCase
import com.sorrowblue.comicviewer.framework.Result
import javax.inject.Inject

internal class RemoveFavoriteBookInteractor @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
) : RemoveFavoriteBookUseCase() {
    override suspend fun run(request: Request): Result<Unit, Unit> {
        return Result.Success(
            favoriteRepository.remove(request.favoriteBook)
        )
    }
}
