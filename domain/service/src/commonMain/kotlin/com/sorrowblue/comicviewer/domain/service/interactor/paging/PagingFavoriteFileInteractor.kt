package com.sorrowblue.comicviewer.domain.service.interactor.paging

import androidx.paging.PagingData
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.service.datasource.DatastoreDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.FavoriteFileLocalDataSource
import com.sorrowblue.comicviewer.domain.usecase.paging.PagingFavoriteFileUseCase
import di.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.koin.core.annotation.Singleton

@Singleton
internal class PagingFavoriteFileInteractor @Inject constructor(
    private val favoriteFileLocalDataSource: FavoriteFileLocalDataSource,
    private val datastoreDataSource: DatastoreDataSource,
) : PagingFavoriteFileUseCase() {

    override fun run(request: Request): Flow<PagingData<File>> {
        return favoriteFileLocalDataSource.pagingSource(request.pagingConfig, request.favoriteId) {
            runBlocking { datastoreDataSource.folderDisplaySettings.first() }.sortType
        }
    }
}
