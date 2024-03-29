package com.sorrowblue.comicviewer.data.infrastructure.repository.impl

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.sorrowblue.comicviewer.data.infrastructure.datasource.FavoriteFileLocalDataSource
import com.sorrowblue.comicviewer.data.infrastructure.datasource.FavoriteLocalDataSource
import com.sorrowblue.comicviewer.data.infrastructure.di.IoDispatcher
import com.sorrowblue.comicviewer.domain.model.Result
import com.sorrowblue.comicviewer.domain.model.Unknown
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.favorite.Favorite
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteFile
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.service.repository.FavoriteFileRepository
import com.sorrowblue.comicviewer.domain.service.repository.FavoriteRepository
import com.sorrowblue.comicviewer.domain.service.repository.SettingsCommonRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

internal class FavoriteFileRepositoryImpl @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val favoriteFileLocalDataSource: FavoriteFileLocalDataSource,
    private val settingsCommonRepository: SettingsCommonRepository,
) : FavoriteFileRepository {

    override fun getNextRelFile(
        favoriteFile: FavoriteFile,
        isNext: Boolean,
    ): Flow<Result<File, Unit>> {
        val sortType =
            runBlocking { settingsCommonRepository.folderDisplaySettings.first() }.sortType
        return kotlin.runCatching {
            if (isNext) {
                favoriteFileLocalDataSource.flowNextFavoriteFile(favoriteFile, sortType)
            } else {
                favoriteFileLocalDataSource.flowPrevFavoriteFile(favoriteFile, sortType)
            }
        }.fold({ modelFlow ->
            modelFlow.map { if (it != null) Result.Success(it) else Result.Error(Unit) }
        }, {
            flowOf(Result.Exception(Unknown(it)))
        })
    }

    override fun pagingDataFlow(
        pagingConfig: PagingConfig,
        favoriteId: FavoriteId,
    ): Flow<PagingData<File>> {
        return favoriteFileLocalDataSource.pagingSource(pagingConfig, favoriteId) {
            runBlocking { settingsCommonRepository.folderDisplaySettings.first() }.sortType
        }
    }

    override suspend fun add(favoriteFile: FavoriteFile) {
        return withContext(dispatcher) {
            favoriteFileLocalDataSource.add(favoriteFile)
        }
    }

    override suspend fun delete(favoriteFile: FavoriteFile) {
        return withContext(dispatcher) {
            favoriteFileLocalDataSource.delete(favoriteFile)
        }
    }
}

internal class FavoriteRepositoryImpl @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val favoriteLocalDataSource: FavoriteLocalDataSource,
) : FavoriteRepository {

    override fun get(favoriteId: FavoriteId): Flow<Favorite> {
        return favoriteLocalDataSource.flow(favoriteId)
            .flowOn(dispatcher)
    }

    override suspend fun update(favorite: Favorite): Favorite {
        return withContext(dispatcher) {
            favoriteLocalDataSource.update(favorite)
        }
    }

    override suspend fun delete(favoriteId: FavoriteId) {
        return withContext(dispatcher) {
            favoriteLocalDataSource.delete(favoriteId)
        }
    }

    override fun pagingDataFlow(
        pagingConfig: PagingConfig,
        bookshelfId: BookshelfId,
        path: String,
    ): Flow<PagingData<Favorite>> {
        return favoriteLocalDataSource.pagingDataFlow(pagingConfig, bookshelfId, path)
            .map { pagingData ->
                pagingData.map {
                    Favorite(FavoriteId(it.id.value), it.name, it.count, it.exist)
                }
            }
    }

    override suspend fun create(title: String) {
        withContext(dispatcher) {
            favoriteLocalDataSource.create(Favorite(title))
        }
    }
}
