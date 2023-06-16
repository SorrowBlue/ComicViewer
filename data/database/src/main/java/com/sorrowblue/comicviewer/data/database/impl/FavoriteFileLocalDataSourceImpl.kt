package com.sorrowblue.comicviewer.data.database.impl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.sorrowblue.comicviewer.data.common.FileModel
import com.sorrowblue.comicviewer.data.common.bookshelf.SortEntity
import com.sorrowblue.comicviewer.data.common.favorite.FavoriteFileModel
import com.sorrowblue.comicviewer.data.common.favorite.FavoriteModelId
import com.sorrowblue.comicviewer.data.database.dao.FavoriteFileDao
import com.sorrowblue.comicviewer.data.database.entity.FavoriteFile
import com.sorrowblue.comicviewer.data.database.entity.File
import com.sorrowblue.comicviewer.data.datasource.FavoriteFileLocalDataSource
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class FavoriteFileLocalDataSourceImpl @Inject constructor(
    private val favoriteFileDao: FavoriteFileDao,
) : FavoriteFileLocalDataSource {

    override fun pagingSource(
        pagingConfig: PagingConfig, favoriteModelId: FavoriteModelId, sortType: () -> SortEntity
    ): Flow<PagingData<FileModel>> {
        return Pager(pagingConfig) {
            favoriteFileDao.pagingSource(favoriteModelId.value, sortType.invoke())
        }.flow.map { it.map(File::toModel) }
    }

    override suspend fun getCacheKeyList(
        favoriteModelId: FavoriteModelId, limit: Int
    ): List<String> {
        return favoriteFileDao.findCacheKey(favoriteModelId.value, limit)
    }

    override suspend fun add(favoriteFileModel: FavoriteFileModel) {
        favoriteFileDao.insert(FavoriteFile.fromModel(favoriteFileModel))
    }

    override suspend fun delete(favoriteFileModel: FavoriteFileModel) {
        favoriteFileDao.delete(FavoriteFile.fromModel(favoriteFileModel))
    }

    override fun flowNextFavoriteFile(favoriteFileModel: FavoriteFileModel, sortEntity: SortEntity): Flow<FileModel?> {
        val favoriteFile = FavoriteFile.fromModel(favoriteFileModel)
        return favoriteFileDao.flowPrevNext(
            favoriteFile.favoriteId,
            favoriteFile.serverId,
            favoriteFile.filePath,
            true,
            sortEntity
        ).map { it?.toModel() }
    }

    override fun flowPrevFavoriteFile(favoriteFileModel: FavoriteFileModel, sortEntity: SortEntity): Flow<FileModel?> {
        val favoriteFile = FavoriteFile.fromModel(favoriteFileModel)
        return favoriteFileDao.flowPrevNext(
            favoriteFile.favoriteId,
            favoriteFile.serverId,
            favoriteFile.filePath,
            false,
            sortEntity
        ).map { it?.toModel() }
    }
}
