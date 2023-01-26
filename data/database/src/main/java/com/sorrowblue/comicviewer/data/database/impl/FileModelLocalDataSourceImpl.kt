package com.sorrowblue.comicviewer.data.database.impl

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.withTransaction
import com.sorrowblue.comicviewer.data.common.FileModel
import com.sorrowblue.comicviewer.data.common.FolderThumbnailOrderModel
import com.sorrowblue.comicviewer.data.common.ServerModel
import com.sorrowblue.comicviewer.data.common.ServerModelId
import com.sorrowblue.comicviewer.data.common.SimpleFileModel
import com.sorrowblue.comicviewer.data.common.SortType
import com.sorrowblue.comicviewer.data.database.ComicViewerDatabase
import com.sorrowblue.comicviewer.data.database.FileModelRemoteMediator
import com.sorrowblue.comicviewer.data.database.dao.FileDao
import com.sorrowblue.comicviewer.data.database.dao.pagingSource
import com.sorrowblue.comicviewer.data.database.dao.pagingSourceQuery
import com.sorrowblue.comicviewer.data.database.entity.File
import com.sorrowblue.comicviewer.data.database.entity.UpdateFileHistory
import com.sorrowblue.comicviewer.data.database.entity.UpdateFileInfo
import com.sorrowblue.comicviewer.data.database.entity.toFile
import com.sorrowblue.comicviewer.data.database.entity.toSimpleFile
import com.sorrowblue.comicviewer.data.datasource.FileModelLocalDataSource
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import logcat.logcat

internal class FileModelLocalDataSourceImpl @Inject constructor(
    private val dao: FileDao,
    private val database: ComicViewerDatabase,
    private val factory: FileModelRemoteMediator.Factory
) : FileModelLocalDataSource {

    override suspend fun register(fileModel: FileModel) {
        dao.upsert(fileModel.toFile())
    }

    override suspend fun update(
        path: String,
        serverModelId: ServerModelId,
        lastReadPage: Int,
        lastRead: Long
    ) {
        dao.update(UpdateFileHistory(path, serverModelId.value, lastReadPage, lastRead))
    }

    override suspend fun update(
        path: String,
        serverModelId: ServerModelId,
        cacheKey: String,
        totalPage: Int
    ) {
        dao.update(UpdateFileInfo(path, serverModelId.value, cacheKey, totalPage))
    }

    override suspend fun updateAll(list: List<SimpleFileModel>) {
        dao.updateAllSimple(list.map { it.toSimpleFile() })
    }

    override suspend fun <R> withTransaction(block: suspend () -> R): R {
        return database.withTransaction(block)
    }

    override suspend fun selectByNotPaths(
        serverModelId: ServerModelId,
        path: String,
        list: List<String>
    ): List<FileModel> {
        return dao.selectByNotPaths(serverModelId.value, path, list).map(File::toFileModel)
    }

    override suspend fun deleteAll(list: List<FileModel>) {
        dao.deleteAll(list.map { it.toFile() })
    }

    override suspend fun exists(serverModelId: ServerModelId, path: String): Boolean {
        return dao.selectBy(serverModelId.value, path) != null
    }

    override suspend fun registerAll(list: List<FileModel>) {
        withTransaction {
            val (exists, noexists) = list.partition {
                dao.selectBy(it.serverModelId.value, it.path) != null
            }
            dao.insertAll(noexists.map { it.toFile() })
            dao.updateAll(exists.map { it.toFile() })
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun pagingSource(
        pagingConfig: PagingConfig,
        serverModel: ServerModel,
        fileModel: FileModel,
        sortType: () -> SortType
    ): Flow<PagingData<FileModel>> {
        val remoteMediator = factory.create(serverModel, fileModel)
        return Pager(pagingConfig, remoteMediator = remoteMediator) {
            dao.pagingSource(serverModel.id.value, fileModel.path, sortType())
        }.flow.map { pagingData -> pagingData.map { it.toFileModel() } }

    }

    override fun pagingSource(
        pagingConfig: PagingConfig,
        serverModelId: ServerModelId,
        query: () -> String,
        sortType: () -> SortType,
    ): Flow<PagingData<FileModel>> {
        return Pager(pagingConfig) {
            dao.pagingSourceQuery(
                serverModelId.value,
                query(),
                sortType()
            )
        }.flow.map { pagingData -> pagingData.map { it.toFileModel() } }
    }

    override suspend fun root(id: ServerModelId): FileModel.Folder? {
        return dao.selectRootBy(id.value)?.toFileModel() as? FileModel.Folder
    }

    override suspend fun findBy(serverModelId: ServerModelId): List<FileModel> {
        return dao.selectBy(serverModelId.value).map(File::toFileModel)
    }

    override suspend fun findBy(serverModelId: ServerModelId, path: String): FileModel? {
        return dao.selectBy(serverModelId.value, path)?.toFileModel()
    }

    override suspend fun nextFileModel(serverModelId: ServerModelId, path: String): FileModel? {
        return dao.selectNextFile(serverModelId.value, path)?.toFileModel()
    }

    override suspend fun prevFileModel(serverModelId: ServerModelId, path: String): FileModel? {
        return dao.selectPrevFile(serverModelId.value, path)?.toFileModel()
    }

    override suspend fun getCacheKeys(
        serverModelId: ServerModelId,
        parent: String,
        limit: Int
    ): List<String> {
        return dao.selectCacheKeysSortIndex(serverModelId.value, "$parent%", limit)
    }

    override suspend fun getCacheKeys(
        serverModelId: ServerModelId,
        parent: String,
        limit: Int,
        folderThumbnailOrderModel: FolderThumbnailOrderModel
    ): List<String> {
        return when (folderThumbnailOrderModel) {
            FolderThumbnailOrderModel.NAME -> dao.selectCacheKeysSortIndex(
                serverModelId.value,
                "$parent%",
                limit
            )
            FolderThumbnailOrderModel.MODIFIED -> dao.selectCacheKeysSortLastModified(
                serverModelId.value,
                "$parent%",
                limit
            )
            FolderThumbnailOrderModel.LAST_READ -> dao.selectCacheKeysSortLastRead(
                serverModelId.value,
                "$parent%",
                limit
            )
        }
    }

    override suspend fun removeCacheKey(diskCacheKey: String) {
        return dao.removeCacheKey(diskCacheKey)
    }
}
