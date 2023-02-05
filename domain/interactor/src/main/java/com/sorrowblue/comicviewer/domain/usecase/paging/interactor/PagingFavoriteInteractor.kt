package com.sorrowblue.comicviewer.domain.usecase.paging.interactor

import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.entity.favorite.Favorite
import com.sorrowblue.comicviewer.domain.repository.FavoriteRepository
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingFavoriteUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

internal class PagingFavoriteInteractor @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
) : PagingFavoriteUseCase() {

    override fun run(request: Request): Flow<PagingData<Favorite>> {
        return favoriteRepository.pagingDataFlow(request.pagingConfig)
    }
}
