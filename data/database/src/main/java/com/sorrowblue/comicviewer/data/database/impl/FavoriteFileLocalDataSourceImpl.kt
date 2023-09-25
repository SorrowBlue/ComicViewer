package com.sorrowblue.comicviewer.data.database.impl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.sorrowblue.comicviewer.data.database.dao.FavoriteFileDao
import com.sorrowblue.comicviewer.data.database.entity.FavoriteFileEntity
import com.sorrowblue.comicviewer.data.database.entity.FileEntity
import com.sorrowblue.comicviewer.data.infrastructure.datasource.FavoriteFileLocalDataSource
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteFile
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.settings.SortType
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class FavoriteFileLocalDataSourceImpl @Inject constructor(
    private val favoriteFileDao: FavoriteFileDao,
) : FavoriteFileLocalDataSource {

    override fun pagingSource(
        pagingConfig: PagingConfig, favoriteModelId: FavoriteId, sortType: () -> SortType,
    ): Flow<PagingData<File>> {
        return Pager(pagingConfig) {
            favoriteFileDao.pagingSource(favoriteModelId.value, sortType.invoke())
        }.flow.map { it.map(FileEntity::toModel) }
    }

    override suspend fun getCacheKeyList(
        favoriteModelId: FavoriteId, limit: Int,
    ): List<String> {
        return favoriteFileDao.findCacheKey(favoriteModelId.value, limit)
    }

    override suspend fun add(favoriteFileModel: FavoriteFile) {
        favoriteFileDao.insert(FavoriteFileEntity.fromModel(favoriteFileModel))
    }

    override suspend fun delete(favoriteFileModel: FavoriteFile) {
        favoriteFileDao.delete(FavoriteFileEntity.fromModel(favoriteFileModel))
    }

    override fun flowNextFavoriteFile(
        favoriteFileModel: FavoriteFile,
        sortEntity: SortType,
    ): Flow<File?> {
        val favoriteFileEntity = FavoriteFileEntity.fromModel(favoriteFileModel)
        return favoriteFileDao.flowPrevNext(
            favoriteFileEntity.favoriteId,
            favoriteFileEntity.serverId,
            favoriteFileEntity.filePath,
            true,
            sortEntity
        ).map { it?.toModel() }
    }

    override fun flowPrevFavoriteFile(
        favoriteFileModel: FavoriteFile,
        sortEntity: SortType,
    ): Flow<File?> {
        val favoriteFileEntity = FavoriteFileEntity.fromModel(favoriteFileModel)
        return favoriteFileDao.flowPrevNext(
            favoriteFileEntity.favoriteId,
            favoriteFileEntity.serverId,
            favoriteFileEntity.filePath,
            false,
            sortEntity
        ).map { it?.toModel() }
    }
}
