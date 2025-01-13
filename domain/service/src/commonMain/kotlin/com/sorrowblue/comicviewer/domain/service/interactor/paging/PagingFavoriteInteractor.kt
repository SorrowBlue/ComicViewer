package com.sorrowblue.comicviewer.domain.service.interactor.paging

import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.favorite.Favorite
import com.sorrowblue.comicviewer.domain.service.datasource.FavoriteLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingFavoriteUseCase
import di.Inject
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Singleton

@Singleton
internal class PagingFavoriteInteractor @Inject constructor(
    private val favoriteLocalDataSource: FavoriteLocalDataSource,
) : PagingFavoriteUseCase() {

    override fun run(request: Request): Flow<PagingData<Favorite>> {
        return favoriteLocalDataSource
            .pagingDataFlow(
                request.pagingConfig,
                request.bookshelfId,
                request.path,
                request.isRecent
            )
    }
}
