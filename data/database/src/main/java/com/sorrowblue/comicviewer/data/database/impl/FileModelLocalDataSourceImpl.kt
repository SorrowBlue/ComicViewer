package com.sorrowblue.comicviewer.data.database.impl

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.withTransaction
import com.sorrowblue.comicviewer.data.common.FileModel
import com.sorrowblue.comicviewer.data.common.SimpleFileModel
import com.sorrowblue.comicviewer.data.common.bookshelf.BookshelfModel
import com.sorrowblue.comicviewer.data.common.bookshelf.BookshelfModelId
import com.sorrowblue.comicviewer.data.common.bookshelf.FolderThumbnailOrderModel
import com.sorrowblue.comicviewer.data.common.bookshelf.SortType
import com.sorrowblue.comicviewer.data.database.ComicViewerDatabase
import com.sorrowblue.comicviewer.data.database.FileModelRemoteMediator
import com.sorrowblue.comicviewer.data.database.dao.FileDao
import com.sorrowblue.comicviewer.data.database.entity.File
import com.sorrowblue.comicviewer.data.database.entity.SimpleFile
import com.sorrowblue.comicviewer.data.database.entity.UpdateFileHistory
import com.sorrowblue.comicviewer.data.database.entity.UpdateFileInfo
import com.sorrowblue.comicviewer.data.datasource.FileModelLocalDataSource
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class FileModelLocalDataSourceImpl @Inject constructor(
    private val dao: FileDao,
    private val database: ComicViewerDatabase,
    private val factory: FileModelRemoteMediator.Factory
) : FileModelLocalDataSource {

    override suspend fun register(fileModel: FileModel) {
        dao.upsert(File.fromModel(fileModel))
    }

    override suspend fun update(
        path: String,
        bookshelfModelId: BookshelfModelId,
        lastReadPage: Int,
        lastRead: Long
    ) {
        dao.update(UpdateFileHistory(path, bookshelfModelId.value, lastReadPage, lastRead))
    }

    override suspend fun update(
        path: String,
        bookshelfModelId: BookshelfModelId,
        cacheKey: String,
        totalPage: Int
    ) {
        dao.update(UpdateFileInfo(path, bookshelfModelId.value, cacheKey, totalPage))
    }

    override suspend fun updateAll(list: List<SimpleFileModel>) {
        dao.updateAllSimple(list.map(SimpleFile::fromModel))
    }

    override suspend fun <R> withTransaction(block: suspend () -> R): R {
        return database.withTransaction(block)
    }

    override suspend fun selectByNotPaths(
        bookshelfModelId: BookshelfModelId,
        path: String,
        list: List<String>
    ): List<FileModel> {
        return dao.selectByNotPaths(bookshelfModelId.value, path, list).map(File::toModel)
    }

    override suspend fun deleteAll(list: List<FileModel>) {
        dao.deleteAll(list.map(File.Companion::fromModel))
    }

    override suspend fun exists(bookshelfModelId: BookshelfModelId, path: String): Boolean {
        return dao.selectBy(bookshelfModelId.value, path) != null
    }

    override suspend fun registerAll(list: List<FileModel>) {
        withTransaction {
            val (exists, noexists) = list.partition {
                dao.selectBy(it.bookshelfModelId.value, it.path) != null
            }
            dao.insertAll(noexists.map { File.fromModel(it) })
            dao.updateAll(exists.map { File.fromModel(it) })
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun pagingSource(
        pagingConfig: PagingConfig,
        bookshelfModel: BookshelfModel,
        fileModel: FileModel,
        sortType: () -> SortType
    ): Flow<PagingData<FileModel>> {
        val remoteMediator = factory.create(bookshelfModel, fileModel)
        return Pager(pagingConfig, remoteMediator = remoteMediator) {
            dao.pagingSource(bookshelfModel.id.value, fileModel.path, sortType())
        }.flow.map { pagingData -> pagingData.map { it.toModel() } }

    }

    override fun pagingSource(
        pagingConfig: PagingConfig,
        bookshelfModelId: BookshelfModelId,
        query: () -> String,
        sortType: () -> SortType,
    ): Flow<PagingData<FileModel>> {
        return Pager(pagingConfig) {
            dao.pagingSourceQuery(
                bookshelfModelId.value,
                query(),
                sortType()
            )
        }.flow.map { pagingData -> pagingData.map { it.toModel() } }
    }

    override suspend fun root(id: BookshelfModelId): FileModel.Folder? {
        return dao.selectRootBy(id.value)?.toModel() as? FileModel.Folder
    }

    override suspend fun findBy(bookshelfModelId: BookshelfModelId): List<FileModel> {
        return dao.selectBy(bookshelfModelId.value).map(File::toModel)
    }

    override suspend fun findBy(bookshelfModelId: BookshelfModelId, path: String): FileModel? {
        return dao.selectBy(bookshelfModelId.value, path)?.toModel()
    }

    override fun selectBy(bookshelfModelId: BookshelfModelId, path: String): Flow<FileModel?> {
        return dao.selectBy2(bookshelfModelId.value, path).map { it?.toModel() }
    }

    override fun nextFileModel(bookshelfModelId: BookshelfModelId, path: String): Flow<FileModel?> {
        return dao.selectNextFile(bookshelfModelId.value, path).map { it?.toModel() }
    }

    override fun prevFileModel(bookshelfModelId: BookshelfModelId, path: String): Flow<FileModel?> {
        return dao.selectPrevFile(bookshelfModelId.value, path).map { it?.toModel() }
    }

    override suspend fun getCacheKeys(
        bookshelfModelId: BookshelfModelId,
        parent: String,
        limit: Int
    ): List<String> {
        return dao.selectCacheKeysSortIndex(bookshelfModelId.value, "$parent%", limit)
    }

    override suspend fun getCacheKeys(
        bookshelfModelId: BookshelfModelId,
        parent: String,
        limit: Int,
        folderThumbnailOrderModel: FolderThumbnailOrderModel
    ): List<String> {
        return when (folderThumbnailOrderModel) {
            FolderThumbnailOrderModel.NAME -> dao.selectCacheKeysSortIndex(
                bookshelfModelId.value,
                "$parent%",
                limit
            )

            FolderThumbnailOrderModel.MODIFIED -> dao.selectCacheKeysSortLastModified(
                bookshelfModelId.value,
                "$parent%",
                limit
            )

            FolderThumbnailOrderModel.LAST_READ -> dao.selectCacheKeysSortLastRead(
                bookshelfModelId.value,
                "$parent%",
                limit
            )
        }
    }

    override suspend fun removeCacheKey(diskCacheKey: String) {
        return dao.removeCacheKey(diskCacheKey)
    }
}
