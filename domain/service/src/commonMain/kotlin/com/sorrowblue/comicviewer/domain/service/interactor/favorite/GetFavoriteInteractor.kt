package com.sorrowblue.comicviewer.domain.service.interactor.favorite

import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.service.datasource.FavoriteLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.favorite.GetFavoriteUseCase
import di.Inject
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Singleton

@Singleton
internal class GetFavoriteInteractor @Inject constructor(
    private val favoriteLocalDataSource: FavoriteLocalDataSource,
) : GetFavoriteUseCase() {

    override fun run(request: Request) = kotlin.runCatching {
        favoriteLocalDataSource.flow(request.favoriteId)
    }.fold({ flow ->
        flow.map { Resource.Success(it) }
    }, {
        flow { Resource.Error(Error.System) }
    })
}
