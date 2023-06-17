package com.sorrowblue.comicviewer.data.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Update
import androidx.room.Upsert
import androidx.sqlite.db.SupportSQLiteProgram
import androidx.sqlite.db.SupportSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQueryBuilder
import com.sorrowblue.comicviewer.data.common.bookshelf.SearchConditionEntity
import com.sorrowblue.comicviewer.data.common.bookshelf.SortEntity
import com.sorrowblue.comicviewer.data.database.entity.File
import com.sorrowblue.comicviewer.data.database.entity.FileWithCount
import com.sorrowblue.comicviewer.data.database.entity.SimpleFile
import com.sorrowblue.comicviewer.data.database.entity.UpdateFileHistory
import com.sorrowblue.comicviewer.data.database.entity.UpdateFileInfo
import kotlinx.coroutines.flow.Flow
import logcat.logcat

@Dao
internal interface FileDao {

    @Upsert
    suspend fun upsert(file: File): Long

    @Upsert
    suspend fun upsertAll(file: List<File>): List<Long>

    @Update(entity = File::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateHistory(updateFileHistory: UpdateFileHistory): Int

    @Update(entity = File::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateInfo(updateFileInfo: UpdateFileInfo)

    @Update(entity = File::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateAllSimple(list: List<SimpleFile>)

    @Delete
    suspend fun deleteAll(list: List<File>)

    @Query("SELECT * FROM file WHERE bookshelf_id = :bookshelfId AND path = :path")
    suspend fun find(bookshelfId: Int, path: String): File?

    @Query("SELECT * FROM file WHERE bookshelf_id= :bookshelfId AND path = :path")
    fun flow(bookshelfId: Int, path: String): Flow<File?>

    @Query("SELECT * FROM file WHERE bookshelf_id = :id AND parent = :parent AND path NOT IN (:paths)")
    suspend fun findByNotPaths(id: Int, parent: String, paths: List<String>): List<File>

    @Deprecated("使用禁止")
    @RawQuery(observedEntities = [File::class])
    fun flowPrevNextFile(supportSQLiteQuery: SupportSQLiteQuery): Flow<File?>

    fun flowPrevNextFile(bookshelfId: Int, path: String, isNext: Boolean, sortEntity: SortEntity): Flow<File?> {
        val column = when (sortEntity) {
            is SortEntity.NAME -> "sort_index"
            is SortEntity.DATE -> "last_modified"
            is SortEntity.SIZE -> "size"
        }
        val comparison = if (isNext && sortEntity.isAsc) ">=" else "<="
        val order = if (isNext && sortEntity.isAsc) "ASC" else "DESC"
        val sqLiteQuery = object : SupportSQLiteQuery {
            override val argCount: Int
                get() = 2
            override val sql = """
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
            """.trimIndent()

            override fun bindTo(statement: SupportSQLiteProgram) {
                statement.bindLong(1, bookshelfId.toLong())
                statement.bindString(2, path)
            }
        }
        @Suppress("DEPRECATION")
        return flowPrevNextFile(sqLiteQuery)
    }

    @Deprecated("使用禁止")
    @RawQuery(observedEntities = [File::class])
    fun pagingSource(query: SupportSQLiteQuery): PagingSource<Int, FileWithCount>

    @Query("SELECT cache_key FROM file WHERE bookshelf_id = :bookshelfId AND parent LIKE :parent AND file_type != 'FOLDER' AND cache_key != '' ORDER BY parent, sort_index LIMIT :limit")
    suspend fun findCacheKeyOrderSortIndex(
        bookshelfId: Int,
        parent: String,
        limit: Int
    ): List<String>

    @Query("SELECT cache_key FROM file WHERE bookshelf_id = :bookshelfId AND parent LIKE :parent AND file_type != 'FOLDER' AND cache_key != '' ORDER BY last_modified DESC LIMIT :limit")
    suspend fun findCacheKeyOrderLastModified(
        bookshelfId: Int,
        parent: String,
        limit: Int
    ): List<String>

    @Query("SELECT cache_key FROM file WHERE bookshelf_id = :bookshelfId AND parent LIKE :parent AND file_type != 'FOLDER' AND cache_key != '' ORDER BY last_read DESC LIMIT :limit")
    suspend fun findCacheKeysOrderLastRead(
        bookshelfId: Int,
        parent: String,
        limit: Int
    ): List<String>

    @Query("UPDATE file SET cache_key = '' WHERE cache_key = :cacheKey")
    suspend fun deleteCacheKeyBy(cacheKey: String)

    @Query("SELECT * FROM file WHERE bookshelf_id = :bookshelfId AND parent = ''")
    suspend fun findRootFile(bookshelfId: Int): File?

    fun pagingSource(
        bookshelfId: Int,
        searchConditionEntity: SearchConditionEntity,
        sortEntity: SortEntity
    ): PagingSource<Int, FileWithCount> {
        val query = SupportSQLiteQueryBuilder.builder("file").apply {
            columns(arrayOf("*", """
                CASE
                  WHEN file_type = 'FOLDER' then (SELECT COUNT(f1.path) FROM file f1 WHERE f1.parent = file.path)
                  else 0
                END count
            """.trimIndent()))
            var selectionStr = "bookshelf_id = :bookshelfId"
            val bindArgs = mutableListOf<Any>(bookshelfId)

            when (val range = searchConditionEntity.range) {
                is SearchConditionEntity.Range.IN_FOLDER -> {
                    selectionStr += " AND parent = :parent"
                    bindArgs += range.parent
                }

                is SearchConditionEntity.Range.FOLDER_BELOW -> {
                    selectionStr += " AND parent LIKE :parent"
                    bindArgs += "${range.parent}%"
                }

                SearchConditionEntity.Range.BOOKSHELF -> Unit
            }

            if (searchConditionEntity.query != null) {
                selectionStr += " AND name LIKE :q"
                bindArgs += "%${searchConditionEntity.query}%"
            }

            selectionStr += when (searchConditionEntity.period) {
                SearchConditionEntity.Period.NONE -> ""
                SearchConditionEntity.Period.HOUR_24 -> " AND last_modified > strftime('%s000', datetime('now', '-24 hours'))"
                SearchConditionEntity.Period.WEEK_1 -> " AND last_modified > strftime('%s000', datetime('now', '-7 days'))"
                SearchConditionEntity.Period.MONTH_1 -> " AND last_modified > strftime('%s000', datetime('now', '-1 months'))"
            }

            selection(selectionStr, bindArgs.toTypedArray())
            when (sortEntity) {
                is SortEntity.NAME -> if (sortEntity.isAsc) "file_type_order, sort_index" else "file_type_order DESC, sort_index DESC"
                is SortEntity.DATE -> if (sortEntity.isAsc) "file_type_order, last_modified, sort_index" else "file_type_order DESC, last_modified DESC, sort_index DESC"
                is SortEntity.SIZE -> if (sortEntity.isAsc) "file_type_order, size, sort_index" else "file_type_order DESC, size DESC, sort_index DESC"
            }.let(::orderBy)
        }.create()
        logcat { query.sql }
        @Suppress("DEPRECATION") return pagingSource(query)
    }

    @Query("SELECT * FROM file WHERE file_type != 'FOLDER' AND last_read != 0 ORDER BY last_read DESC")
    fun pagingSourceHistory(): PagingSource<Int, File>

    @Query("UPDATE file SET cache_key = '' WHERE cache_key != ''")
    suspend fun deleteAllCacheKey()

    @Query("UPDATE file set last_read = 0, last_read_page = 0  WHERE bookshelf_id = :bookshelfId AND path IN (:list)")
    suspend fun deleteHistory(bookshelfId: Int, list: Array<String>)
}
