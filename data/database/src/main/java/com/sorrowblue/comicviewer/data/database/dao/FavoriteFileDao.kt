package com.sorrowblue.comicviewer.data.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteProgram
import androidx.sqlite.db.SupportSQLiteQuery
import com.sorrowblue.comicviewer.data.common.bookshelf.SortEntity
import com.sorrowblue.comicviewer.data.database.entity.FavoriteFile
import com.sorrowblue.comicviewer.data.database.entity.File
import kotlinx.coroutines.flow.Flow

@Dao
internal interface FavoriteFileDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(favoriteFile: FavoriteFile): Long

    @Delete
    suspend fun delete(favoriteFile: FavoriteFile): Int

    @Deprecated("", level = DeprecationLevel.WARNING)
    @RawQuery(observedEntities = [FavoriteFile::class, File::class])
    fun pagingSource(query: SupportSQLiteQuery): PagingSource<Int, File>

    fun pagingSource(favoriteId: Int, sortType: SortEntity): PagingSource<Int, File> {
        val orderBy = when (sortType) {
            is SortEntity.NAME -> if (sortType.isAsc) "file_type_order, sort_index" else "file_type_order DESC, sort_index DESC"
            is SortEntity.DATE -> if (sortType.isAsc) "file_type_order, last_modified, sort_index" else "file_type_order DESC, last_modified DESC, sort_index DESC"
            is SortEntity.SIZE -> if (sortType.isAsc) "file_type_order, size, sort_index" else "file_type_order DESC, size DESC, sort_index DESC"
        }
        @Suppress("DEPRECATION")
        return pagingSource(object : SupportSQLiteQuery {
            override val argCount = 1
            override val sql = """
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

            override fun bindTo(statement: SupportSQLiteProgram) {
                statement.bindLong(1, favoriteId.toLong())
            }

        })
    }

    @Query("SELECT cache_key FROM favorite_file INNER JOIN file ON favorite_file.favorite_id = :favoriteId AND favorite_file.bookshelf_id == file.bookshelf_id AND favorite_file.file_path == file.path WHERE file_type != 'FOLDER' AND cache_key != '' LIMIT :limit")
    suspend fun findCacheKey(favoriteId: Int, limit: Int): List<String>

    @Deprecated("使用禁止")
    @RawQuery(observedEntities = [File::class])
    fun flowPrevNext(supportSQLiteQuery: SupportSQLiteQuery): Flow<File?>

    fun flowPrevNext(favoriteId: Int, bookshelfId: Int, path: String, isNext: Boolean, sortEntity: SortEntity): Flow<File?> {
        val column = when (sortEntity) {
            is SortEntity.NAME -> "sort_index"
            is SortEntity.DATE -> "last_modified"
            is SortEntity.SIZE -> "size"
        }
        val comparison = if (isNext && sortEntity.isAsc) ">=" else "<="
        val order = if (isNext && sortEntity.isAsc) "ASC" else "DESC"
        val sqLiteQuery = object : SupportSQLiteQuery {
            override val argCount = 3
            override val sql = """
                WITH
                  tmp as (
                    SELECT
                      file.*
                    FROM
                      favorite_file
                    INNER JOIN
                      file
                    ON
                      favorite_file.favorite_id = :favoriteId
                      AND favorite_file.bookshelf_id = file.bookshelf_id
                      AND favorite_file.file_path = file.path
                  )
                SELECT
                  *
                FROM
                  tmp
                  , (
                    SELECT
                      path c_path, $column c_$column
                    FROM
                      tmp
                    WHERE
                      bookshelf_id = :bookshelfId AND path = :path
                  )
                WHERE
                  file_type != 'FOLDER'
                  AND path != c_path
                  AND $column $comparison c_$column
                ORDER BY
                  $column $order
                LIMIT 1
                ;
            """.trimIndent()

            override fun bindTo(statement: SupportSQLiteProgram) {
                statement.bindLong(1, favoriteId.toLong())
                statement.bindLong(2, bookshelfId.toLong())
                statement.bindString(3, path)
            }
        }
        @Suppress("DEPRECATION")
        return flowPrevNext(sqLiteQuery)
    }
}
