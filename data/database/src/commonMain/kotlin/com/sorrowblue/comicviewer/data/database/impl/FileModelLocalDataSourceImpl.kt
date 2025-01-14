package com.sorrowblue.comicviewer.data.database.impl

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.RoomRawQuery
import com.sorrowblue.comicviewer.data.database.FileModelRemoteMediator
import com.sorrowblue.comicviewer.data.database.dao.FileDao
import com.sorrowblue.comicviewer.data.database.dao.pagingSourceFileSearch
import com.sorrowblue.comicviewer.data.database.entity.file.FileEntity
import com.sorrowblue.comicviewer.data.database.entity.file.QueryFileWithCountEntity
import com.sorrowblue.comicviewer.data.database.entity.file.UpdateFileEntityMinimum
import com.sorrowblue.comicviewer.data.database.entity.file.UpdateFileEntityMinimumWithSortIndex
import com.sorrowblue.comicviewer.data.database.entity.file.UpdateFileHistoryEntity
import com.sorrowblue.comicviewer.data.database.entity.file.UpdateFileInfoEntity
import com.sorrowblue.comicviewer.domain.model.Resource
import com.sorrowblue.comicviewer.domain.model.SearchCondition
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderThumbnailOrder
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import com.sorrowblue.comicviewer.domain.service.datasource.FileLocalDataSource
import com.sorrowblue.comicviewer.domain.service.datasource.LocalDataSourceQueryError
import di.Inject
import di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import logcat.logcat
import org.koin.core.annotation.Singleton

@Singleton
internal class FileModelLocalDataSourceImpl @Inject constructor(
    private val dao: FileDao,
    private val factory: FileModelRemoteMediator.Factory,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
) : FileLocalDataSource {

    override suspend fun fileList(bookshelfId: BookshelfId, limit: Int, offset: Long): List<File> {
        return dao.fileList(bookshelfId.value, limit, offset).first().map { it.toModel() }
    }

    override suspend fun count(bookshelfId: BookshelfId): Long {
        return dao.count(bookshelfId.value).first()
    }

    override fun pagingSource(
        pagingConfig: PagingConfig,
        bookshelfId: BookshelfId,
        searchCondition: () -> SearchCondition,
    ): Flow<PagingData<File>> = Pager(pagingConfig) {
        dao.pagingSourceFileSearch(bookshelfId, searchCondition())
    }.flow.map { it.map(QueryFileWithCountEntity::toModel) }

    override suspend fun addUpdate(fileModel: File) {
        withContext(dispatcher) {
            dao.upsert(FileEntity.fromModel(fileModel))
        }
    }

    override suspend fun updateHistory(
        path: String,
        bookshelfId: BookshelfId,
        lastReadPage: Int,
        lastReading: Long,
    ) {
        withContext(dispatcher) {
            dao.updateHistory(UpdateFileHistoryEntity(path, bookshelfId, lastReadPage, lastReading))
        }
    }

    override suspend fun updateAdditionalInfo(
        path: String,
        bookshelfId: BookshelfId,
        cacheKey: String,
        totalPage: Int,
    ) {
        withContext(dispatcher) {
            dao.updateInfo(UpdateFileInfoEntity(path, bookshelfId, cacheKey, totalPage))
        }
    }

    override suspend fun updateSimpleAll(list: List<File>) {
        withContext(dispatcher) {
            dao.updateSimple(list.map(UpdateFileEntityMinimumWithSortIndex::fromModel))
        }
    }

    override suspend fun updateSimple(list: File): Resource<File, LocalDataSourceQueryError> {
        return withContext(dispatcher) {
            runCatching {
                dao.updateSimpleGet(UpdateFileEntityMinimum.fromModel(list))?.toModel()
            }.fold(
                onSuccess = {
                    if (it != null) {
                        Resource.Success(it)
                    } else {
                        Resource.Error(LocalDataSourceQueryError.NotFound)
                    }
                },
                onFailure = {
                    Resource.Error(LocalDataSourceQueryError.SystemError(it))
                }
            )
        }
    }

    override suspend fun selectByNotPaths(
        bookshelfId: BookshelfId,
        path: String,
        list: List<String>,
    ): List<File> {
        return withContext(dispatcher) {
            dao.findByNotPaths(bookshelfId.value, path, list).map(FileEntity::toModel)
        }
    }

    override suspend fun deleteAll(list: List<File>) {
        withContext(dispatcher) {
            dao.deleteAll(list.map(FileEntity.Companion::fromModel))
        }
    }

    override suspend fun exists(bookshelfId: BookshelfId, path: String): Boolean {
        return withContext(dispatcher) {
            dao.find(bookshelfId.value, path) != null
        }
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
            dao.pagingSourceFileSearch(bookshelf.id, searchCondition())
        }.flow.map { it.map(QueryFileWithCountEntity::toModel) }
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun pagingSourceBookThumbnail(
        pagingConfig: PagingConfig,
        bookshelf: Bookshelf,
        file: File,
        searchCondition: () -> SearchCondition,
    ): Flow<PagingData<BookThumbnail>> {
        val remoteMediator = factory.create(bookshelf, file)
        return Pager(pagingConfig, remoteMediator = remoteMediator) {
            dao.pagingSourceFileSearch(bookshelf.id, searchCondition())
        }.flow.map { pagingData ->
            pagingData.map {
                BookThumbnail.from(it.toModel() as Book)
            }
        }
    }

    override suspend fun root(id: BookshelfId): Folder? {
        return withContext(dispatcher) {
            dao.findRootFile(id.value)?.toModel() as? Folder
        }
    }

    override suspend fun findBy(bookshelfId: BookshelfId, path: String): File? {
        return withContext(dispatcher) {
            dao.find(bookshelfId.value, path)?.toModel()
        }
    }

    override fun flow(bookshelfId: BookshelfId, path: String): Flow<File?> {
        return dao.flow(bookshelfId.value, path).map { it?.toModel() }
    }

    override fun nextFileModel(
        bookshelfId: BookshelfId,
        path: String,
        sortType: SortType,
    ): Flow<File?> {
        return flowPrevNextFile(bookshelfId, path, true, sortType)
            .map { it.firstOrNull()?.toModel() }
    }

    override fun prevFileModel(
        bookshelfId: BookshelfId,
        path: String,
        sortType: SortType,
    ): Flow<File?> {
        return flowPrevNextFile(bookshelfId, path, false, sortType)
            .map { it.firstOrNull()?.toModel() }
    }

    override suspend fun getCacheKeys(
        bookshelfId: BookshelfId,
        parent: String,
        limit: Int,
        folderThumbnailOrderModel: FolderThumbnailOrder,
    ): List<String> {
        return withContext(dispatcher) {
            when (folderThumbnailOrderModel) {
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
    }

    override suspend fun removeCacheKey(diskCacheKey: String) {
        return withContext(dispatcher) {
            dao.deleteCacheKeyBy(diskCacheKey)
        }
    }

    override fun pagingHistoryBookSource(pagingConfig: PagingConfig): Flow<PagingData<Book>> {
        return Pager(pagingConfig) {
            dao.pagingSourceHistory()
        }.flow.map { pagingData -> pagingData.map { it.toModel() as Book } }
    }

    override fun lastHistory(): Flow<File?> {
        return dao.lastHistory().map { it?.toModel() }
    }

    override suspend fun deleteThumbnails() {
        withContext(dispatcher) {
            dao.deleteAllCacheKey()
        }
    }

    override suspend fun clearCacheKey(bookshelfId: BookshelfId) {
        withContext(dispatcher) {
            dao.updateCacheKeyToEmpty(bookshelfId = bookshelfId.value)
        }
    }

    override suspend fun deleteHistory(bookshelfId: BookshelfId, list: List<String>) {
        withContext(dispatcher) {
            dao.deleteHistory(bookshelfId.value, list.toTypedArray())
        }
    }

    override suspend fun deleteAllHistory() {
        withContext(dispatcher) {
            dao.deleteAllHistory()
        }
    }

    override suspend fun updateHistory(file: File, files: List<File>) {
        withContext(dispatcher) {
            dao.updateSame(FileEntity.fromModel(file), files.map(FileEntity.Companion::fromModel))
        }
    }

    override suspend fun deleteAll2(bookshelfModelId: BookshelfId) {
        withContext(dispatcher) {
            dao.deleteAll(bookshelfModelId.value)
        }
    }

    override suspend fun getCacheKeyList(bookshelfId: BookshelfId): List<String> {
        return withContext(dispatcher) {
            dao.cacheKeyList(bookshelfId.value)
        }
    }

    private fun flowPrevNextFile(
        bookshelfId: BookshelfId,
        path: String,
        isNext: Boolean,
        sortType: SortType,
    ): Flow<List<FileEntity>> {
        val column = when (sortType) {
            is SortType.Name -> "sort_index"
            is SortType.Date -> "last_modified"
            is SortType.Size -> "size"
        }

        val comparison =
            if ((isNext && sortType.isAsc) || (!isNext && !sortType.isAsc)) ">=" else "<="
        val order =
            if ((isNext && sortType.isAsc) || (!isNext && !sortType.isAsc)) "ASC" else "DESC"
        logcat { "$path, isNext: $isNext, sortType.isAsc: ${sortType.isAsc}" }
        return dao.flowPrevNextFile(
            RoomRawQuery(
                """
                    SELECT
                    *
                    FROM
                    file
                    , (
                      SELECT
                        bookshelf_id c_bookshelf_id, parent c_parent, path c_path, $column c_$column
                      FROM
                        file
                      WHERE
                        bookshelf_id = :bookshelfId AND path = :path
                    )
                  WHERE
                    bookshelf_id = c_bookshelf_id
                    AND parent = c_parent
                    AND file_type != 'FOLDER'
                    AND path != c_path
                    AND $column $comparison c_$column
                  ORDER BY
                    $column $order
                  LIMIT 1
                  ;
                """.trimIndent(),
                onBindStatement = {
                    it.bindLong(1, bookshelfId.value.toLong())
                    it.bindText(2, path)
                }
            )
        )
    }

}
