package com.sorrowblue.comicviewer.domain.usecase.interactor

import com.sorrowblue.comicviewer.domain.repository.FavoriteRepository
import com.sorrowblue.comicviewer.domain.usecase.AddFavoriteBookUseCase
import com.sorrowblue.comicviewer.framework.Result
import javax.inject.Inject

internal class AddFavoriteBookInteractor @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
) : AddFavoriteBookUseCase() {
    override suspend fun run(request: Request): Result<Unit, Unit> {
        return Result.Success(
            favoriteRepository.add(request.favoriteBook)
        )
    }
}
