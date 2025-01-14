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
import com.sorrowblue.comicviewer.data.database.entity.favorite.FavoriteFileEntity
import com.sorrowblue.comicviewer.data.database.entity.file.FileEntity
import com.sorrowblue.comicviewer.domain.model.bookshelf.BookshelfId
import com.sorrowblue.comicviewer.domain.model.favorite.FavoriteId
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import kotlinx.coroutines.flow.Flow

@Dao
internal interface FavoriteFileDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(favoriteFileEntity: FavoriteFileEntity): Long

    @Delete
    suspend fun delete(favoriteFileEntity: FavoriteFileEntity): Int

    @Query("SELECT file.bookshelf_id, file.cache_key FROM favorite_file INNER JOIN file ON favorite_file.favorite_id = :favoriteId AND favorite_file.bookshelf_id == file.bookshelf_id AND favorite_file.file_path == file.path WHERE file_type != 'FOLDER' AND cache_key != '' LIMIT :limit")
    suspend fun findCacheKey(favoriteId: Int, limit: Int): List<BookshelfIdCacheKey>

    @RawQuery(observedEntities = [FavoriteFileEntity::class, FileEntity::class])
    fun pagingSource(query: RoomRawQuery): PagingSource<Int, FileEntity>

    @RawQuery(observedEntities = [FileEntity::class])
    fun flowPrevNext(query: RoomRawQuery): Flow<List<FileEntity>>
}

internal fun FavoriteFileDao.pagingSource(
    favoriteId: Int,
    sortType: SortType,
): PagingSource<Int, FileEntity> {
    val orderBy = when (sortType) {
        is SortType.Name -> if (sortType.isAsc) "file_type_order, sort_index" else "file_type_order DESC, sort_index DESC"
        is SortType.Date -> if (sortType.isAsc) "file_type_order, last_modified, sort_index" else "file_type_order DESC, last_modified DESC, sort_index DESC"
        is SortType.Size -> if (sortType.isAsc) "file_type_order, size, sort_index" else "file_type_order DESC, size DESC, sort_index DESC"
    }
    return pagingSource(
        RoomRawQuery(
            """
                SELECT
                  file.*
                FROM
                  favorite_file
                INNER JOIN
                  file
                ON
                  favorite_file.bookshelf_id = file.bookshelf_id AND favorite_file.file_path = file.path
                WHERE
                  favorite_id = :favoriteId
                ORDER BY
                  $orderBy
                """.trimIndent()
        ) {
            it.bindLong(1, favoriteId.toLong())
        }
    )
}


internal fun FavoriteFileDao.flowPrevNext(
    favoriteId: FavoriteId,
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
    val comparison = if (isNext && sortType.isAsc) ">=" else "<="
    val order = if (isNext && sortType.isAsc) "ASC" else "DESC"
    return flowPrevNext(
        RoomRawQuery(
            """
                        WITH
                          tmp as (
                            SELECT file.*
                            FROM favorite_file
                            INNER JOIN file ON
                              favorite_file.favorite_id = :favoriteId
                              AND favorite_file.bookshelf_id = file.bookshelf_id
                              AND favorite_file.file_path = file.path
                          )
                        SELECT *
                        FROM tmp, (
                          SELECT path c_path, $column c_$column
                          FROM tmp
                          WHERE bookshelf_id = :bookshelfId AND path = :path
                        )
                        WHERE file_type != 'FOLDER' AND path != c_path AND $column $comparison c_$column
                        ORDER BY $column $order
                        LIMIT 1
                        ;
                    """.trimIndent()
        ) {
            it.bindLong(1, favoriteId.value.toLong())
            it.bindLong(2, bookshelfId.value.toLong())
            it.bindText(3, path)
        }
    )
}
