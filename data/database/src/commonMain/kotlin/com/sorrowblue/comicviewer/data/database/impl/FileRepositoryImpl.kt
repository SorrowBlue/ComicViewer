/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.data.database.impl

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room3.RoomRawQuery
import com.sorrowblue.comicviewer.data.database.FileModelRemoteMediator
import com.sorrowblue.comicviewer.data.database.dao.FileDao
import com.sorrowblue.comicviewer.data.database.dao.flowPrevNextFile
import com.sorrowblue.comicviewer.data.database.dao.pagingSourceFileSearch
import com.sorrowblue.comicviewer.data.database.entity.file.FileEntity
import com.sorrowblue.comicviewer.data.database.entity.file.QueryFileWithCountEntity
import com.sorrowblue.comicviewer.data.database.entity.file.UpdateFileEntityMinimum
import com.sorrowblue.comicviewer.data.database.entity.file.UpdateFileHistoryEntity
import com.sorrowblue.comicviewer.data.database.entity.file.UpdateFileInfoEntity
import com.sorrowblue.comicviewer.data.database.entity.file.UpdateFileTypeInfoEntity
import com.sorrowblue.comicviewer.domain.model.bookshelf.Bookshelf
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.common.IoDispatcher
import com.sorrowblue.comicviewer.domain.model.common.Resource
import com.sorrowblue.comicviewer.domain.model.file.Book
import com.sorrowblue.comicviewer.domain.model.file.BookFile
import com.sorrowblue.comicviewer.domain.model.file.BookFolder
import com.sorrowblue.comicviewer.domain.model.file.BookThumbnail
import com.sorrowblue.comicviewer.domain.model.file.File
import com.sorrowblue.comicviewer.domain.model.file.Folder
import com.sorrowblue.comicviewer.domain.model.search.SearchCondition
import com.sorrowblue.comicviewer.domain.model.settings.folder.FolderThumbnailOrder
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import com.sorrowblue.comicviewer.domain.repository.FileRepository
import com.sorrowblue.comicviewer.domain.repository.FileRepositoryQueryError
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import logcat.logcat

@ContributesBinding(AppScope::class)
internal class FileRepositoryImpl(
    private val dao: FileDao,
    private val factory: FileModelRemoteMediator.Factory,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
) : FileRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun pagingDataFlow(
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
            dao.pagingSourceFileSearch(bookshelf.id, searchCondition(), excludeFolder = true)
        }.flow.map { pagingData ->
            pagingData.map {
                BookThumbnail.from(it.toModel() as Book)
            }
        }
    }

    override suspend fun fileList(bookshelfId: BookshelfId, limit: Int, offset: Long): List<File> =
        withContext(dispatcher) {
            dao.fileList(bookshelfId.value, limit, offset).first().map {
                it.toModel()
            }
        }

    override suspend fun count(bookshelfId: BookshelfId): Long = withContext(dispatcher) {
        dao.count(bookshelfId.value).first()
    }

    override fun pagingDataFlow(
        pagingConfig: PagingConfig,
        bookshelfId: BookshelfId?,
        searchCondition: () -> SearchCondition,
    ): Flow<PagingData<File>> = Pager(pagingConfig) {
        dao.pagingSourceFileSearch(bookshelfId, searchCondition())
    }.flow.map { it.map(QueryFileWithCountEntity::toModel) }

    override suspend fun addUpdate(file: File) {
        withContext(dispatcher) {
            dao.upsert(FileEntity.fromModel(file))
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

    override suspend fun updateFileType(file: File) {
        withContext(dispatcher) {
            val fileType = when (file) {
                is BookFile -> FileEntity.Type.FILE
                is BookFolder -> FileEntity.Type.IMAGE_FOLDER
                is Folder -> FileEntity.Type.FOLDER
            }
            dao.updateFileType(UpdateFileTypeInfoEntity(file.path, file.bookshelfId, fileType))
        }
    }

    override suspend fun updateSimple(file: File): Resource<File, FileRepositoryQueryError> =
        withContext(
            dispatcher,
        ) {
            runCatching {
                dao.updateSimpleGet(UpdateFileEntityMinimum.fromModel(file))?.toModel()
            }.fold(
                onSuccess = {
                    if (it != null) {
                        Resource.Success(it)
                    } else {
                        Resource.Error(FileRepositoryQueryError.NotFound)
                    }
                },
                onFailure = {
                    if (it is CancellationException) throw it
                    Resource.Error(FileRepositoryQueryError.SystemError(it))
                },
            )
        }

    override fun pagingSource(
        bookshelfId: BookshelfId,
        pagingConfig: PagingConfig,
    ): Flow<PagingData<BookThumbnail>> = Pager(pagingConfig) {
        dao.pagingSourceFileOnBookshelf(bookshelfId.value)
    }.flow.map { pagingData ->
        pagingData.map { BookThumbnail.from(it.toModel() as Book) }
    }

    override suspend fun root(id: BookshelfId): Folder? = withContext(dispatcher) {
        dao.findRootFile(id.value)?.toModel() as? Folder
    }

    override suspend fun findBy(bookshelfId: BookshelfId, path: String): File? = withContext(
        dispatcher,
    ) {
        dao.find(bookshelfId.value, path)?.toModel()
    }

    override fun flow(bookshelfId: BookshelfId, path: String): Flow<File?> = dao
        .flow(
            bookshelfId.value,
            path,
        ).map {
            it?.toModel()
        }

    override fun nextFile(
        bookshelfId: BookshelfId,
        path: String,
        sortType: SortType,
    ): Flow<File?> = flowPrevNextFile(bookshelfId, path, true, sortType)
        .map { it.firstOrNull()?.toModel() }

    override fun nextFile(
        bookshelfId: BookshelfId,
        path: String,
        searchCondition: SearchCondition,
        sortType: SortType,
    ): Flow<File?> = dao.flowPrevNextFile(bookshelfId, path, searchCondition, true, sortType)
        .map { it.firstOrNull()?.toModel() }

    override fun prevFile(
        bookshelfId: BookshelfId,
        path: String,
        sortType: SortType,
    ): Flow<File?> = flowPrevNextFile(bookshelfId, path, false, sortType)
        .map { it.firstOrNull()?.toModel() }

    override fun prevFile(
        bookshelfId: BookshelfId,
        path: String,
        searchCondition: SearchCondition,
        sortType: SortType,
    ): Flow<File?> = dao.flowPrevNextFile(bookshelfId, path, searchCondition, false, sortType)
        .map { it.firstOrNull()?.toModel() }

    override suspend fun getCacheKeys(
        bookshelfId: BookshelfId,
        parent: String,
        limit: Int,
        folderThumbnailOrder: FolderThumbnailOrder,
    ): List<String> = withContext(dispatcher) {
        when (folderThumbnailOrder) {
            FolderThumbnailOrder.NAME -> dao.findCacheKeyOrderSortIndex(
                bookshelfId.value,
                "$parent%",
                limit,
            )

            FolderThumbnailOrder.MODIFIED -> dao.findCacheKeyOrderLastModified(
                bookshelfId.value,
                "$parent%",
                limit,
            )

            FolderThumbnailOrder.LAST_READ -> dao.findCacheKeysOrderLastRead(
                bookshelfId.value,
                "$parent%",
                limit,
            )
        }
    }

    override suspend fun removeCacheKey(diskCacheKey: String) = withContext(dispatcher) {
        dao.deleteCacheKeyBy(diskCacheKey)
    }

    override fun pagingHistoryBookSource(pagingConfig: PagingConfig): Flow<PagingData<Book>> =
        Pager(
            pagingConfig,
        ) {
            dao.pagingSourceHistory()
        }.flow
            .map { pagingData -> pagingData.map { it.toModel() as Book } }

    override fun lastHistory(): Flow<File?> = dao.lastHistory().map { it?.toModel() }

    override suspend fun clearCacheKey(bookshelfId: BookshelfId) {
        withContext(dispatcher) {
            dao.updateCacheKeyToEmpty(bookshelfId = bookshelfId.value)
        }
    }

    override suspend fun deleteAllHistory() {
        withContext(dispatcher) {
            dao.deleteAllHistory()
        }
    }

    override suspend fun syncFolderFiles(file: File, files: List<File>) {
        withContext(dispatcher) {
            dao.updateSame(FileEntity.fromModel(file), files.map(FileEntity.Companion::fromModel))
        }
    }

    override suspend fun deleteAll(bookshelfId: BookshelfId) {
        withContext(dispatcher) {
            dao.deleteAll(bookshelfId.value)
        }
    }

    override suspend fun getCacheKeyList(bookshelfId: BookshelfId): List<String> = withContext(
        dispatcher,
    ) {
        dao.cacheKeyList(bookshelfId.value)
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

        val op =
            if ((isNext && sortType.isAsc) || (!isNext && !sortType.isAsc)) ">" else "<"
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
                      bookshelf_id c_bookshelf_id, parent c_parent, path c_path, sort_index c_sort_index, $column c_$column
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
                  AND (
                    $column $op c_$column
                    OR ($column = c_$column AND sort_index $op c_sort_index)
                    OR ($column = c_$column AND sort_index = c_sort_index AND path $op c_path)
                  )
                ORDER BY
                  $column $order, sort_index $order, path $order
                LIMIT 1
                ;
                """.trimIndent(),
                onBindStatement = {
                    it.bindLong(1, bookshelfId.value.toLong())
                    it.bindText(2, path)
                },
            ),
        )
    }
}
