package com.sorrowblue.comicviewer.domain.interactor.favorite

import com.sorrowblue.comicviewer.domain.entity.favorite.Favorite
import com.sorrowblue.comicviewer.domain.repository.FavoriteRepository
import com.sorrowblue.comicviewer.domain.usecase.favorite.GetFavoriteListUseCase
import com.sorrowblue.comicviewer.framework.Result
import javax.inject.Inject

internal class GetFavoriteListInteractor @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
) : GetFavoriteListUseCase() {
    override suspend fun run(request: Request): Result<List<Favorite>, Unit> {
        return Result.Success(
            favoriteRepository.getFavoriteList(request.bookshelfId, request.filePath)
        )
    }
}