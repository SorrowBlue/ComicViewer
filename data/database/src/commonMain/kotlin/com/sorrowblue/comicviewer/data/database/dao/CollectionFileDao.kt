/*
 * Copyright 2026 SorrowBlue. See LICENSE for details.
 */

package com.sorrowblue.comicviewer.data.database.dao

import androidx.paging.PagingSource
import androidx.room3.Dao
import androidx.room3.DaoReturnTypeConverters
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.RawQuery
import androidx.room3.RoomRawQuery
import androidx.room3.paging.PagingSourceDaoReturnTypeConverter
import com.sorrowblue.comicviewer.data.database.entity.bookshelf.BookshelfIdCacheKey
import com.sorrowblue.comicviewer.data.database.entity.collection.CollectionFileEntity
import com.sorrowblue.comicviewer.data.database.entity.file.FileEntity
import com.sorrowblue.comicviewer.domain.model.collection.CollectionId
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the collection_file table. Provides methods for
 * inserting, deleting, and querying collection files and related file
 * entities.
 */
@Dao
@DaoReturnTypeConverters(PagingSourceDaoReturnTypeConverter::class)
internal interface CollectionFileDao {
    /**
     * Inserts a CollectionFileEntity into the database. If the entity already
     * exists, the operation is ignored.
     *
     * @param entity The CollectionFileEntity to insert.
     * @return The row ID of the inserted entity, or -1 if ignored.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: CollectionFileEntity): Long

    /**
     * Deletes a CollectionFileEntity from the database.
     *
     * @param entity The CollectionFileEntity to delete.
     * @return The number of rows deleted.
     */
    @Delete
    suspend fun delete(entity: CollectionFileEntity): Int

    /**
     * Returns a PagingSource for FileEntity based on a raw SQL query.
     *
     * @param query The RoomRawQuery containing the SQL statement.
     * @return PagingSource for FileEntity.
     */
    @RawQuery(observedEntities = [CollectionFileEntity::class, FileEntity::class])
    fun pagingSource(query: RoomRawQuery): PagingSource<Int, FileEntity>

    /**
     * Returns a Flow of a list of FileEntity for previous, current, and next
     * files based on a raw SQL query.
     *
     * @param query The RoomRawQuery containing the SQL statement.
     * @return Flow emitting a list of FileEntity.
     */
    @RawQuery(observedEntities = [FileEntity::class])
    fun flowPrevNext(query: RoomRawQuery): Flow<List<FileEntity>>

    /**
     * Finds cache keys for basic collection files that are not folders and
     * have a non-empty cache key.
     *
     * @param id The CollectionId to query.
     * @param limit The maximum number of results to return.
     * @return List of BookshelfIdCacheKey.
     */
    @Query(
        """SELECT file.bookshelf_id, file.cache_key FROM collection_file INNER JOIN file ON collection_file.collection_id = :id AND collection_file.bookshelf_id == file.bookshelf_id AND collection_file.file_path == file.path WHERE file_type != 'FOLDER' AND cache_key != '' LIMIT :limit""",
    )
    suspend fun findBasicCollectionFileCacheKey(
        id: CollectionId,
        limit: Int,
    ): List<BookshelfIdCacheKey>
}

internal fun CollectionFileDao.pagingSource(
    collectionId: Int,
    sortType: SortType,
): PagingSource<Int, FileEntity> {
    val orderBy = when (sortType) {
        is SortType.Name -> if (sortType.isAsc) {
            "file_type_order, sort_index"
        } else {
            "file_type_order DESC, sort_index DESC"
        }

        is SortType.Date -> if (sortType.isAsc) {
            "file_type_order, last_modified, sort_index"
        } else {
            "file_type_order DESC, last_modified DESC, sort_index DESC"
        }

        is SortType.Size -> if (sortType.isAsc) {
            "file_type_order, size, sort_index"
        } else {
            "file_type_order DESC, size DESC, sort_index DESC"
        }
    }
    return pagingSource(
        RoomRawQuery(
            """
            SELECT
              file.*
            FROM
              collection_file
            INNER JOIN
              file
            ON
              collection_file.bookshelf_id = file.bookshelf_id AND collection_file.file_path = file.path
            WHERE
              collection_id = :collectionId
            ORDER BY
              $orderBy
            """.trimIndent(),
        ) {
            it.bindLong(1, collectionId.toLong())
        },
    )
}

internal fun CollectionFileDao.flowPrevNextCollectionFile(
    collectionId: Int,
    bookshelfId: Int,
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

    return flowPrevNext(
        RoomRawQuery(
            """
              SELECT
                file.*
              FROM
                collection_file
              INNER JOIN
                file
              ON
                collection_file.bookshelf_id = file.bookshelf_id AND collection_file.file_path = file.path
              , (
                SELECT
                  collection_file.collection_id c_collection_id,
                  file.bookshelf_id c_bookshelf_id,
                  file.path c_path,
                  file.$column c_$column
                FROM
                  collection_file
                INNER JOIN
                  file
                ON
                  collection_file.bookshelf_id = file.bookshelf_id AND collection_file.file_path = file.path
                WHERE
                  collection_file.collection_id = ? AND file.bookshelf_id = ? AND file.path = ?
              )
              WHERE
                collection_file.collection_id = c_collection_id
                AND file.bookshelf_id = c_bookshelf_id
                AND file.file_type != 'FOLDER'
                AND file.path != c_path
                AND (file.$column $op c_$column OR (file.$column = c_$column AND file.path $op c_path))
              ORDER BY
                file.$column $order, file.path $order
              LIMIT 1
              ;
            """.trimIndent(),
        ) { statement ->
            statement.bindLong(1, collectionId.toLong())
            statement.bindLong(2, bookshelfId.toLong())
            statement.bindText(3, path)
        },
    )
}
