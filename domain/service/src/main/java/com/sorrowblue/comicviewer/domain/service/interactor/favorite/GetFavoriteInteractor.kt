package com.sorrowblue.comicviewer.domain.service.interactor.favorite

import com.sorrowblue.comicviewer.domain.model.Result
import com.sorrowblue.comicviewer.domain.model.favorite.Favorite
import com.sorrowblue.comicviewer.domain.service.repository.FavoriteRepository
import com.sorrowblue.comicviewer.domain.usecase.favorite.GetFavoriteUseCase
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
