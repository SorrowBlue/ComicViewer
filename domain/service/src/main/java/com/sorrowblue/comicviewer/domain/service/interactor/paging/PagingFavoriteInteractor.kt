package com.sorrowblue.comicviewer.domain.service.interactor.paging

import androidx.paging.PagingData
import androidx.paging.map
import com.sorrowblue.comicviewer.domain.model.favorite.Favorite
import com.sorrowblue.comicviewer.domain.service.datasource.FavoriteLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingFavoriteUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class PagingFavoriteInteractor @Inject constructor(
    private val favoriteLocalDataSource: FavoriteLocalDataSource,
) : PagingFavoriteUseCase() {

    override fun run(request: Request): Flow<PagingData<Favorite>> {
        return favoriteLocalDataSource
            .pagingDataFlow(request.pagingConfig, request.bookshelfId, request.path)
            .map { pagingData ->
                pagingData.map {
                    Favorite(it.id, it.name, it.count, it.exist)
                }
            }
    }
}
