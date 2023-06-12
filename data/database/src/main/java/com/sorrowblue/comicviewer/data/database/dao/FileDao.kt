package com.sorrowblue.comicviewer.data.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Update
import androidx.room.Upsert
import androidx.sqlite.db.SupportSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQueryBuilder
import com.sorrowblue.comicviewer.data.common.bookshelf.SearchConditionEntity
import com.sorrowblue.comicviewer.data.common.bookshelf.SortEntity
import com.sorrowblue.comicviewer.data.database.entity.File
import com.sorrowblue.comicviewer.data.database.entity.SimpleFile
import com.sorrowblue.comicviewer.data.database.entity.UpdateFileHistory
import com.sorrowblue.comicviewer.data.database.entity.UpdateFileInfo
import kotlinx.coroutines.flow.Flow

@Dao
internal interface FileDao {

    @Upsert
    suspend fun upsert(file: File): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(file: File): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(file: List<File>): List<Long>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(file: File): Int

    @Update(entity = File::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(updateFileHistory: UpdateFileHistory): Int

    @Update(entity = File::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(updateFileInfo: UpdateFileInfo)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateAll(file: List<File>)

    @Delete
    suspend fun deleteAll(list: List<File>)

    @Update(entity = File::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateAllSimple(list: List<SimpleFile>)

    @Query("SELECT * FROM file WHERE bookshelf_id = :bookshelfId AND path = :path")
    suspend fun selectBy(bookshelfId: Int, path: String): File?

    @Query("SELECT * FROM file WHERE bookshelf_id= :bookshelfId AND path = :path")
    fun selectBy2(bookshelfId: Int, path: String): Flow<File?>

    @Query("SELECT * FROM file WHERE bookshelf_id = :bookshelfId")
    suspend fun selectBy(bookshelfId: Int): List<File>

    @Query("SELECT * FROM file WHERE bookshelf_id = :id AND parent = :parent AND path NOT IN (:paths)")
    suspend fun selectByNotPaths(id: Int, parent: String, paths: List<String>): List<File>

    @Deprecated("使用禁止")
    @RawQuery(observedEntities = [File::class])
    fun pagingSource(query: SupportSQLiteQuery): PagingSource<Int, File>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM file, (SELECT sort_index AS current_sort_index, parent current_parent FROM file WHERE bookshelf_id = :bookshelfId AND path = :path) WHERE bookshelf_id = :bookshelfId AND parent = current_parent AND file_type != 'FOLDER' AND sort_index > current_sort_index ORDER BY sort_index LIMIT 1")
    fun selectNextFile(bookshelfId: Int, path: String): Flow<File?>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM file, (SELECT sort_index si, parent pa FROM file WHERE bookshelf_id = :bookshelfId AND path = :path) WHERE bookshelf_id = :bookshelfId AND parent = pa AND file_type != 'FOLDER' AND sort_index < si ORDER BY sort_index DESC LIMIT 1")
    fun selectPrevFile(bookshelfId: Int, path: String): Flow<File?>

    @Query("SELECT cache_key FROM file WHERE bookshelf_id = :bookshelfId AND parent LIKE :parent AND file_type != 'FOLDER' AND cache_key != '' ORDER BY parent, sort_index LIMIT :limit")
    suspend fun selectCacheKeysSortIndex(bookshelfId: Int, parent: String, limit: Int): List<String>

    @Query("SELECT cache_key FROM file WHERE bookshelf_id = :bookshelfId AND parent LIKE :parent AND file_type != 'FOLDER' AND cache_key != '' ORDER BY last_modified DESC LIMIT :limit")
    suspend fun selectCacheKeysSortLastModified(
        bookshelfId: Int,
        parent: String,
        limit: Int
    ): List<String>

    @Query("SELECT cache_key FROM file WHERE bookshelf_id = :bookshelfId AND parent LIKE :parent AND file_type != 'FOLDER' AND cache_key != '' ORDER BY last_read DESC LIMIT :limit")
    suspend fun selectCacheKeysSortLastRead(
        bookshelfId: Int,
        parent: String,
        limit: Int
    ): List<String>

    @Query("UPDATE file SET cache_key = '' WHERE cache_key = :cacheKey")
    suspend fun removeCacheKey(cacheKey: String)

    @Query("SELECT * FROM file WHERE bookshelf_id = :bookshelfId AND parent = ''")
    suspend fun selectRootBy(bookshelfId: Int): File?

    fun pagingSource(
        bookshelfId: Int,
        searchConditionEntity: SearchConditionEntity,
        sortEntity: SortEntity
    ): PagingSource<Int, File> {
        val query = SupportSQLiteQueryBuilder.builder("file").apply {
            columns(arrayOf("*"))
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
        @Suppress("DEPRECATION") return pagingSource(query)
    }

    @Query("SELECT * FROM file WHERE file_type != 'FOLDER' AND last_read != 0 ORDER BY last_read DESC")
    fun pagingHistoryBookSource(): PagingSource<Int, File>

    @Query("UPDATE file SET cache_key = '' WHERE cache_key != ''")
    suspend fun deleteThumbnails()

    @Query("UPDATE file set last_read = 0, last_read_page = 0  WHERE bookshelf_id = :bookshelfId AND path IN (:list)")
    suspend fun deleteHistory(bookshelfId: Int, list: Array<String>)
}
