package com.sorrowblue.comicviewer.data.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.sqlite.db.SupportSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQueryBuilder
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

    @Query("SELECT cache_key FROM favorite_file INNER JOIN file ON favorite_file.bookshelf_id == file.bookshelf_id AND favorite_file.file_path == file.path WHERE favorite_id = :favoriteId AND file_type != 'FOLDER' AND cache_key != '' LIMIT :limit")
    suspend fun selectCacheKey(favoriteId: Int, limit: Int): List<String>

    fun pagingSource(favoriteId: Int, sortType: SortEntity): PagingSource<Int, File> {
        val query =
            SupportSQLiteQueryBuilder.builder("favorite_file INNER JOIN file ON favorite_file.bookshelf_id = file.bookshelf_id AND favorite_file.file_path = file.path")
                .apply {
                    columns(arrayOf("file.*"))
                    selection("favorite_id = :favoriteId", arrayOf(favoriteId))
                    when (sortType) {
                        is SortEntity.NAME -> if (sortType.isAsc) "file_type_order, sort_index" else "file_type_order DESC, sort_index DESC"
                        is SortEntity.DATE -> if (sortType.isAsc) "file_type_order, last_modified, sort_index" else "file_type_order DESC, last_modified DESC, sort_index DESC"
                        is SortEntity.SIZE -> if (sortType.isAsc) "file_type_order, size, sort_index" else "file_type_order DESC, size DESC, sort_index DESC"
                    }.let(::orderBy)
                }.create()
        @Suppress("DEPRECATION") return pagingSource(query)
    }

    @RewriteQueriesToDropUnusedColumns
    @Query("$NEXT_FAVORITE_WITH $NEXT_FAVORITE_SELECT sort_index c_sort_index $NEXT_FAVORITE_WHERE sort_index >= c_sort_index $ORDER_BY sort_index asc $LIMIT_1")
    fun flowNextOrderNameAsc(favoriteId: Int, bookshelfId: Int, path: String): Flow<File?>

    @RewriteQueriesToDropUnusedColumns
    @Query("$NEXT_FAVORITE_WITH $NEXT_FAVORITE_SELECT sort_index c_sort_index $NEXT_FAVORITE_WHERE sort_index <= c_sort_index $ORDER_BY sort_index desc $LIMIT_1")
    fun flowNextOrderNameDesc(favoriteId: Int, bookshelfId: Int, path: String): Flow<File?>

    @RewriteQueriesToDropUnusedColumns
    @Query("$NEXT_FAVORITE_WITH $NEXT_FAVORITE_SELECT last_modified c_last_modified $NEXT_FAVORITE_WHERE last_modified >= c_last_modified $ORDER_BY last_modified asc $LIMIT_1")
    fun flowNextOrderLastModifiedAsc(favoriteId: Int, bookshelfId: Int, path: String): Flow<File?>

    @RewriteQueriesToDropUnusedColumns
    @Query("$NEXT_FAVORITE_WITH $NEXT_FAVORITE_SELECT last_modified c_last_modified $NEXT_FAVORITE_WHERE last_modified <= c_last_modified $ORDER_BY last_modified desc $LIMIT_1")
    fun flowNextOrderLastModifiedDesc(favoriteId: Int, bookshelfId: Int, path: String): Flow<File?>

    @RewriteQueriesToDropUnusedColumns
    @Query("$NEXT_FAVORITE_WITH $NEXT_FAVORITE_SELECT size c_size $NEXT_FAVORITE_WHERE size >= c_size $ORDER_BY size asc $LIMIT_1")
    fun flowNextOrderSizeAsc(favoriteId: Int, bookshelfId: Int, path: String): Flow<File?>

    @RewriteQueriesToDropUnusedColumns
    @Query("$NEXT_FAVORITE_WITH $NEXT_FAVORITE_SELECT size c_size $NEXT_FAVORITE_WHERE size <= c_size $ORDER_BY size desc $LIMIT_1")
    fun flowNextOrderSizeDesc(favoriteId: Int, bookshelfId: Int, path: String): Flow<File?>

}

private const val NEXT_FAVORITE_WITH =
    "WITH tmp as (SELECT file.* FROM favorite_file INNER JOIN file ON favorite_file.favorite_id = :favoriteId AND favorite_file.bookshelf_id = file.bookshelf_id AND favorite_file.file_path = file.path) "

private const val NEXT_FAVORITE_SELECT = " SELECT * FROM tmp, (SELECT path c_path, "
private const val NEXT_FAVORITE_WHERE = """
    FROM tmp WHERE tmp.bookshelf_id = :bookshelfId AND tmp.path = :path)
    WHERE
      file_type != 'FOLDER'
      AND path != c_path
      AND 
"""
