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
import com.sorrowblue.comicviewer.data.common.bookshelf.SortType
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
    suspend fun selectCacheKeysSortLastModified(bookshelfId: Int, parent: String, limit: Int): List<String>

    @Query("SELECT cache_key FROM file WHERE bookshelf_id = :bookshelfId AND parent LIKE :parent AND file_type != 'FOLDER' AND cache_key != '' ORDER BY last_read DESC LIMIT :limit")
    suspend fun selectCacheKeysSortLastRead(bookshelfId: Int, parent: String, limit: Int): List<String>

    @Query("UPDATE file SET cache_key = '' WHERE cache_key = :cacheKey")
    suspend fun removeCacheKey(cacheKey: String)

    @Query("SELECT * FROM file WHERE bookshelf_id = :bookshelfId AND parent = ''")
    suspend fun selectRootBy(bookshelfId: Int): File?


    fun pagingSource(bookshelfId: Int, parent: String, sortType: SortType): PagingSource<Int, File> {
        val query = SupportSQLiteQueryBuilder.builder("file").apply {
            columns(arrayOf("*"))
            selection("bookshelf_id = :bookshelfId AND parent = :parent", arrayOf(bookshelfId, parent))
            when (sortType) {
                is SortType.NAME -> if (sortType.isAsc) "file_type_order, sort_index" else "file_type_order DESC, sort_index DESC"
                is SortType.DATE -> if (sortType.isAsc) "file_type_order, last_modified, sort_index" else "file_type_order DESC, last_modified DESC, sort_index DESC"
                is SortType.SIZE -> if (sortType.isAsc) "file_type_order, size, sort_index" else "file_type_order DESC, size DESC, sort_index DESC"
            }.let(::orderBy)
        }.create()
        @Suppress("DEPRECATION") return pagingSource(query)
    }

    fun pagingSourceQuery(bookshelfId: Int, q: String, sortType: SortType): PagingSource<Int, File> {
        val query = SupportSQLiteQueryBuilder.builder("file").apply {
            columns(arrayOf("*"))
            selection("bookshelf_id = :bookshelfId AND name LIKE :path", arrayOf(bookshelfId, "%$q%"))
            when (sortType) {
                is SortType.NAME -> if (sortType.isAsc) "file_type_order, sort_index" else "file_type_order DESC, sort_index DESC"
                is SortType.DATE -> if (sortType.isAsc) "file_type_order, last_modified, sort_index" else "file_type_order DESC, last_modified DESC, sort_index DESC"
                is SortType.SIZE -> if (sortType.isAsc) "file_type_order, size, sort_index" else "file_type_order DESC, size DESC, sort_index DESC"
            }.let(::orderBy)
        }.create()
        @Suppress("DEPRECATION") return pagingSource(query)
    }

}
