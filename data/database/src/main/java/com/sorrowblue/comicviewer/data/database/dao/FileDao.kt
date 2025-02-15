package com.sorrowblue.comicviewer.data.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Update
import androidx.room.Upsert
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQueryBuilder
import com.sorrowblue.comicviewer.data.database.entity.file.FileEntity
import com.sorrowblue.comicviewer.data.database.entity.file.QueryFileWithCountEntity
import com.sorrowblue.comicviewer.data.database.entity.file.UpdateFileEntityMinimum
import com.sorrowblue.comicviewer.data.database.entity.file.UpdateFileEntityMinimumWithSortIndex
import com.sorrowblue.comicviewer.data.database.entity.file.UpdateFileHistoryEntity
import com.sorrowblue.comicviewer.data.database.entity.file.UpdateFileInfoEntity
import com.sorrowblue.comicviewer.domain.model.SearchCondition
import com.sorrowblue.comicviewer.domain.model.settings.folder.SortType
import kotlinx.coroutines.flow.Flow
import logcat.logcat

@Dao
internal interface FileDao {

    @Upsert
    suspend fun upsert(fileEntity: FileEntity): Long

    @Upsert
    suspend fun upsertAll(fileEntity: List<FileEntity>): List<Long>

    @Update(entity = FileEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateHistory(updateFileHistoryEntity: UpdateFileHistoryEntity): Int

    @Update(entity = FileEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateInfo(updateFileInfoEntity: UpdateFileInfoEntity)

    @Update(entity = FileEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateSimple(entity: UpdateFileEntityMinimum): Int

    @Update(entity = FileEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateSimple(list: List<UpdateFileEntityMinimumWithSortIndex>)

    @Delete
    suspend fun deleteAll(list: List<FileEntity>)

    @Query("SELECT * FROM file WHERE bookshelf_id = :bookshelfId AND path = :path")
    suspend fun find(bookshelfId: Int, path: String): FileEntity?

    @Query("SELECT * FROM file WHERE bookshelf_id= :bookshelfId AND path = :path")
    fun flow(bookshelfId: Int, path: String): Flow<FileEntity?>

    @Query("SELECT * FROM file WHERE bookshelf_id = :id AND parent = :parent AND path NOT IN (:paths)")
    suspend fun findByNotPaths(id: Int, parent: String, paths: List<String>): List<FileEntity>

    @RawQuery(observedEntities = [FileEntity::class])
    fun flowPrevNextFile(supportSQLiteQuery: SupportSQLiteQuery): Flow<List<FileEntity>>

    fun flowPrevNextFile(
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

        val comparison =
            if ((isNext && sortType.isAsc) || (!isNext && !sortType.isAsc)) ">=" else "<="
        val order = if ((isNext && sortType.isAsc) || (!isNext && !sortType.isAsc)) "ASC" else "DESC"
        logcat { "$path, isNext: $isNext, sortType.isAsc: ${sortType.isAsc}" }
        return flowPrevNextFile(
            SimpleSQLiteQuery(
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
                arrayOf<Any>(bookshelfId.toLong(), path)
            )
        )
    }

    @RawQuery(observedEntities = [FileEntity::class])
    fun pagingSource(query: SupportSQLiteQuery): PagingSource<Int, QueryFileWithCountEntity>

    @Query(
        "SELECT cache_key FROM file WHERE bookshelf_id = :bookshelfId AND parent LIKE :parent AND file_type != 'FOLDER' AND cache_key != '' ORDER BY parent, sort_index LIMIT :limit"
    )
    suspend fun findCacheKeyOrderSortIndex(
        bookshelfId: Int,
        parent: String,
        limit: Int,
    ): List<String>

    @Query(
        "SELECT cache_key FROM file WHERE bookshelf_id = :bookshelfId AND parent LIKE :parent AND file_type != 'FOLDER' AND cache_key != '' ORDER BY last_modified DESC LIMIT :limit"
    )
    suspend fun findCacheKeyOrderLastModified(
        bookshelfId: Int,
        parent: String,
        limit: Int,
    ): List<String>

    @Query(
        "SELECT cache_key FROM file WHERE bookshelf_id = :bookshelfId AND parent LIKE :parent AND file_type != 'FOLDER' AND cache_key != '' ORDER BY last_read DESC LIMIT :limit"
    )
    suspend fun findCacheKeysOrderLastRead(
        bookshelfId: Int,
        parent: String,
        limit: Int,
    ): List<String>

    @Query("UPDATE file SET cache_key = '' WHERE cache_key = :cacheKey")
    suspend fun deleteCacheKeyBy(cacheKey: String)

    @Query("UPDATE file SET cache_key = '' WHERE bookshelf_id = :bookshelfId AND cache_key != ''")
    suspend fun updateCacheKeyToEmpty(bookshelfId: Int)

    @Query("SELECT * FROM file WHERE bookshelf_id = :bookshelfId AND parent = ''")
    suspend fun findRootFile(bookshelfId: Int): FileEntity?

    fun pagingSource(
        bookshelfId: Int,
        searchCondition: SearchCondition,
    ): PagingSource<Int, QueryFileWithCountEntity> {
        val query = SupportSQLiteQueryBuilder.builder("file").apply {
            columns(
                arrayOf(
                    "*",
                    """
                CASE
                  WHEN file_type = 'FOLDER' then (SELECT COUNT(f1.path) FROM file f1 WHERE f1.parent = file.path)
                  else 0
                END count
                    """.trimIndent()
                )
            )
            var selectionStr = "bookshelf_id = :bookshelfId"
            val bindArgs = mutableListOf<Any>(bookshelfId)

            if (!searchCondition.showHidden) {
                selectionStr += " AND hidden = :hidden"
                selectionStr += " AND name NOT LIKE '.%'"
                bindArgs += false
            }

            when (val range = searchCondition.range) {
                is SearchCondition.Range.InFolder -> {
                    selectionStr += " AND parent = :parent"
                    bindArgs += range.parent
                }

                is SearchCondition.Range.SubFolder -> {
                    selectionStr += " AND parent LIKE :parent"
                    bindArgs += "${range.parent}%"
                }

                SearchCondition.Range.Bookshelf -> {
                    selectionStr += " AND parent != ''"
                }
            }

            if (searchCondition.query.isNotEmpty()) {
                selectionStr += " AND name LIKE :q"
                bindArgs += "%${searchCondition.query}%"
            }

            selectionStr += when (searchCondition.period) {
                SearchCondition.Period.None -> ""
                SearchCondition.Period.Hour24 -> " AND last_modified > strftime('%s000', datetime('now', '-24 hours'))"
                SearchCondition.Period.Week1 -> " AND last_modified > strftime('%s000', datetime('now', '-7 days'))"
                SearchCondition.Period.Month1 -> " AND last_modified > strftime('%s000', datetime('now', '-1 months'))"
            }

            selection(selectionStr, bindArgs.toTypedArray())
            val sortStr = if (searchCondition.sortType.isAsc) "ASC" else "DESC"
            when (searchCondition.sortType) {
                is SortType.Name -> "file_type_order $sortStr, sort_index $sortStr"
                is SortType.Date -> "file_type_order $sortStr, last_modified $sortStr, sort_index $sortStr"
                is SortType.Size -> "file_type_order $sortStr, size $sortStr, sort_index $sortStr"
            }.let(::orderBy)
        }.create()
        logcat { query.sql.trimIndent().replace(Regex("""\r\n|\n|\r"""), "") }
        return pagingSource(query)
    }

    @Query("SELECT * FROM file WHERE file_type != 'FOLDER' AND last_read != 0 ORDER BY last_read DESC")
    fun pagingSourceHistory(): PagingSource<Int, FileEntity>

    @Query("SELECT * FROM file WHERE file_type != 'FOLDER' AND last_read != 0 ORDER BY last_read DESC LIMIT 1")
    fun lastHistory(): Flow<FileEntity>

    @Query("UPDATE file SET cache_key = '' WHERE cache_key != ''")
    suspend fun deleteAllCacheKey()

    @Query("UPDATE file set last_read = 0, last_read_page = 0  WHERE bookshelf_id = :bookshelfId AND path IN (:list)")
    suspend fun deleteHistory(bookshelfId: Int, list: Array<String>)

    @Query("DELETE FROM file WHERE bookshelf_id = :id")
    suspend fun deleteAll(id: Int)

    @Query("SELECT cache_key FROM file WHERE bookshelf_id = :id")
    suspend fun cacheKeyList(id: Int): List<String>

    @Query(
        "SELECT * FROM file WHERE bookshelf_id = :id AND file_type = 'FILE' ORDER BY path LIMIT :limit OFFSET :offset"
    )
    fun fileList(id: Int, limit: Int, offset: Long): Flow<List<FileEntity>>

    @Query("SELECT Count(*) FROM file WHERE bookshelf_id = :id AND file_type = 'FILE'")
    fun count(id: Int): Flow<Long>

    @Query("UPDATE file SET last_read = 0, last_read_page = 0")
    suspend fun deleteAllHistory()
}
