package com.sorrowblue.comicviewer.data.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQueryBuilder
import com.sorrowblue.comicviewer.data.common.bookshelf.SortType
import com.sorrowblue.comicviewer.data.database.entity.FavoriteFile
import com.sorrowblue.comicviewer.data.database.entity.File

@Dao
internal interface FavoriteFileDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(favoriteFile: FavoriteFile): Long

    @Delete
    suspend fun delete(favoriteFile: FavoriteFile)

    @RawQuery(observedEntities = [FavoriteFile::class, File::class])
    fun pagingSource(query: SupportSQLiteQuery): PagingSource<Int, File>

    @Query("SELECT cache_key FROM favorite_file INNER JOIN file ON favorite_file.bookshelf_id == file.bookshelf_id AND favorite_file.file_path == file.path WHERE favorite_id = :favoriteId AND file_type != 'FOLDER' AND cache_key != '' LIMIT :limit")
    suspend fun selectCacheKey(favoriteId: Int, limit: Int): List<String>

    fun pagingSource(favoriteId: Int, sortType: SortType): PagingSource<Int, File> {
        val query =
            SupportSQLiteQueryBuilder.builder("favorite_file INNER JOIN file ON favorite_file.bookshelf_id = file.bookshelf_id AND favorite_file.file_path = file.path")
                .apply {
                    columns(arrayOf("file.*"))
                    selection(
                        "favorite_id = :favoriteId",
                        arrayOf(favoriteId)
                    )
                    when (sortType) {
                        is SortType.NAME -> if (sortType.isAsc) "file_type_order, sort_index" else "file_type_order DESC, sort_index DESC"
                        is SortType.DATE -> if (sortType.isAsc) "file_type_order, last_modified, sort_index" else "file_type_order DESC, last_modified DESC, sort_index DESC"
                        is SortType.SIZE -> if (sortType.isAsc) "file_type_order, size, sort_index" else "file_type_order DESC, size DESC, sort_index DESC"
                    }.let(::orderBy)
                }.create()
        return pagingSource(query)
    }
}
