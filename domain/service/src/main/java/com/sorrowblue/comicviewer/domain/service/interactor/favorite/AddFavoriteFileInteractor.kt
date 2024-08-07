package com.sorrowblue.comicviewer.domain.service.interactor.favorite

import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.service.datasource.FavoriteFileLocalDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.FavoriteLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.favorite.AddFavoriteFileUseCase
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

internal class AddFavoriteFileInteractor @Inject constructor(
    private val favoriteLocalDataSource: FavoriteLocalDataSource,
    private val favoriteFileLocalDataSource: FavoriteFileLocalDataSource,
) : AddFavoriteFileUseCase() {

    override fun run(request: Request): Flow<Resource<Unit, Error>> {
        return flow {
            favoriteFileLocalDataSource.add(request.favoriteFile)
            favoriteLocalDataSource.flow(request.favoriteFile.id).first().let {
                favoriteLocalDataSource.update(
                    it.copy(
                        addedDateTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
                    )
                )
            }
            emit(Resource.Success(Unit))
        }
    }
}
