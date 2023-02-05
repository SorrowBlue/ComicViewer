package com.sorrowblue.comicviewer.domain.usecase.interactor

import com.sorrowblue.comicviewer.domain.entity.favorite.Favorite
import com.sorrowblue.comicviewer.domain.repository.FavoriteRepository
import com.sorrowblue.comicviewer.domain.usecase.GetFavoriteUseCase
import com.sorrowblue.comicviewer.framework.Result
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class GetFavoriteInteractor @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
) : GetFavoriteUseCase() {

    override fun run(request: Request): Flow<Result<Favorite, Unit>> {
        return favoriteRepository.get(request.favoriteId).map { Result.Success(it) }
    }
}
