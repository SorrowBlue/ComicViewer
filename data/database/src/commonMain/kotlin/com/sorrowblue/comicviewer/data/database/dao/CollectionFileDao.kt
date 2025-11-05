package com.sorrowblue.comicviewer.data.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.RoomRawQuery
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
        "SELECT file.bookshelf_id, file.cache_key FROM collection_file INNER JOIN file ON collection_file.collection_id = :id AND collection_file.bookshelf_id == file.bookshelf_id AND collection_file.file_path == file.path WHERE file_type != 'FOLDER' AND cache_key != '' LIMIT :limit",
    )
    suspend fun findBasicCollectionFileCacheKey(id: CollectionId, limit: Int): List<BookshelfIdCacheKey>
}

internal fun CollectionFileDao.pagingSource(collectionId: Int, sortType: SortType): PagingSource<Int, FileEntity> {
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
