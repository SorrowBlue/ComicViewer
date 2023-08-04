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
import com.sorrowblue.comicviewer.data.common.bookshelf.SearchConditionEntity
import com.sorrowblue.comicviewer.data.common.bookshelf.SearchConditionEntity2
import com.sorrowblue.comicviewer.data.common.bookshelf.SortEntity
import com.sorrowblue.comicviewer.data.database.ComicViewerDatabase
import com.sorrowblue.comicviewer.data.database.FileModelRemoteMediator
import com.sorrowblue.comicviewer.data.database.dao.FileDao
import com.sorrowblue.comicviewer.data.database.entity.File
import com.sorrowblue.comicviewer.data.database.entity.FileWithCount
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

    override fun pagingSource(
        pagingConfig: PagingConfig,
        bookshelfModelId: BookshelfModelId,
        searchConditionEntity: () -> SearchConditionEntity2
    ) = Pager(pagingConfig) {
        dao.pagingSource(bookshelfModelId.value, searchConditionEntity())
    }.flow.map { it.map(FileWithCount::toModel) }

    override suspend fun addUpdate(fileModel: FileModel) {
        dao.upsert(File.fromModel(fileModel))
    }

    override suspend fun updateHistory(
        path: String,
        bookshelfModelId: BookshelfModelId,
        lastReadPage: Int,
        lastReading: Long
    ) {
        dao.updateHistory(UpdateFileHistory(path, bookshelfModelId.value, lastReadPage, lastReading))
    }

    override suspend fun updateAdditionalInfo(
        path: String,
        bookshelfModelId: BookshelfModelId,
        cacheKey: String,
        totalPage: Int
    ) {
        dao.updateInfo(UpdateFileInfo(path, bookshelfModelId.value, cacheKey, totalPage))
    }

    override suspend fun updateSimpleAll(list: List<SimpleFileModel>) {
        dao.updateAllSimple(list.map(SimpleFile::fromModel))
    }

    override suspend fun selectByNotPaths(
        bookshelfModelId: BookshelfModelId,
        path: String,
        list: List<String>
    ): List<FileModel> {
        return dao.findByNotPaths(bookshelfModelId.value, path, list).map(File::toModel)
    }

    override suspend fun deleteAll(list: List<FileModel>) {
        dao.deleteAll(list.map(File.Companion::fromModel))
    }

    override suspend fun exists(bookshelfModelId: BookshelfModelId, path: String): Boolean {
        return dao.find(bookshelfModelId.value, path) != null
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun pagingSource(
        pagingConfig: PagingConfig,
        bookshelfModel: BookshelfModel,
        fileModel: FileModel,
        sortType: () -> SortEntity
    ): Flow<PagingData<FileModel>> {
        val remoteMediator = factory.create(bookshelfModel, fileModel)
        val searchConditionEntity = SearchConditionEntity(
            null,
            SearchConditionEntity.Range.IN_FOLDER(fileModel.path),
            SearchConditionEntity.Period.NONE
        )
        return Pager(pagingConfig, remoteMediator = remoteMediator) {
            dao.pagingSource(bookshelfModel.id.value, searchConditionEntity, sortType())
        }.flow.map { it.map(FileWithCount::toModel) }

    }

    override fun pagingSource(
        pagingConfig: PagingConfig,
        bookshelfModelId: BookshelfModelId,
        searchConditionEntity: SearchConditionEntity,
        sortEntity: () -> SortEntity
    ): Flow<PagingData<FileModel>> {
        return Pager(pagingConfig) {
            dao.pagingSource(bookshelfModelId.value, searchConditionEntity, sortEntity())
        }.flow.map { it.map(FileWithCount::toModel) }
    }

    override suspend fun root(id: BookshelfModelId): FileModel.Folder? {
        return dao.findRootFile(id.value)?.toModel() as? FileModel.Folder
    }

    override suspend fun findBy(bookshelfModelId: BookshelfModelId, path: String): FileModel? {
        return dao.find(bookshelfModelId.value, path)?.toModel()
    }

    override fun flow(bookshelfModelId: BookshelfModelId, path: String): Flow<FileModel?> {
        return dao.flow(bookshelfModelId.value, path).map { it?.toModel() }
    }

    override fun nextFileModel(
        bookshelfModelId: BookshelfModelId,
        path: String,
        sortEntity: SortEntity
    ): Flow<FileModel?> {
        return dao.flowPrevNextFile(bookshelfModelId.value, path, true, sortEntity)
            .map { it?.toModel() }
    }

    override fun prevFileModel(
        bookshelfModelId: BookshelfModelId,
        path: String,
        sortEntity: SortEntity
    ): Flow<FileModel?> {
        return dao.flowPrevNextFile(bookshelfModelId.value, path, false, sortEntity)
            .map { it?.toModel() }
    }

    override suspend fun getCacheKeys(
        bookshelfModelId: BookshelfModelId,
        parent: String,
        limit: Int
    ): List<String> {
        return dao.findCacheKeyOrderSortIndex(bookshelfModelId.value, "$parent%", limit)
    }

    override suspend fun getCacheKeys(
        bookshelfModelId: BookshelfModelId,
        parent: String,
        limit: Int,
        folderThumbnailOrderModel: FolderThumbnailOrderModel
    ): List<String> {
        return when (folderThumbnailOrderModel) {
            FolderThumbnailOrderModel.NAME -> dao.findCacheKeyOrderSortIndex(
                bookshelfModelId.value,
                "$parent%",
                limit
            )

            FolderThumbnailOrderModel.MODIFIED -> dao.findCacheKeyOrderLastModified(
                bookshelfModelId.value,
                "$parent%",
                limit
            )

            FolderThumbnailOrderModel.LAST_READ -> dao.findCacheKeysOrderLastRead(
                bookshelfModelId.value,
                "$parent%",
                limit
            )
        }
    }

    override suspend fun removeCacheKey(diskCacheKey: String) {
        return dao.deleteCacheKeyBy(diskCacheKey)
    }

    override fun pagingHistoryBookSource(pagingConfig: PagingConfig): Flow<PagingData<FileModel>> {
        return Pager(pagingConfig) {
            dao.pagingSourceHistory()
        }.flow.map { pagingData -> pagingData.map { it.toModel() } }
    }

    override suspend fun deleteThumbnails() {
        dao.deleteAllCacheKey()
    }

    override suspend fun deleteHistory(bookshelfModelId: BookshelfModelId, list: List<String>) {
        dao.deleteHistory(bookshelfModelId.value, list.toTypedArray())
    }

    override suspend fun updateHistory(fileModel: FileModel, files: List<FileModel>) {
        database.withTransaction {
            // リモートになくてDBにある項目：削除対象
            val deleteFileData = selectByNotPaths(
                fileModel.bookshelfModelId,
                fileModel.path,
                files.map(FileModel::path)
            )
            // DBから削除
            deleteAll(deleteFileData)

            // existsFiles DBにある項目：更新対象
            // noExistsFiles DBにない項目：挿入対象
            val (existsFiles, noExistsFiles) = files.partition {
                exists(it.bookshelfModelId, it.path)
            }

            // DBにない項目を挿入
            dao.upsertAll(noExistsFiles.map { File.fromModel(it) })

            // DBにファイルを更新
            // ファイルサイズ、更新日時、タイプ ソート、インデックス
            updateSimpleAll(existsFiles.map(FileModel::toSimpleModel))
        }
    }
}
