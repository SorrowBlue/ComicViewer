package com.sorrowblue.comicviewer.data.database.impl

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.withTransaction
import com.sorrowblue.comicviewer.data.database.ComicViewerDatabase
import com.sorrowblue.comicviewer.data.database.FileModelRemoteMediator
import com.sorrowblue.comicviewer.data.database.dao.FileDao
import com.sorrowblue.comicviewer.data.database.entity.FileEntity
import com.sorrowblue.comicviewer.data.database.entity.FileWithCountEntity
import com.sorrowblue.comicviewer.data.database.entity.SimpleFileEntity
import com.sorrowblue.comicviewer.data.database.entity.UpdateFileHistoryEntity
import com.sorrowblue.comicviewer.data.database.entity.UpdateFileInfoEntity
import com.sorrowblue.comicviewer.domain.model.SearchCondition
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderThumbnailOrder
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import com.sorrowblue.comicviewer.domain.service.datasource.FileLocalDataSource
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class FileModelLocalDataSourceImpl @Inject constructor(
    private val dao: FileDao,
    private val database: ComicViewerDatabase,
    private val factory: FileModelRemoteMediator.Factory,
) : FileLocalDataSource {

    override fun pagingSource(
        pagingConfig: PagingConfig,
        bookshelfId: BookshelfId,
        searchCondition: () -> SearchCondition,
    ): Flow<PagingData<File>> = Pager(pagingConfig) {
        dao.pagingSource(bookshelfId.value, searchCondition())
    }.flow.map { it.map(FileWithCountEntity::toModel) }

    override suspend fun addUpdate(fileModel: File) {
        dao.upsert(FileEntity.fromModel(fileModel))
    }

    override suspend fun updateHistory(
        path: String,
        bookshelfId: BookshelfId,
        lastReadPage: Int,
        lastReading: Long,
    ) {
        dao.updateHistory(
            UpdateFileHistoryEntity(
                path,
                bookshelfId,
                lastReadPage,
                lastReading
            )
        )
    }

    override suspend fun updateAdditionalInfo(
        path: String,
        bookshelfId: BookshelfId,
        cacheKey: String,
        totalPage: Int,
    ) {
        dao.updateInfo(UpdateFileInfoEntity(path, bookshelfId, cacheKey, totalPage))
    }

    override suspend fun updateSimpleAll(list: List<File>) {
        dao.updateAllSimple(list.map(SimpleFileEntity::fromModel))
    }

    override suspend fun selectByNotPaths(
        bookshelfId: BookshelfId,
        path: String,
        list: List<String>,
    ): List<File> {
        return dao.findByNotPaths(bookshelfId.value, path, list).map(FileEntity::toModel)
    }

    override suspend fun deleteAll(list: List<File>) {
        dao.deleteAll(list.map(FileEntity.Companion::fromModel))
    }

    override suspend fun exists(bookshelfId: BookshelfId, path: String): Boolean {
        return dao.find(bookshelfId.value, path) != null
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun pagingSource(
        pagingConfig: PagingConfig,
        bookshelf: Bookshelf,
        file: File,
        searchCondition: () -> SearchCondition,
    ): Flow<PagingData<File>> {
        val remoteMediator = factory.create(bookshelf, file)
        return Pager(pagingConfig, remoteMediator = remoteMediator) {
            dao.pagingSource(bookshelf.id.value, searchCondition())
        }.flow.map { it.map(FileWithCountEntity::toModel) }
    }

    override suspend fun root(id: BookshelfId): Folder? {
        return dao.findRootFile(id.value)?.toModel() as? Folder
    }

    override suspend fun findBy(bookshelfId: BookshelfId, path: String): File? {
        return dao.find(bookshelfId.value, path)?.toModel()
    }

    override fun flow(bookshelfId: BookshelfId, path: String): Flow<File?> {
        return dao.flow(bookshelfId.value, path).map { it?.toModel() }
    }

    override fun nextFileModel(
        bookshelfId: BookshelfId,
        path: String,
        sortType: SortType,
    ): Flow<File?> {
        return dao.flowPrevNextFile(bookshelfId.value, path, true, sortType)
            .map { it.firstOrNull()?.toModel() }
    }

    override fun prevFileModel(
        bookshelfId: BookshelfId,
        path: String,
        sortType: SortType,
    ): Flow<File?> {
        return dao.flowPrevNextFile(bookshelfId.value, path, false, sortType)
            .map { it.firstOrNull()?.toModel() }
    }

    override suspend fun getCacheKeys(
        bookshelfId: BookshelfId,
        parent: String,
        limit: Int,
    ): List<String> {
        return dao.findCacheKeyOrderSortIndex(bookshelfId.value, "$parent%", limit)
    }

    override suspend fun getCacheKeys(
        bookshelfId: BookshelfId,
        parent: String,
        limit: Int,
        folderThumbnailOrderModel: FolderThumbnailOrder,
    ): List<String> {
        return when (folderThumbnailOrderModel) {
            FolderThumbnailOrder.NAME -> dao.findCacheKeyOrderSortIndex(
                bookshelfId.value,
                "$parent%",
                limit
            )

            FolderThumbnailOrder.MODIFIED -> dao.findCacheKeyOrderLastModified(
                bookshelfId.value,
                "$parent%",
                limit
            )

            FolderThumbnailOrder.LAST_READ -> dao.findCacheKeysOrderLastRead(
                bookshelfId.value,
                "$parent%",
                limit
            )
        }
    }

    override suspend fun removeCacheKey(diskCacheKey: String) {
        return dao.deleteCacheKeyBy(diskCacheKey)
    }

    override fun pagingHistoryBookSource(pagingConfig: PagingConfig): Flow<PagingData<Book>> {
        return Pager(pagingConfig) {
            dao.pagingSourceHistory()
        }.flow.map { pagingData -> pagingData.map { it.toModel() as Book } }
    }

    override fun lastHistory(): Flow<File> {
        return dao.lastHistory().map(FileEntity::toModel)
    }

    override suspend fun deleteThumbnails() {
        dao.deleteAllCacheKey()
    }

    override suspend fun deleteHistory(bookshelfId: BookshelfId, list: List<String>) {
        dao.deleteHistory(bookshelfId.value, list.toTypedArray())
    }

    override suspend fun updateHistory(file: File, files: List<File>) {
        database.withTransaction {
            // リモートになくてDBにある項目：削除対象
            val deleteFileData = selectByNotPaths(
                file.bookshelfId,
                file.path,
                files.map(File::path)
            )
            // DBから削除
            deleteAll(deleteFileData)

            // existsFiles DBにある項目：更新対象
            // noExistsFiles DBにない項目：挿入対象
            val (existsFiles, noExistsFiles) = files.partition {
                exists(it.bookshelfId, it.path)
            }

            // DBにない項目を挿入
            dao.upsertAll(noExistsFiles.map { FileEntity.fromModel(it) })

            // DBにファイルを更新
            // ファイルサイズ、更新日時、タイプ ソート、インデックス
            updateSimpleAll(existsFiles)
        }
    }

    override suspend fun deleteAll2(bookshelfModelId: BookshelfId) {
        dao.deleteAll(bookshelfModelId.value)
    }

    override suspend fun getCacheKeyList(bookshelfId: BookshelfId): List<String> {
        return dao.cacheKeyList(bookshelfId.value)
    }
}
