package com.sorrowblue.comicviewer.data.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.sorrowblue.comicviewer.data.database.entity.favorite.FavoriteEntity
import com.sorrowblue.comicviewer.data.database.entity.favorite.QueryFavoriteFileWithCountEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface FavoriteDao {

    @Upsert
    suspend fun upsert(favoriteEntity: FavoriteEntity): Long

    @Delete
    suspend fun delete(favoriteEntity: FavoriteEntity): Int

    @Query(
        "SELECT *, (SELECT COUNT(*) FROM favorite_file WHERE favorite_id = :favoriteId) AS count, 0 exist FROM favorite WHERE id = :favoriteId"
    )
    fun flow(favoriteId: Int): Flow<QueryFavoriteFileWithCountEntity?>

    @Query(
        "SELECT *, (SELECT COUNT(*) FROM favorite_file WHERE id = favorite_id) count, EXISTS(SELECT file_path FROM favorite_file WHERE id = favorite_id AND bookshelf_id = :bookshelfId AND file_path = :path) exist FROM favorite"
    )
    fun pagingSourceFileOnFolder(
        bookshelfId: Int,
        path: String,
    ): PagingSource<Int, QueryFavoriteFileWithCountEntity>

    @Query(
        "SELECT *, (SELECT COUNT(*) FROM favorite_file WHERE id = favorite_id) count, EXISTS(SELECT file_path FROM favorite_file WHERE id = favorite_id AND bookshelf_id = :bookshelfId AND file_path = :path) exist FROM favorite ORDER BY added_date_time DESC"
    )
    fun pagingSourceRecent(
        bookshelfId: Int,
        path: String,
    ): PagingSource<Int, QueryFavoriteFileWithCountEntity>
}
