package com.sorrowblue.comicviewer.data.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Upsert
import androidx.sqlite.db.SupportSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQueryBuilder
import com.sorrowblue.comicviewer.data.common.SortType
import com.sorrowblue.comicviewer.data.database.entity.Favorite
import com.sorrowblue.comicviewer.data.database.entity.FavoriteAndBookCount
import com.sorrowblue.comicviewer.data.database.entity.FavoriteBook
import com.sorrowblue.comicviewer.data.database.entity.File

@Dao
internal interface FavoriteDao {

    @Upsert
    suspend fun upsert(favorite: Favorite): Long

    @Delete
    suspend fun delete(favorite: Favorite)

    @Query("SELECT *, (SELECT COUNT(*) FROM favorite_book WHERE favorite_id = :favoriteId) AS count FROM favorite WHERE id = :favoriteId")
    suspend fun findBy(favoriteId: Int): FavoriteAndBookCount?

    @Query("SELECT favorite.* FROM favorite_book INNER JOIN favorite ON favorite.id = favorite_book.favorite_id WHERE favorite_book.server_id = :serverId AND favorite_book.file_path = :filePath")
    fun selectBy(serverId: Int, filePath: String): List<Favorite>

    @Query("SELECT * FROM favorite")
    fun pagingSource(): PagingSource<Int, Favorite>

    @Query("SELECT *, (SELECT COUNT(*) FROM favorite_book WHERE favorite_id = id) AS count FROM favorite")
    fun pagingSourceCount(): PagingSource<Int, FavoriteAndBookCount>
}

@Dao
internal interface FavoriteBookDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(favoriteBook: FavoriteBook): Long

    @Delete
    suspend fun delete(favoriteBook: FavoriteBook)

    @RawQuery(observedEntities = [FavoriteBook::class, File::class])
    fun pagingSource(query: SupportSQLiteQuery): PagingSource<Int, File>

    @Query("SELECT cache_key FROM favorite_book INNER JOIN file ON favorite_book.server_id == file.server_id AND favorite_book.file_path == file.path WHERE favorite_id = :favoriteId AND file_type != 'FOLDER' AND cache_key != '' LIMIT :limit")
    suspend fun selectCacheKey(favoriteId: Int, limit: Int): List<String>
}

/**
 * SELECT file.* FROM favorite_book INNER JOIN file ON favorite_book.server_id = file.server_id AND favorite_book.file_path = file.path WHERE favorite_id = :favoriteId
 *
 * @param favoriteId
 * @param sortType
 * @return
 */
internal fun FavoriteBookDao.pagingSource(
    favoriteId: Int, sortType: SortType
): PagingSource<Int, File> {
    val query =
        SupportSQLiteQueryBuilder.builder("favorite_book INNER JOIN file ON favorite_book.server_id = file.server_id AND favorite_book.file_path = file.path")
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
    @Suppress("DEPRECATION")
    return pagingSource(query)
}
