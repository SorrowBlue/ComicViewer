package com.sorrowblue.comicviewer.data.reporitory

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.sorrowblue.comicviewer.data.common.FileModel
import com.sorrowblue.comicviewer.data.common.bookshelf.SortEntity
import com.sorrowblue.comicviewer.data.common.favorite.FavoriteModel
import com.sorrowblue.comicviewer.data.common.favorite.FavoriteModelId
import com.sorrowblue.comicviewer.data.datasource.FavoriteFileLocalDataSource
import com.sorrowblue.comicviewer.data.datasource.FavoriteLocalDataSource
import com.sorrowblue.comicviewer.data.mapper.toFavorite
import com.sorrowblue.comicviewer.data.mapper.toFavoriteBookModel
import com.sorrowblue.comicviewer.data.mapper.toFile
import com.sorrowblue.comicviewer.domain.model.favorite.Favorite
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteFile
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.settings.SortType
import com.sorrowblue.comicviewer.domain.service.repository.FavoriteFileRepository
import com.sorrowblue.comicviewer.domain.service.repository.FavoriteRepository
import com.sorrowblue.comicviewer.domain.service.repository.SettingsCommonRepository
import com.sorrowblue.comicviewer.framework.Result
import com.sorrowblue.comicviewer.framework.Unknown
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

internal class FavoriteFileRepositoryImpl @Inject constructor(
    private val favoriteFileLocalDataSource: FavoriteFileLocalDataSource,
    private val settingsCommonRepository: SettingsCommonRepository
) : FavoriteFileRepository {

    override fun getNextRelFile(
        favoriteFile: FavoriteFile,
        isNext: Boolean
    ): Flow<Result<File, Unit>> {
        val sortEntity = runBlocking { settingsCommonRepository.folderDisplaySettings.first() }.sortType.let(SortEntity::from         )
        return kotlin.runCatching {
            if (isNext) {
                favoriteFileLocalDataSource.flowNextFavoriteFile(favoriteFile.toFavoriteBookModel(), sortEntity)
            } else {
                favoriteFileLocalDataSource.flowPrevFavoriteFile(favoriteFile.toFavoriteBookModel(), sortEntity)
            }
        }.fold({ modelFlow ->
            modelFlow.map { if (it != null) Result.Success(it.toFile()) else Result.Error(Unit) }
        }, {
            flowOf(Result.Exception(Unknown(it)))
        })
    }

    override fun pagingDataFlow(
        pagingConfig: PagingConfig,
        favoriteId: FavoriteId
    ): Flow<PagingData<File>> {
        return favoriteFileLocalDataSource.pagingSource(
            pagingConfig, FavoriteModelId(favoriteId.value)
        ) {
            val settings = runBlocking { settingsCommonRepository.folderDisplaySettings.first() }
            when (settings.sortType) {
                is SortType.DATE -> SortEntity.DATE(settings.sortType.isAsc)
                is SortType.NAME -> SortEntity.NAME(settings.sortType.isAsc)
                is SortType.SIZE -> SortEntity.SIZE(settings.sortType.isAsc)
            }
        }.map { it.map(FileModel::toFile) }
    }

    override suspend fun add(favoriteFile: FavoriteFile) {
        return withContext(Dispatchers.IO) {
            favoriteFileLocalDataSource.add(favoriteFile.toFavoriteBookModel())
        }
    }

    override suspend fun delete(favoriteFile: FavoriteFile) {
        return withContext(Dispatchers.IO) {
            favoriteFileLocalDataSource.delete(favoriteFile.toFavoriteBookModel())
        }
    }

}

internal class FavoriteRepositoryImpl @Inject constructor(
    private val favoriteLocalDataSource: FavoriteLocalDataSource
) : FavoriteRepository {

    override fun get(favoriteId: FavoriteId): Flow<Favorite> {
        return favoriteLocalDataSource.flow(FavoriteModelId(favoriteId.value))
            .map(FavoriteModel::toFavorite).flowOn(Dispatchers.IO)
    }

    override suspend fun update(favorite: Favorite): Favorite {
        return withContext(Dispatchers.IO) {
            favoriteLocalDataSource.update(
                FavoriteModel(
                    FavoriteModelId(favorite.id.value), favorite.name, favorite.count
                )
            ).toFavorite()
        }
    }

    override suspend fun delete(favoriteId: FavoriteId) {
        return withContext(Dispatchers.IO) {
            favoriteLocalDataSource.delete(FavoriteModelId(favoriteId.value))
        }
    }

    override fun pagingDataFlow(pagingConfig: PagingConfig): Flow<PagingData<Favorite>> {
        return favoriteLocalDataSource.pagingDataFlow(pagingConfig).map { pagingData ->
            pagingData.map {
                Favorite(FavoriteId(it.id.value), it.name, it.count)
            }
        }
    }

    override suspend fun create(title: String) {
        withContext(Dispatchers.IO) {
            favoriteLocalDataSource.create(FavoriteModel(title))
        }
    }
}
